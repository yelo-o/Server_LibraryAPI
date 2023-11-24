package com.book.api.config;

import com.book.api.models.Book;
import com.book.api.models.BookStatus;
import com.book.api.models.BookType;
import com.book.api.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
//@Profile("test")
public class AppConfig {
    @Bean
    public CommandLineRunner test(BookRepository bookRepository) {
        return args -> {
            System.out.println("**** book Insert 시작");
            List<Book> bookList = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> Book.builder()
                            .title("Effective Java version " + i + ".0")
                            .publisher("No." + i + " publishing company")
                            .author("author " + i)
                            .status(BookStatus.AVAILABLE)
                            .copies(10)
                            .copiesAvailable(5)
                            .type(BookType.CLASSIC)
                            .build())
                    .collect(Collectors.toList());
            bookRepository.saveAll(bookList);
            System.out.println("**** book Insert 끝");
        };
    }
}
