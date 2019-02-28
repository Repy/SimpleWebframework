
package example;

import info.repy.webframework.WebApp;

import java.io.IOException;
import java.net.ServerSocket;

public class App {
    public static void main(String[] args) {
        try {
            ServerSocket sso = new ServerSocket(8080);
            while (true) {
                new Thread(new WebApp(sso.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
