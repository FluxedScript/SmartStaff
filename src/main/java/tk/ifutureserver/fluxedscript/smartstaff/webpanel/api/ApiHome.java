package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.IOException;
import java.io.OutputStream;

public class ApiHome implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        String oldpassword = Main.oldapipassword;
        String cmd = null;
        String checkpassword = null;
        String response = t.getRequestURI().getQuery(); //if http://localhost:4000/api?name=john&password=hi
        if(t.getRequestURI().getQuery() != null) {
            System.out.print(t.getRequestURI().getPath());
            System.out.print(response); //name=john&password=hi
            String[] parts = response.split("&");
            for (String s: parts) {
                String mainname = s.split("=")[0];
                String attribute = s.split("=")[1];
                if (mainname.equalsIgnoreCase("password")) {
                    checkpassword = attribute;
                    System.out.print("password: "+attribute);
                }
                if(mainname.equalsIgnoreCase("cmd") || mainname.equalsIgnoreCase("command") || mainname.equalsIgnoreCase("run")) {
                    cmd = attribute;
                    System.out.print("CMD: "+attribute);
                }
            }
        }else {
            return;
        }
        if (!(oldpassword.equals(checkpassword))) {
            String response2 = "Invalid password";
            t.sendResponseHeaders(401, response2.length());
            OutputStream os = t.getResponseBody();
            os.write(response2.getBytes());
            os.close();
            return;
        }
    }
}