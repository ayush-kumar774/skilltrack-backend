package org.havoc.skilltrack.skilllog.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.enums.GitHubEventType;
import org.havoc.skilltrack.external.github.dto.GitHubEventResponse;
import org.havoc.skilltrack.external.leetcode.dto.LeetCodeSubmissionResponse;
import org.havoc.skilltrack.skilllog.dto.SkillLogRequest;
import org.havoc.skilltrack.skilllog.dto.SkillLogResponse;
import org.havoc.skilltrack.skilllog.entity.SkillLog;
import org.havoc.skilltrack.skilllog.repository.SkillLogRepository;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillLogService {

    private final UserRepository userRepository;
    private final SkillLogRepository skillLogRepository;

    public SkillLogResponse createSkillLog(@Valid SkillLogRequest request) {
        log.info("📥 Incoming SkillLog request: {}", request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.debug("🔐 Authenticated user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        log.debug("👤 User found: ID={}, Username={}, Email={}", user.getId(), user.getUsername(), user.getEmail());

        SkillLog skillLog = SkillLog.builder()
                .userId(user.getId())
                .type(request.getType())
                .source(request.getSource())
                .count(request.getCount())
                .date(request.getDate())
                .tags(request.getTags())
                .build();

        skillLogRepository.save(skillLog);
        log.info("✅ SkillLog created: ID={}, Type={}, Source={}, Date={}", skillLog.getId(), skillLog.getType(), skillLog.getSource(), skillLog.getDate());

        return SkillLogResponse.builder()
                .id(skillLog.getId().toString())
                .userId(skillLog.getUserId())
                .type(skillLog.getType())
                .source(skillLog.getSource())
                .count(skillLog.getCount())
                .date(skillLog.getDate())
                .tags(skillLog.getTags())
                .build();
    }

    public void logGitHubActivity(List<GitHubEventResponse> events, Long userId) {
        if (events.isEmpty()) {
            log.warn("⚠️ No GitHub events to process for userId {}", userId);
            return;
        }

        log.info("🔄 Processing {} GitHub events for userId {}", events.size(), userId);

        List<SkillLog> logs = events.stream()
                .filter(event -> GitHubEventType.isLoggable(event.getType()))
                .flatMap(event ->
                        GitHubEventType.from(event.getType()).map(githubType -> {
                            LocalDate date = event.getCreated_at().toLocalDate();

                            boolean alreadyLogged = skillLogRepository.existsByUserIdAndTypeAndSourceAndDate(
                                    userId, githubType.name(), "GITHUB", date);

                            if (alreadyLogged) {
                                log.debug("⏭️ Skipping duplicate GitHub event: type={}, date={}, userId={}", githubType.name(), date, userId);
                                return Stream.<SkillLog>empty();
                            }

                            int commitCount = event.getPayload() != null ? event.getPayload().getCommits().size() : 1;

                            SkillLog logEntry = SkillLog.builder()
                                    .userId(userId)
                                    .type(githubType.name())
                                    .source("GITHUB")
                                    .count(commitCount)
                                    .date(date)
                                    .tags(List.of("GitHub"))
                                    .build();

                            log.debug("📝 Logging GitHub activity: {}", logEntry);
                            return Stream.of(logEntry);
                        }).orElseGet(() -> {
                            log.warn("❓ Unknown GitHub event type: {}", event.getType());
                            return Stream.empty();
                        })
                ).toList();

        skillLogRepository.saveAll(logs);
        log.info("✅ Saved {} GitHub activity logs for userId {}", logs.size(), userId);
    }

    public void logLeetCodeActivity(List<LeetCodeSubmissionResponse> submissions, Long userId) {
        if (submissions.isEmpty()) {
            log.warn("⚠️ No LeetCode submissions to log for userId {}", userId);
            return;
        }

        log.info("🔄 Processing {} LeetCode submissions for userId {}", submissions.size(), userId);

        List<SkillLog> logs = submissions.stream()
                .map(submission -> {
                    LocalDate date = Instant.ofEpochSecond(Long.parseLong(submission.getTimestamp()))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    boolean alreadyLogged = skillLogRepository.existsByUserIdAndTypeAndSourceAndDate(
                            userId, "DSA", "LEETCODE", date);

                    if (alreadyLogged) {
                        log.debug("⏭️ Skipping duplicate LeetCode log for userId={}, date={}", userId, date);
                        return null;
                    }

                    List<String> tags = Stream.of(submission.getLang(), submission.getStatusDisplay())
                            .filter(s -> s != null && !s.isBlank())
                            .toList();

                    SkillLog logEntry = SkillLog.builder()
                            .userId(userId)
                            .type("DSA")
                            .source("LEETCODE")
                            .count(1)
                            .date(date)
                            .tags(tags.isEmpty() ? List.of("Unknown") : tags)
                            .build();

                    log.debug("📝 Logging LeetCode submission: {}", logEntry.toString());
                    return logEntry;
                })
                .filter(Objects::nonNull)
                .toList();

        skillLogRepository.saveAll(logs);
        log.info("✅ Saved {} LeetCode activity logs for userId {}", logs.size(), userId);
    }
}