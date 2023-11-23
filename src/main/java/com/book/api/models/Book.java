package com.book.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;

    private String author;

    private String publisher;

    @Enumerated(EnumType.STRING)
    private BookType type;

    private int copies; //total cnt
    private int copiesAvailable; //available cnt

    private BookStatus status; //AVAILABLE, VACANT

}
