package com.raajneel.Payload.Request;

import com.raajneel.Domain.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Criteria parameters used for searching and filtering book reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationSearchRequest {

    @Schema(description = "Filter by patron user ID", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Filter by target book ID", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookId;

    @Schema(description = "Filter by reservation lifecycle status", allowableValues = {"PENDING", "AVAILABLE", "FULFILLED", "CANCELLED", "EXPIRED"}, example = "PENDING", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ReservationStatus status;

    @Schema(description = "When true, limits results to currently active holds (PENDING or AVAILABLE)", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean activeOnly;

    @Builder.Default
    @Schema(description = "Zero-indexed page number for pagination", example = "0", defaultValue = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int page = 0;

    @Builder.Default
    @Schema(description = "Page size limit", example = "20", defaultValue = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int size = 20;

    @Builder.Default
    @Schema(description = "Entity attribute to sort reservations by", example = "reservedAt", defaultValue = "reservedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortBy = "reservedAt";

    @Builder.Default
    @Schema(description = "Sort ordering direction", allowableValues = {"ASC", "DESC"}, example = "DESC", defaultValue = "DESC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortDirection = "DESC";
}
