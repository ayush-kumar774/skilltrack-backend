package org.havoc.skilltrack.skilllog.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SkillLogDashboardResponse {

    private List<SkillLogResponse> logs;

    // Aggregations
    private Map<String, Integer> totalCountPerType;    // e.g., DSA -> 30, Backend -> 20
    private int totalLogs;                             // e.g., 50
    private int last7DaysCount;                        // e.g., 15
}
