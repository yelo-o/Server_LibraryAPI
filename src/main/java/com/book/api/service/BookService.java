package com.book.api.service;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    PageResponse<?> getAllBook(int pageNo, int pageSize);

    BookDto getBookById(Long id);

    BookDto updateBookById(BookDto bookDto, Long id);

    void deleteBookById(Long id);
}
