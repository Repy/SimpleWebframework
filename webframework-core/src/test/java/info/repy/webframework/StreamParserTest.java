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
            reqs.addAll(streamString(st, "GET /path/ HTTP1"));
            reqs.addAll(streamString(st, ".1"));
            reqs.addAll(streamString(st, "\r"));
            reqs.addAll(streamString(st, "\n"));
            reqs.addAll(streamString(st, "AAA:111\r\n"));
            reqs.addAll(streamString(st, "AAA: 222   \n"));
            reqs.addAll(streamString(st, "BBB:333\r"));
            reqs.addAll(streamString(st, "AAA:             444  \r"));
            assert reqs.size() == 0;
            reqs.addAll(streamString(st, "\r"));
            assert reqs.size() == 1;
            reqs.addAll(streamString(st, "\n"));
            assert reqs.size() == 1;
            assert Objects.equals(reqs.get(0).getMethod(), "GET");
            assert Objects.equals(reqs.get(0).getPath(), "/path/");
            assert Objects.equals(reqs.get(0).getHttpVersion(), "HTTP1.1");
            assert Objects.equals(reqs.get(0).getHeader("BBB")[0], "333");
            assert Objects.equals(reqs.get(0).getHeader("AAA")[0], "111");
            assert Objects.equals(reqs.get(0).getHeader("AAA")[1], "222   ");
            assert Objects.equals(reqs.get(0).getHeader("AAA")[2], "444  ");

            reqs.clear();
            reqs.addAll(streamString(st, "GET / HTTP1.1\nAAA:111\nAAA: 222\nBBB:333\nAAA:444\n\n"));
            assert reqs.size() == 1;
            assert Objects.equals(reqs.get(0).getMethod(), "GET");

            reqs.clear();
            reqs.addAll(streamString(st, "GET / HTTP1.1\rAAA:111\rAAA: 222\rBBB:333\rAAA:444\r\r"));
            assert reqs.size() == 1;
            assert Objects.equals(reqs.get(0).getMethod(), "GET");

            reqs.clear();
            reqs.addAll(streamString(st, "GET / HTTP1.1\r\nAAA:111\r\nAAA: 222\r\nBBB:333\r\nAAA:444\r\n\r\nGET / HTTP1.1\r\nAAA:111\r\nAAA: 222\r\nBBB:333\r\nAAA:444\r\n\r\n"));
            assert reqs.size() == 2;
            assert Objects.equals(reqs.get(0).getMethod(), "GET");
            assert Objects.equals(reqs.get(1).getMethod(), "GET");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\r\nabc"));
            assert reqs.size() == 1;
            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r"));
            assert reqs.size() == 0;
            reqs.addAll(streamString(st, "\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\n\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\r\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\r\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\rabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");

            reqs.clear();
            reqs.addAll(streamString(st, "POST / HTTP1.1\r\nContent-lenGth:3\n\nabc"));
            assert reqs.size() == 1;
            assert Objects.equals(new String(reqs.get(0).getBody()), "abc");
            assert Objects.equals(reqs.get(0).getMethod(), "POST");
        } catch (Exception e){
            throw new AssertionError();
        }
    }

    @Test
    public void testQuery() throws IOException {
        StreamParser st = new StreamParser();
        Request req;

        req = streamString(st, "GET /path/ HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert req.getQueryString().size() == 0;

        req = streamString(st, "GET /path/? HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert req.getQueryString().size() == 1;
        assert req.getQueryString().containsKey("");

        req = streamString(st, "GET /path/?aa=bb HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert Objects.equals(req.getQueryString().get("aa").get(0), "bb");

        req = streamString(st, "GET /path/?aa=bb1&aa=bb2&aa=bb3 HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert Objects.equals(req.getQueryString().get("aa").get(0), "bb1");
        assert Objects.equals(req.getQueryString().get("aa").get(1), "bb2");
        assert Objects.equals(req.getQueryString().get("aa").get(2), "bb3");

        req = streamString(st, "GET /path/?aa=bb1&aa=bb2&aa=bb3 HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert Objects.equals(req.getQueryString().get("aa").get(0), "bb1");
        assert Objects.equals(req.getQueryString().get("aa").get(1), "bb2");
        assert Objects.equals(req.getQueryString().get("aa").get(2), "bb3");

        req = streamString(st, "GET /path/?aa=&aa&aa=bb3 HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert Objects.equals(req.getQueryString().get("aa").get(0), "");
        assert Objects.equals(req.getQueryString().get("aa").get(1), "");
        assert Objects.equals(req.getQueryString().get("aa").get(2), "bb3");

        req = streamString(st, "GET /path/?aa=&&aa=?&aa=bb3 HTTP1.1\r\nAAA:111\r\n\r\n").get(0);
        assert Objects.equals(req.getQueryString().get("aa").get(0), "");
        assert Objects.equals(req.getQueryString().get("aa").get(1), "?");
        assert Objects.equals(req.getQueryString().get("aa").get(2), "bb3");

    }

    public List<Request> streamString(StreamParser st, String str) throws IOException {
        byte[] strby = str.getBytes(StandardCharsets.UTF_8);
        return st.input(strby, strby.length);
    }
}
