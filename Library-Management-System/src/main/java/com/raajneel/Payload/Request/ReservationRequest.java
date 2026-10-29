package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for reserving an unavailable book")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequest {

    @NotNull(message = "Book ID is mandatory")
    @Schema(description = "Identifier of the book to reserve", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Schema(description = "Special pickup preferences or patron hold notes", example = "Hold for pickup at East Campus branch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
