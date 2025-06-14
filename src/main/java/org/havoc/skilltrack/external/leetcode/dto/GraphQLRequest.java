package org.havoc.skilltrack.external.leetcode.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GraphQLRequest {
    private String query;
    private Variables variables;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Variables {
        private String username;
    }
}
