package com.raajneel.Payload.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookCoverImageUrl;
    private Integer availableCopies;
    private Boolean isBookAvailable;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
