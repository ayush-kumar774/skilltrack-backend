package org.havoc.skilltrack.external.leetcode.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecentSubmissionsData {
    private List<LeetCodeSubmissionResponse> recentSubmissionList;
}
