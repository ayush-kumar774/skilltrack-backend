package org.havoc.skilltrack.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

    @Schema(description = "A human-readable success or info message", example = "Password updated successfully")
    private String message;
}
