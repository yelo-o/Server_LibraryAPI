package com.book.api.controllers;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;
import com.book.api.models.BookType;
import com.book.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    //http://localhost:8080/api/book?pageNo=1&pageSize=5
    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<PageResponse<?>> getBooks(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return new ResponseEntity<>(bookService.getAllBook(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookDto> bookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

//    @PostMapping("/register")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto, @PathVariable("id") Long bookId){
        BookDto response = bookService.updateBookById(bookDto, bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable("id") Long bookId) {
        bookService.deleteBookById(bookId);
        return new ResponseEntity<>("Book deleted", HttpStatus.OK);
    }

    @GetMapping("/booktypes")
    public ResponseEntity<List<String>> getBookTypes() {
        return new ResponseEntity<>(BookType.nameList(), HttpStatus.OK);
    }

    //책 대여
    @PostMapping("/borrow/{id}")
    public ResponseEntity<String> borrowBook(@PathVariable("id") Long bookId,
                                             Authentication authentication) {
        String username = authentication.getName();
        String message;
        if (bookService.createBorrowHistory(bookId, username)) {
            message = "책 대여가 완료되었습니다.";
        } else {
            message = "대여가 불가능합니다.";
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    //책 반납
    @PostMapping("/return/{id}")
    public ResponseEntity<String> returnBook(@PathVariable("id") Long bookId,
                                             Authentication authentication) {
        String username = authentication.getName();
        String message;
        if (bookService.createReturnHistory(bookId, username)) {
            message = "책 반납이 완료되었습니다.";
        } else {
            message = "반납할 수 없습니다.";
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
