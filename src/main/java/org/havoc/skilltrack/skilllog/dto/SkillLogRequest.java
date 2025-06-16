package org.havoc.skilltrack.skilllog.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SkillLogRequest {

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Source is required")
    private String source; // MANUAL, GITHUB, LEETCODE

    @NotNull(message = "Count is required")
    private Integer count; // number of problems, commits, etc.

    @NotNull(message = "Date is required")
    private LocalDate date; // activity date

    private List<String> tags; // ["Graph", "Arrays", "Spring"]

}
