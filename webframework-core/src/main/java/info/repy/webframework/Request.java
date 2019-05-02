package info.repy.webframework;

import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private List<byte[]> headerBytes = new ArrayList<>();

    @Getter
    private byte[] body;

    public Request() {
    }

    public void inputHeader(byte[] line) {
        headerBytes.add(line);
    }
    public void inputBody(byte[] data) {
        body = data;
    }

    public boolean parseHeader() throws IOException {
        boolean firstline = true;
        for (byte[] bytes: headerBytes){
            if(bytes.length == 0) break;
            if(!firstline){
                int end = indexOf(bytes, (byte)':');
                if(end == -1){
                    throw new IOException();
                }
                String name = new String(bytes, 0, end, StandardCharsets.UTF_8);
                int start = end + 1;
                for (int i = start; i < bytes.length; i++) {
                    if(bytes[i] == ' '){
                        start = i + 1;
                    }else{
                        break;
                    }
                }
                String value = new String(bytes, start, bytes.length - start, StandardCharsets.UTF_8);

                addHeader(name, value);
            }else{
                int first = indexOf(bytes, (byte)' ');
                if(first == -1){
                    throw new IOException();
                }
                String me = new String(bytes, 0, first, StandardCharsets.UTF_8);
                int second = indexOf(bytes, (byte)' ', first + 1);
                if(second == -1){
                    throw new IOException();
                }
                String pa = new String(bytes, first + 1, second - (first + 1), StandardCharsets.UTF_8);
                String htt = new String(bytes, second + 1, bytes.length - (second + 1), StandardCharsets.UTF_8);

                this.method = me;
                this.path = pa;
                this.httpVersion = htt;

                firstline = false;
            }
        }
        if(firstline){
            return false;
        }
        contentLength = headerParseContentLength();
        parseQueryString();
        return true;
    }

    private int indexOf(byte[] bytes, byte val){
        return indexOf(bytes, val, 0);
    }
    private int indexOf(byte[] bytes, byte val, int offset){
        for (int i = offset; i < bytes.length; i++) {
            if(bytes[i] == val) return i;
        }
        return -1;
    }

    @Getter
    private String method;

    @Getter
    private String path;

    @Getter
    private Map<String, List<String>> queryString = new HashMap<>();

    @Getter
    private String httpVersion;

    private Map<String, List<String>> headers = new HashMap<>();

    @Getter
    private int contentLength;

    private void parseQueryString() {
        String p = this.path;
        int now = p.indexOf('?');
        if(now == -1) return;
        now++;
        boolean end = true;
        while (end) {
            int next = p.indexOf('&', now);
            int eq = p.indexOf('=', now);
            if(next == -1) {
                next = p.length();
                end = false;
            }
            String key,value;
            if(eq > next || eq == -1) {
                key = p.substring(now, next);
                value = "";
            }else{
                key = p.substring(now, eq);
                value = p.substring(eq + 1, next);
            }
            if(queryString.containsKey(key)){
                queryString.get(key).add(value);
            }else{
                ArrayList<String> ar = new ArrayList<>(1);
                ar.add(value);
                queryString.put(key, ar);
            }
            now = next + 1;
        }
    }

    private int headerParseContentLength() {
        List<String> val = headers.get("content-length");
        if (val == null) return 0;
        if(val.size() > 0) {
            String lens = val.get(0);
            return Integer.parseInt(lens);
        }
        return 0;
    }

    public String[] getHeader(String name) {
        name = name.toLowerCase();
        List<String> val = headers.get(name);
        if (val == null) return null;
        return val.toArray(new String[]{});
    }

    protected void addHeader(String name, String value) {
        name = name.toLowerCase();
        List<String> val = headers.get(name);
        if(val == null) {
            val = new ArrayList<>(1);
            headers.put(name, val);
        }
        val.add(value);
    }
    protected void removeHeader(String name) {
        name = name.toLowerCase();
        if(headers.containsKey(name)) headers.remove(name);
    }
}
