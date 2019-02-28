package info.repy.webframework;

import java.nio.charset.StandardCharsets;

public class Response {

    public byte[] getBytes() {
        return ("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Cache-Control: private, max-age=0\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 69\r\n" +
                "\r\n" +
                "<form method='post'><input type='submit' name='aa' value='aa'></form>").getBytes(StandardCharsets.UTF_8);
    }
}
