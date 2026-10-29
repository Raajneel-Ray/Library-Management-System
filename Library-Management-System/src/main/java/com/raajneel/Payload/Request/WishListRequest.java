package com.raajneel.Payload.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
