package com.book.api.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HistoryType {
    BORROW("BORROW"), RETURN("RETURN");

    private final String name;

    HistoryType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public static List<String> nameList() {
        HistoryType[] bookTypes = HistoryType.values();
        return Arrays.stream(bookTypes)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
