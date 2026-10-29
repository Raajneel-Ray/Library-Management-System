package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for an administrator to forgive and waive an assessed fine")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaiveFineRequest {

    @NotNull(message = "Fine Id is mandatory")
    @Schema(description = "Identifier of the fine to waive", example = "201", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long fineId;

    @NotBlank(message = "Waive Reason is mandatory")
    @Schema(description = "Administrative explanation or policy reason for granting the fine waiver", example = "System outage prevented on-time return; verified and approved", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}
