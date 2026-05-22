package ese01.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class EchoClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8189;

        int connectionTimeout = 5000;
        int readTimeout = 10000;

        System.out.println("Client avviato");

        Socket socket = SocketOpener.openSocket(host, port, connectionTimeout);

        if (socket == null) {
            System.out.println("Connessione non riuscita, socket non aperto");
            return;
        }

        try {
            socket.setSoTimeout(readTimeout);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter( socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Connesso al server");

            String welcome = in.readLine();
            System.out.println("Server: " + welcome);

            boolean running = true;
            boolean connectionOpen = true;

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("EXIT")) {
                    if (connectionOpen) {
                        out.println("BYE");

                        try {
                            String response = in.readLine();
                            System.out.println("Server: " + response);
                        } catch (SocketTimeoutException e) {
                            System.out.println("Timeout: il server non ha risposto al comando BYE");
                        }

                        connectionOpen = false;
                    }

                    running = false;

                } else {
                    out.println(input);

                    try {
                        String response = in.readLine();

                        if (response == null) {
                            System.out.println("Il server ha chiuso la connessione");
                            connectionOpen = false;
                            running = false;
                        } else {
                            System.out.println("Server: " + response);
                        }

                    } catch (SocketTimeoutException e) {
                        System.out.println("Timeout: il server non ha risposto entro " + readTimeout + " ms");
                    }

                    if (input.equalsIgnoreCase("BYE")) {
                        connectionOpen = false;
                        running = false;
                    }
                }
            }

            socket.close();
            scanner.close();

            System.out.println("Client terminato");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}