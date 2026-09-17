package com.raajneel.Payload.Response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStatsResponse {
    private long totalActiveBooks;
    private long totalAvailableBooks;
}
