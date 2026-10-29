package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Criteria parameters used for searching and filtering books in catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookSearchRequest {

    @Schema(description = "Keyword search matching against title, author, or ISBN", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String searchTerm;

    @Schema(description = "Filter by specific genre category ID", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long genreId;

    @Schema(description = "When true, returns only books with at least one available copy on shelf", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean availableOnly;

    @Schema(description = "Zero-indexed page number for pagination", example = "0", defaultValue = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer page = 0;

    @Schema(description = "Number of book items per page (max: 100)", example = "20", defaultValue = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer size = 20;

    @Schema(description = "Entity field to sort results by", example = "createdAt", defaultValue = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortBy = "createdAt";

    @Schema(description = "Sort ordering direction", allowableValues = {"ASC", "DESC"}, example = "DESC", defaultValue = "DESC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sortDirection = "DESC";
}
