package com.book.api.service;

import com.book.api.dto.HistoryDto;
import com.book.api.dto.PageResponse;

public interface HisotryService {
    HistoryDto createHistory(HistoryDto historyDto);

    PageResponse<?> getAllHistory(int pageNo, int pageSize);

    HistoryDto getHistoryById(Long id);


}
