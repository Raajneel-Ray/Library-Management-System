package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for adding a book to patron wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListRequest {

    @NotNull(message = "Book ID is required")
    @Schema(description = "Identifier of the book to add to wishlist", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Personal reading plan remarks or priority notes", example = "Recommended for upcoming software architecture exam", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
