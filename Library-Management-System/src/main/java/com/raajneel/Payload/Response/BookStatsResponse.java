package com.raajneel.Payload.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Aggregated catalog inventory statistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStatsResponse {

    @Schema(description = "Total number of active, non-deleted book titles in library catalog", example = "1250", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalActiveBooks;

    @Schema(description = "Total count of physical copies currently sitting on shelves available for checkout", example = "3420", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalAvailableBooks;
}
