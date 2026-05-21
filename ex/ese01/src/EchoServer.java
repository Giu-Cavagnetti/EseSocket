package ese01.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    public static void main(String[] args) {
        int port = 8189;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server aperto sulla porta " + port);

            Socket incoming = serverSocket.accept();
            System.out.println("Client connesso");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(incoming.getInputStream())
            );

            PrintWriter out = new PrintWriter(
                    incoming.getOutputStream(),
                    true
            );

            out.println("Hello! Enter BYE to EXIT");

            boolean done = false;

            while (!done) {
                String line = in.readLine();

                if (line == null) {
                    done = true;
                } else {
                    out.println("ECHO: " + line);

                    if (line.trim().equalsIgnoreCase("BYE")) {
                        done = true;
                    }
                }
            }

            incoming.close();
            System.out.println("Connessione chiusa");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}