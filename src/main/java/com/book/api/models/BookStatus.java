package com.book.api.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookStatus {
    AVAILABLE("AVAILABLE"), VACANT("VACANT");
    private final String name;

    BookStatus(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
