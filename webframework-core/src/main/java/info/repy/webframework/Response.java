package info.repy.webframework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Response implements AutoCloseable{

    private final int status;

    public Response(int status) {
        this.status = status;
        this.outputStream = new ByteArrayOutputStream();
        this.headers = new HashMap<>();
    }

    private final ByteArrayOutputStream outputStream;

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    private Map<String, List<String>> headers;

    public String[] getHeader(String name) {
        name = name.toLowerCase();
        List<String> val = headers.get(name);
        if (val == null) return null;
        return val.toArray(new String[]{});
    }

    public void addHeader(String name, String value) {
        name = name.toLowerCase();
        List<String> val = headers.get(name);
        if (val == null) {
            val = new ArrayList<>(1);
            headers.put(name, val);
        }
        val.add(value);
    }

    public void removeHeader(String name) {
        name = name.toLowerCase();
        if (headers.containsKey(name)) headers.remove(name);
    }

    public byte[] getHeaderBytes() throws IOException {
        outputStream.close();
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ");
        switch (status) {
            case 200:
                sb.append("200 OK");
                break;
            case 2:
                break;
            default:
                break;
        }
        sb.append("\r\n");
        if (this.headers.containsKey("content-length")) {
            this.removeHeader("content-length");
        }
        this.addHeader("content-length", Integer.toString(outputStream.size()));
        for (String key : headers.keySet()) {
            List<String> vals = headers.get(key);
            for (String val : vals) {
                sb.append(key);
                sb.append(": ");
                sb.append(val);
                sb.append("\r\n");
            }
        }
        sb.append("\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getBodyBytes() {
        return outputStream.toByteArray();
    }

    @Override
    public void close() throws Exception {
        this.outputStream.close();
    }
}
