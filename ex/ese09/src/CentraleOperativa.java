package ese09.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CentraleOperativa extends Thread {

    private static final int SERVER_PORT = 3333;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            System.out.println("Centrale operativa in ascolto TCP sulla porta " + SERVER_PORT);

            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(socket.getInputStream())
                        )
                ) {
                    String allarme = br.readLine();

                    if (allarme != null) {
                        System.out.println("ALLARME RICEVUTO: " + allarme);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}