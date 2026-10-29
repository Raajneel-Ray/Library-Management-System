package com.raajneel.Payload.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Standard operational feedback response wrapper")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    @Schema(description = "Human-readable status or error description message", example = "Operation completed successfully", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Flag indicating success (true) or failure (false)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean status;
}
