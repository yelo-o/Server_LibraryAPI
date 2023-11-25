package com.book.api.repository;

import com.book.api.models.Book;
import com.book.api.models.BookStatus;
import com.book.api.models.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByStatus(BookStatus status);
}
