package com.raajneel.Payload.Request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookSearchRequest {

    private String searchTerm;
    private Long genreId;
    private Boolean availableOnly;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
