package org.havoc.skilltrack.skilllog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderBoardEntryDTO {
    private String username;
    private Long score;
}
