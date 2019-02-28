package info.repy.webframework;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StreamParserTest {
    @Test
    public void streamCrLf() {
        try {
            StreamParser st = new StreamParser();
            List<Request> reqs = new ArrayList<>();
            reqs.addAll(streamString(st, "GET / HTTP1"));
            reqs.addAll(streamString(st, ".1"));
            reqs.addAll(streamString(st, "\r"));
            reqs.addAll(streamString(st, "\n"));
            reqs.addAll(streamString(st, "AAA:111\r\n"));
            reqs.addAll(streamString(st, "AAA: 222\n"));
            reqs.addAll(streamString(st, "BBB:333\r"));
            reqs.addAll(streamString(st, "AAA:             444\r"));
            assert reqs.size() == 0;
            reqs.addAll(streamString(st, "\r"));
            assert reqs.size() == 1;
            reqs.addAll(streamString(st, "\n"));
            assert reqs.size() == 1;
            reqs.addAll(streamString(st, "GET / HTTP1.1\nAAA:111\nAAA: 222\nBBB:333\nAAA:444\n\n"));
            assert reqs.size() == 2;
            reqs.addAll(streamString(st, "GET / HTTP1.1\rAAA:111\rAAA: 222\rBBB:333\rAAA:444\r\r"));
            assert reqs.size() == 3;
            reqs.addAll(streamString(st, "GET / HTTP1.1\r\nAAA:111\r\nAAA: 222\r\nBBB:333\r\nAAA:444\r\n\r\nGET / HTTP1.1\r\nAAA:111\r\nAAA: 222\r\nBBB:333\r\nAAA:444\r\n\r\n"));
            assert reqs.size() == 5;

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\r\nabc"));
            assert reqs.size() == 1;
            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r"));
            assert reqs.size() == 0;
            reqs.addAll(streamString(st, "\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
        } catch (Exception e){
            throw new AssertionError();
        }
    }

    public List<Request> streamString(StreamParser st, String str) throws IOException {
        byte[] strby = str.getBytes(StandardCharsets.UTF_8);
        return st.input(strby, strby.length);
    }
}
