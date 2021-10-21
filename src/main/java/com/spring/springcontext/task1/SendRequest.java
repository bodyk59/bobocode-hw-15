package com.spring.springcontext.task1;

import java.util.Comparator;

import static com.spring.springcontext.task1.Utils.*;

public class SendRequest {
    private static final String NASA_URL_12 = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=12&api_key=DEMO_KEY";
    private static final String BOBOCODE = "https://bobocode.herokuapp.com/nasa/pictures";

    public static void main(String[] args) {
        var nasaUri = createURI(NASA_URL_12);
        var bobocodeUri = createURI(BOBOCODE);
        var json = workWithSocket(nasaUri, ((writer, reader) -> {
            GET(new ServerData(nasaUri, 443), writer);
            return getBody(reader);
        }));
        var picture = getURIs(json).stream()
                .map(link -> workWithSocket(link, ((writer, reader) -> {
                    HEAD(new ServerData(link, 80), writer);
                    var newLink = createURI(getHeadersValue("Location", reader));
                    var length = workWithSocket(newLink, (w, r) -> {
                        HEAD(new ServerData(newLink, 443), w);
                        return getHeadersValue("Content-Length", r);
                    });
                    return new Picture(newLink, link, Integer.valueOf(length));
                })))
                .peek(System.out::println)
                .max(Comparator.comparing(Picture::length))
                .orElseThrow();
        var response = workWithSocket(bobocodeUri, ((writer, reader) -> {
            var body = createJson(picture, new User("Bohdan", "Kurchak"));
            POST(new ServerData(bobocodeUri, 443, "HTTP/1.1", body), writer);
            GET(new ServerData(bobocodeUri, 443), writer);
            return getResponse(reader);
        }));
    }
}
