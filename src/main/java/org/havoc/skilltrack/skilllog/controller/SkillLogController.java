package org.havoc.skilltrack.skilllog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.external.github.dto.GitHubEventResponse;
import org.havoc.skilltrack.external.leetcode.dto.LeetCodeSubmissionResponse;
import org.havoc.skilltrack.external.github.service.GitHubActivitySyncService;
import org.havoc.skilltrack.external.leetcode.service.LeetCodeSyncService;
import org.havoc.skilltrack.skilllog.dto.SkillLogRequest;
import org.havoc.skilltrack.skilllog.dto.SkillLogResponse;
import org.havoc.skilltrack.skilllog.service.SkillLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/skilllog")
@RequiredArgsConstructor
public class SkillLogController {

    private final SkillLogService skillLogService;

    private final GitHubActivitySyncService gitHubActivitySyncService;

    private final LeetCodeSyncService leetCodeSyncService;


    @PostMapping
    public ResponseEntity<SkillLogResponse> createSkillLog(@Valid @RequestBody SkillLogRequest request) {
        log.info("Received SkillLog create request: {}", request);
        SkillLogResponse response = skillLogService.createSkillLog(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/sync/github")
    public ResponseEntity<List<GitHubEventResponse>> syncGitHubEvents() {
        log.info("Received request to sync GitHub commits");

        List<GitHubEventResponse> events = gitHubActivitySyncService.syncCommitsForCurrentUser();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/sync/leetcode")
    public ResponseEntity<List<LeetCodeSubmissionResponse>> syncLeetCodeSubmissions() {
        log.info("Received request to sync Leetcode submissions");

        List<LeetCodeSubmissionResponse> submissions = leetCodeSyncService.syncLeetCodeSubmissionForCurrentUser();

        return ResponseEntity.ok(submissions);
    }
}
