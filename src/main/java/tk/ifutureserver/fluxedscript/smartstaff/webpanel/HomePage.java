package tk.ifutureserver.fluxedscript.smartstaff.webpanel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.FileUtils;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class HomePage implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        //String response2 = "Welcome to the panel!";
        //t.sendResponseHeaders(200, response2.length());
        //OutputStream os = t.getResponseBody();
        //os.write(response2.getBytes());
        //os.close();
        File file = new File(Main.DataFolder + "/html/index.html");
        byte [] response = FileUtils.readFileToByteArray(file);
        System.out.print(file.exists());
        System.out.print(Arrays.toString(response));

        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}