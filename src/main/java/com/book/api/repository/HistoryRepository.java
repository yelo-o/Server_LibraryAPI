package com.book.api.repository;

import com.book.api.models.Book;
import com.book.api.models.History;
import com.book.api.models.security.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    //select * from history where user_id=1 AND book_id=1 ORDER BY created_date desc LIMIT 1;
    Optional<History> findFirstHistoryByUserAndBookOrderByCreatedDateDesc(UserEntity user, Book book);

}
