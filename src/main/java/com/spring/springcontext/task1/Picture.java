package com.spring.springcontext.task1;

import java.net.URI;

public record Picture(URI uriNew, URI uriOld, Integer length) {
    @Override
    public String toString() {
        return "Picture: uriNew = %s, uriOld = %s, length = %s"
                .formatted(uriNew, uriOld, length);
    }
}
