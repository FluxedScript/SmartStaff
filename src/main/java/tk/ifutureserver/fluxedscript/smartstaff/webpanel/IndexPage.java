package tk.ifutureserver.fluxedscript.smartstaff.webpanel;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class IndexPage implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        String root = Main.DataFolder + File.separator + "html";
        URI uri = t.getRequestURI();
        Headers h2 = t.getResponseHeaders();
        System.out.print(h2);
        System.out.println("looking for: "+ root + uri.getPath());
        String path = uri.getPath();
        if (path.equals("/") || path.endsWith("/") || !(path.contains("."))){
            if (path.endsWith("/")){
                path = path + "index.html";
            } else{
                path = path + "/index.html";
            }

            System.out.print(path);

        }
        File file = new File(root + path).getCanonicalFile();

        if (!file.isFile()) {
            // Object does not exist or is not a file: reject with 404 error.
            file = new File(root + "/errorpages/404.html").getCanonicalFile();
            if (!file.isFile()) { // No files exist
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else{  // check if 404 page exists
                Headers h = t.getResponseHeaders();
                h.set("Content-Type", "text/html");
                t.sendResponseHeaders(200, 0);

                OutputStream os = t.getResponseBody();
                FileInputStream fs = new FileInputStream(file);
                final byte[] buffer = new byte[0x10000];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                    os.write(buffer,0,count);
                }
                fs.close();
                os.close();
            }
        } else {
            // Object exists and is a file: accept with response code 200.
            String mime = "text/html";
            if(path.endsWith(".js")) mime = "application/javascript";
            if(path.endsWith("css")) mime = "text/css";

            Headers h = t.getResponseHeaders();
            h.set("Content-Type", mime);
            t.sendResponseHeaders(200, 0);

            OutputStream os = t.getResponseBody();
            FileInputStream fs = new FileInputStream(file); //get the file
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer,0,count);
            }
            fs.close(); // close file system
            os.close(); // close output stream
        }
    }
}
