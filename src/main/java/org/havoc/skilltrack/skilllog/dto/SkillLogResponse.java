package org.havoc.skilltrack.skilllog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class SkillLogResponse {
    private String id;
    private Long userId;
    private String type;
    private String source;
    private int count;
    private LocalDate date;
    private ZonedDateTime createdAt;
    private List<String> tags;
}
