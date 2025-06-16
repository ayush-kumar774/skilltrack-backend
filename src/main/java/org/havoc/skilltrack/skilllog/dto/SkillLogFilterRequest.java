package org.havoc.skilltrack.skilllog.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SkillLogFilterRequest {
    private String type;
    private String source;
    private LocalDate from;
    private LocalDate to;
    private List<String> tags;
}
