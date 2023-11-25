package com.book.api.dto;

import com.book.api.models.BookStatus;
import com.book.api.models.HistoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryDto {
    private Long id;
//    private BookStatus status;
    private HistoryType type;
}
