package com.raajneel.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaiveFineRequest {
    @NotNull(message = "Fine Id is mandatory")
    private Long fineId;

    @NotBlank(message = "Waive Reason is mandatory")
    private String reason;
}
