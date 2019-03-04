package info.repy.webframework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class WebApp implements Runnable {
    private final Socket socket;
    private final WebHandler[] handlers;

    public WebApp(Socket so, WebHandler[] handlers) {
        this.handlers = handlers;
        this.socket = so;
    }

    public Response exec(Request req) {
        Response res = null;
        for (WebHandler h : handlers){
            boolean ok = false;
            try{
                ok = h.handle(req);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(ok){
                try{
                    res = h.request(req);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        }
        if(res == null){
            res = ResponseFactory.notFound();
        }
        return res;
    }

    @Override
    public void run() {
        System.out.println("Connection開始");
        try (BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());) {
            StreamParser parser = new StreamParser();
            byte[] buf = new byte[10240];
            // ヘッダー行の処理
            while (true) {
                int end = input.read(buf);
                if (end == -1) {
                    // サーバーから切断された
                    break;
                }
                List<Request> reqs = parser.input(buf, end);
                for (Request req : reqs) {
                    System.out.println("リクエスト");
                    System.out.println(req.getMethod());
                    System.out.println(req.getContentLength());
                    System.out.println(new String(req.getBody(),"UTF-8"));
                    Response aa = exec(req);
                    byte[] by = aa.getHeaderBytes();
                    output.write(by, 0, by.length);
                    by = aa.getBodyBytes();
                    output.write(by, 0, by.length);
                    output.flush();
                    System.out.println("レスポンス");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connectionクローズ");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
