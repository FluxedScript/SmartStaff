package tk.ifutureserver.fluxedscript.smartstaff.webpanel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HomePage implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        //String response2 = "Welcome to the panel!";
        //t.sendResponseHeaders(200, response2.length());
        //OutputStream os = t.getResponseBody();
        //os.write(response2.getBytes());
        //os.close();
        File file = new File("/html/index.html");
        System.out.print(file.length());
        t.sendResponseHeaders(200, file.length());
        OutputStream os = t.getResponseBody();
        try {
            Files.copy(file.toPath(), os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        os.close();
    }
}