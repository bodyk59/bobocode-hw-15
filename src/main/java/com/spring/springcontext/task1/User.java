package com.spring.springcontext.task1;

public record User(String firstName, String lastName) {
    @Override
    public String toString() {
        return "User: firstName = %s, lastName = %s"
                .formatted(firstName, lastName);
    }
}
