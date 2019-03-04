
package example;

import info.repy.webframework.Request;
import info.repy.webframework.Response;
import info.repy.webframework.WebApp;
import info.repy.webframework.WebHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;

public class App {
    public static void main(String[] args) {
        try {
            // 後でコネクション数の上限機能等をつける
            ServerSocket sso = new ServerSocket(8080);
            while (true) {
                new Thread(new WebApp(sso.accept(), new WebHandler[]{
                        new WebHandler() {
                            @Override
                            public boolean handle(Request req) {
                                return true;
                            }

                            @Override
                            public Response request(Request req) {
                                try (
                                        Response res = new Response(200);
                                        OutputStream out = res.getOutputStream();
                                        OutputStreamWriter w = new OutputStreamWriter(out);
                                        BufferedWriter br = new BufferedWriter(w);
                                ) {
                                    res.addHeader("Content-Type", "text/html");
                                    br.write("ビックカメラ<br><form method='post'><input type='submit' name='aaa' value='ビックカメラ'></form>");
                                    br.close();
                                    return res;
                                } catch (Exception e) {
                                    return null;
                                }
                            }
                        }
                })).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
