package com.book.api.service;

import com.book.api.dto.BookDto;
import com.book.api.dto.HistoryDto;
import com.book.api.dto.PageResponse;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    boolean createBorrowHistory(Long id, String username);
    boolean createReturnHistory(Long id, String username);
    PageResponse<?> getAllBook(int pageNo, int pageSize);

    BookDto getBookById(Long id);

    BookDto updateBookById(BookDto bookDto, Long id);
    boolean updateHistoryById(Long id);

    void deleteBookById(Long id);
}
