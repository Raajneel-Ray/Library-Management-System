package com.raajneel.Payload.Response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Generic paginated payload envelope wrapping list results and page metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    @ArraySchema(
            arraySchema = @Schema(description = "List of domain records contained in current page slice", requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(description = "Page element item")
    )
    private List<T> content;

    @Schema(description = "Current page index (0-indexed)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageNumber;

    @Schema(description = "Requested page size limit", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize;

    @Schema(description = "Total number of elements matching the query criteria across all pages", example = "85", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalElements;

    @Schema(description = "Total number of pages available", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalPages;

    @Schema(description = "Flag indicating whether current page is the final page", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean last;

    @Schema(description = "Flag indicating whether current page is the first page", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean first;

    @Schema(description = "Flag indicating whether current page contains zero elements", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean empty;
}
