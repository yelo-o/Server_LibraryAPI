package com.book.api.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BookType {
    ADVENTURE("ADVENTURE"), CLASSIC("CLASSIC"), FANTASY("FANTASY");

    private final String name;

    BookType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public static List<String> nameList() {
        BookType[] bookTypes = BookType.values();
        return Arrays.stream(bookTypes)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
