package info.repy.webframework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamParser {
    int crlfcount = 0;
    int body = -1;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Request request = new Request();

    public List<Request> input(byte[] buf, int end) throws IOException {
        List<Request> ret = new ArrayList<>();


        int start = 0;
        for (int i = 0; i < end; i++) {
            byte by = buf[i];
            char ch = (char) by;
            if((crlfcount == 3 || crlfcount == 1) && by == '\n'){
                crlfcount++;
                start = i + 1;
                continue;
            }
            if (body > 0) {
                body--;
            }
            if (body == -1) {
                if(by == '\r'){
                    if(crlfcount == 0){
                        crlfcount = 1;
                        //切り出し
                    } else if(crlfcount == 1){
                        crlfcount = 1;
                        //切り出し
                    } else if(crlfcount == 2){
                        crlfcount = 3;
                        //切り出し
                    } else {
                        throw new RuntimeException();
                        // ありえない
                    }
                } else if(by == '\n'){
                    if(crlfcount == 0){
                        crlfcount = 0;
                        //切り出し
                    } else if(crlfcount == 1){
                        throw new RuntimeException();
                        // ありえない
                    } else if(crlfcount == 2){
                        crlfcount = 0;
                        //切り出し
                    } else {
                        throw new RuntimeException();
                        // ありえない
                    }
                } else {
                    crlfcount = 0;
                    continue;
                }

                //切り出し
                if (start <= i) {
                    out.write(buf, start, i - start);
                    start = i + 1;
                }
                out.flush();
                byte[] add = out.toByteArray();
                out.reset();
                request.inputHeader(add);
                if(add.length == 0 && request.parseHeader()){
                    body = request.getContentLength();
                }
            }
            if (body == 0) {
                //切り出し
                if (start <= i) {
                    out.write(buf, start, i - start + 1);
                    start = i + 1;
                }
                byte[] bodys = out.toByteArray();
                out.reset();
                request.inputBody(bodys);
                ret.add(request);
                request = new Request();
                body = -1;
            }
        }
        out.write(buf, start, end - start);

        return ret;
    }
}
