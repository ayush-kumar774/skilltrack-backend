package org.havoc.skilltrack.external.leetcode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeetCodeSubmissionResponse {
    private String title;
    private String titleSlug;
    private String statusDisplay;
    private String timestamp;
    private String lang;
}
