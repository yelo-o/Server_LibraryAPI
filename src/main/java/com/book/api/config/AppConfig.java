package com.book.api.config;

import com.book.api.models.Book;
import com.book.api.models.BookStatus;
import com.book.api.models.BookType;
import com.book.api.models.security.RoleEntity;
import com.book.api.models.security.UserEntity;
import com.book.api.repository.BookRepository;
import com.book.api.repository.security.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
//@Profile("test")
public class AppConfig {
    @Bean
    public CommandLineRunner test(BookRepository bookRepository, UserRepository userRepository
    , PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("**** book Insert 시작");
            List<Book> bookList = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> Book.builder()
                            .title("Effective Java version " + i + ".0")
                            .publisher("No." + i + " publishing company")
                            .author("author " + i)
                            .status(BookStatus.AVAILABLE)
                            .quantity(i)
//                            .copiesAvailable(5)
//                            .type(BookType.CLASSIC)
                            .build())
                    .collect(Collectors.toList());
            bookRepository.saveAll(bookList);
            System.out.println("**** book Insert 끝");

            System.out.println("**** User Insert 시작");
            List<RoleEntity> roles = new ArrayList<>();
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName("ROLE_ADMIN");
            roles.add(roleEntity);
            List<UserEntity> userList = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> UserEntity.builder()
                            .firstName("firstName " + i)
                            .lastName("lastName " + i)
                            .username("admin" + i)
                            .password(passwordEncoder.encode(("123456")))
                            .roles(roles)
                            .build())
                    .collect(Collectors.toList());
            userRepository.saveAll(userList);
            System.out.println("**** User Insert 끝");
        };
    }
}
