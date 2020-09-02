package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ApiHome implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        String response2 = "Welcome to the api!";
        t.sendResponseHeaders(200, response2.length());
        OutputStream os = t.getResponseBody();
        os.write(response2.getBytes());
        os.close();
    }
}