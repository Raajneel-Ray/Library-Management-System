package com.raajneel.Payload.Request;

import com.raajneel.Domain.BookLoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "Criteria parameters used for filtering and searching book loans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanSearchRequest {

    @Schema(description = "Filter by specific user ID", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Filter by specific book ID", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookId;

    @Schema(description = "Filter by loan lifecycle status", allowableValues = {"CHECKED_OUT", "RETURNED", "OVERDUE", "LOST", "DAMAGED"}, example = "CHECKED_OUT", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BookLoanStatus status;

    @Schema(description = "Filter only loans that have passed their due date without being returned", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean overdueOnly;

    @Schema(description = "Filter only loans having unsettled fine balances", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean unpaidFinesOnly;

    @Schema(description = "Start boundary of checkout date range filter", example = "2026-10-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate startDate;

    @Schema(description = "End boundary of checkout date range filter", example = "2026-10-31", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate endDate;

    @Schema(description = "Zero-indexed page number for pagination", example = "0", defaultValue = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer page = 0;

    @Schema(description = "Number of loan records per page (max: 100)", example = "20", defaultValue = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer size = 20;

    @Schema(description = "Entity attribute used for sorting results", example = "createdAt", defaultValue = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortBy = "createdAt";

    @Schema(description = "Sort ordering direction", allowableValues = {"ASC", "DESC"}, example = "DESC", defaultValue = "DESC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortDirection = "DESC";
}
