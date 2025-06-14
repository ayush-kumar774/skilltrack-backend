package org.havoc.skilltrack.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.external.github.service.GitHubActivitySyncService;
import org.havoc.skilltrack.external.leetcode.service.LeetCodeSyncService;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncScheduler {

    private final GitHubActivitySyncService gitHubActivitySyncService;
    private final LeetCodeSyncService leetCodeSyncService;
    private final UserRepository userRepository;

    @Value("${scheduler.sync.cron}")
    private String cronExpr;

    @Scheduled(cron = "${scheduler.sync.cron}")
    public void runSyncJob() {
        log.info("🕛 Midnight Sync Job Started");

        List<User> users = userRepository.findAll();

        users.forEach(user -> {
            try {
                if (Objects.nonNull(user.getGithubUsername()) && !user.getGithubUsername().isBlank()) {
                    gitHubActivitySyncService.syncCommitsForUser(user);
                }
                if (Objects.nonNull(user.getLeetcodeUsername()) && !user.getLeetcodeUsername().isBlank()) {
                    leetCodeSyncService.syncLeetCodeSubmissionsForUser(user);
                }
            } catch (Exception e) {
                log.error("❌ Failed to sync for user: {}", user.getEmail(), e);
            }
        });

        log.info("✅ Midnight Sync Job Completed");
    }
}
