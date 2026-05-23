package ese03.src;

import java.io.*;
import java.net.Socket;

public class ChatConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatConnection(Socket socket) throws IOException {
        this.socket = socket;

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.out = new PrintWriter( new OutputStreamWriter(socket.getOutputStream()), true);

    }

    public void startReading() {
        Thread readerThread = new Thread(() -> {
            try {
                String message;

                while ((message = in.readLine()) != null) {
                    System.out.println();
                    System.out.println("Messaggio ricevuto: " + message);
                    System.out.print("> ");
                }

            } catch (IOException e) {
                System.out.println("Connessione interrotta.");
            } finally {
                close();
            }
        });
        readerThread.start();
    }

    public void send(String message) {
        out.println(message);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Errore durante la chiusura della socket.");
        }
    }


}
