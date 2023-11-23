package com.book.api.controllers;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;
import com.book.api.models.BookType;
import com.book.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private BookService bookService;

    //http://localhost:8080/api/book?pageNo=1&pageSize=5
    @GetMapping
    public ResponseEntity<PageResponse<?>> getBooks(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize) {
        return new ResponseEntity<>(bookService.getAllBook(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> bookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto, @PathVariable("id") Long bookId){
        BookDto response = bookService.updateBookById(bookDto, bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable("id") Long bookId) {
        bookService.deleteBookById(bookId);
        return new ResponseEntity<>("Book deleted", HttpStatus.OK);
    }

    @GetMapping("booktypes")
    public ResponseEntity<List<String>> getBookTypes() {
        return new ResponseEntity<>(BookType.nameList(), HttpStatus.OK);
    }

}
