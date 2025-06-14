package org.havoc.skilltrack.external.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.config.HeaderUtil;
import org.havoc.skilltrack.external.github.dto.GitHubEventResponse;
import org.havoc.skilltrack.skilllog.service.SkillLogService;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.havoc.skilltrack.util.HttpSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubActivitySyncService {

    private final SkillLogService skillLogService;
    private final UserRepository userRepository;
    private final HeaderUtil headerUtil;

    @Value("${external.github.base-url}")
    private String baseUrl;

    public List<GitHubEventResponse> syncCommitsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("[GitHubSync] Starting sync for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[GitHubSync] User not found: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        return getGitHubEventResponses(user);
    }

    private List<GitHubEventResponse> getGitHubEventResponses(User user) {
        String githubUsername = user.getGithubUsername();
        String email = user.getEmail();
        if (Objects.isNull(githubUsername) || githubUsername.isEmpty()) {
            log.warn("[GitHubSync] GitHub username not set for user: {}", email);
            throw new IllegalStateException("GitHub username not linked to your profile");
        }

        String url = String.format("%s/users/%s/events/public", baseUrl, githubUsername);
        HttpEntity<?> requestEntity = new HttpEntity<>(headerUtil.getHeadersFor("github"));

        log.info("[GitHubSync] Sending request to GitHub for username: {}", githubUsername);

        ResponseEntity<List<GitHubEventResponse>> response = HttpSender.send(
                this.getClass().getSimpleName(),
                "GitHubService",
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {},
                true,
                10000
        );

        List<GitHubEventResponse> events = response.getBody();

        if (Objects.isNull(events) || events.isEmpty()) {
            log.warn("[GitHubSync] No events received from GitHub for user: {}", githubUsername);
            return List.of();
        }

        log.info("[GitHubSync] Retrieved {} GitHub events for user: {}", events.size(), githubUsername);

        for (GitHubEventResponse event : events) {
            log.debug("[GitHubSync] Event Type: {}, Repo: {}, Time: {}",
                    event.getType(),
                    event.getRepo() != null ? event.getRepo().getName() : "null",
                    event.getCreated_at());
        }

        skillLogService.logGitHubActivity(events, user.getId());
        log.info("[GitHubSync] Successfully logged GitHub events for user ID: {}", user.getId());

        return events;
    }

    public void syncCommitsForUser(User user) {
        getGitHubEventResponses(user);
    }

}