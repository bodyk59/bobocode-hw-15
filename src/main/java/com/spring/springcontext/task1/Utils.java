package com.spring.springcontext.task1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    @SneakyThrows
    public static <T> T readAndBuild(BufferedReader reader, Class<T> clazz) {
        var json = new StringBuilder();
        var line = reader.readLine();

        while (Objects.nonNull(line)) {
            json.append(line);
            line = reader.readLine();
        }

        return objectMapper.readValue(json.toString(), clazz);
    }

    public static void POST(ServerData data, PrintWriter writer) {
        writer.println("POST %s %s".formatted(data.uri().getPath(), data.protocol()));
        writer.println("Host: %s".formatted(data.uri().getHost()));
        writer.println("Content-Type: application/json");
        writer.println("Content-Length: %d".formatted(data.body().length()));
        writer.println();
        writer.println(data.body());
        writer.flush();
    }

    public static void GET(ServerData data, PrintWriter writer) {
        writer.println("GET %s %s".formatted(data.uri().getPath() + "?" + data.uri().getQuery(), data.protocol()));
        writer.println("Host: %s:%s".formatted(data.uri().getHost(), data.port()));
        writer.println();
        writer.flush();
    }

    public static void HEAD(ServerData data, PrintWriter writer) {
        writer.println("HEAD %s %s".formatted(data.uri().getPath() + "?" + data.uri().getQuery(), data.protocol()));
        writer.println("Host: %s:%s".formatted(data.uri().getHost(), data.port()));
        writer.println();
        writer.flush();
    }

    public static String createJson(Picture picture, User user) {
        return
                """
                {
                  "picture": {
                    "url": "%s",
                    "size": %s
                  },
                  "user": {
                    "firstName": "%s",
                    "lastName": "%s"
                  }
                }
                """.formatted(
                        picture.uriOld().toString(), picture.length(),
                        user.firstName(), user.lastName());
    }

    public static String getResponse(BufferedReader reader) {
        return reader.lines()
                .collect(Collectors.joining("\n"));
    }

    public static String getBody(BufferedReader reader) {
        return getResponse(reader)
                .split("\n{2}")[1];
    }

    @SneakyThrows
    public static List<URI> getURIs(String json) {
        var jsonNode = objectMapper.readTree(json);
        var list = new ArrayList<URI>();
        for (var node : jsonNode.at("/photos")) {
            list.add(new URI(node.get("img_src").asText()));
        }
        return list;
    }


    @SneakyThrows
    public static Socket getSocket(URI uri) {
        return Objects.equals(uri.getScheme(), "https") ?
                SSLSocketFactory.getDefault().createSocket(uri.getHost(), 443) :
                new Socket(uri.getHost(), 80);
    }

    @SneakyThrows
    public static <T> T workWithSocket(URI uri, BiFunction<PrintWriter, BufferedReader, T> biFunction) {
        try (var socket = getSocket(uri);
             var writer = new PrintWriter(socket.getOutputStream());
             var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            return biFunction.apply(writer, reader);
        }
    }

    @SneakyThrows
    public static String getHeadersValue(String header, BufferedReader reader) {
        return reader.lines()
                .filter(s -> s.startsWith(header))
                .map(s -> s.split(":\s")[1])
                .findFirst()
                .orElseThrow();
    }

    public static void printLines(BufferedReader reader) {
        reader.lines().forEach(System.out::println);
    }

    @SneakyThrows
    public static URI createURI(String uri) {
        return new URI(uri);
    }
}
