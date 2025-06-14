package org.havoc.skilltrack.external.leetcode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.config.HeaderUtil;
import org.havoc.skilltrack.external.leetcode.dto.GraphQLRequest;
import org.havoc.skilltrack.external.leetcode.dto.GraphQLResponse;
import org.havoc.skilltrack.external.leetcode.dto.LeetCodeSubmissionResponse;
import org.havoc.skilltrack.skilllog.service.SkillLogService;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.havoc.skilltrack.util.HttpSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeetCodeSyncService {

    private final SkillLogService skillLogService;
    private final UserRepository userRepository;
    private final HeaderUtil headerUtil;

    @Value("${external.leetcode.base-url}")
    private String baseUrl;

    public List<LeetCodeSubmissionResponse> syncLeetCodeSubmissionForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("[LeetCodeSync] Attempting to sync LeetCode submissions for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[LeetCodeSync] User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        return getLeetCodeSubmissionResponses(user);
    }

    public void syncLeetCodeSubmissionsForUser(User user) {
        getLeetCodeSubmissionResponses(user);
    }

    private List<LeetCodeSubmissionResponse> getLeetCodeSubmissionResponses(User user) {
        String leetcodeUsername = user.getLeetcodeUsername();
        String email = user.getEmail();
        if (Objects.isNull(leetcodeUsername) || leetcodeUsername.isEmpty()) {
            log.warn("[LeetCodeSync] LeetCode username not linked for user: {}", email);
            throw new IllegalStateException("LeetCode username is not linked to your profile.");
        }

        log.info("[LeetCodeSync] Found LeetCode username: {} for user: {}", leetcodeUsername, user.getUsername());

        String graphqlQuery = """
            query recentSubmissions($username: String!) {
              recentSubmissionList(username: $username) {
                title
                titleSlug
                timestamp
                statusDisplay
                lang
              }
            }
        """;

        GraphQLRequest requestBody = new GraphQLRequest(graphqlQuery, new GraphQLRequest.Variables(leetcodeUsername));
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headerUtil.getHeadersFor("leetcode"));

        log.info("[LeetCodeSync] Sending GraphQL request to LeetCode for username: {}", leetcodeUsername);

        ResponseEntity<GraphQLResponse> response = HttpSender.send(
                this.getClass().getSimpleName(),
                "LeetCodeGraphQL",
                baseUrl.concat("/graphql"),
                HttpMethod.POST,
                entity,
                GraphQLResponse.class,
                true,
                10000
        );

        if (Objects.isNull(response.getBody()) || Objects.isNull(response.getBody().getData())) {
            log.warn("[LeetCodeSync] No response or empty data received from LeetCode for user: {}", leetcodeUsername);
            return List.of();
        }

        List<LeetCodeSubmissionResponse> submissions = response.getBody().getData().getRecentSubmissionList();
        log.info("[LeetCodeSync] Retrieved {} submissions from LeetCode for user: {}", submissions.size(), leetcodeUsername);

        submissions.forEach(submission ->
                log.debug("[LeetCodeSync] Submission: {}", submission)
        );

        skillLogService.logLeetCodeActivity(submissions, user.getId());
        log.info("[LeetCodeSync] Logged {} LeetCode submissions as skill logs for userId: {}", submissions.size(), user.getId());

        return submissions;
    }
}