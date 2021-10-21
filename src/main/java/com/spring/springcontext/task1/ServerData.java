package com.spring.springcontext.task1;

import java.net.URI;

public record ServerData(URI uri, int port, String protocol, String body) {
    public ServerData(URI uri, int port) {
        this(uri, port, "HTTP/1.0", "");
    }

    public ServerData(URI uri, int port, String body) {
        this(uri, port, "HTTP/1.0", body);
    }

    @Override
    public String toString() {
        return "ServerData: uri = %s, port = %s, protocol = %s, body = %s"
                .formatted(uri.toString(), port, protocol, body);
    }
}
