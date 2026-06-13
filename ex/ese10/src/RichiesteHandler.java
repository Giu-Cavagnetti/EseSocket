package ese10.src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RichiesteHandler extends Thread {

    private final Socket socket;
    private final Server server;

    public RichiesteHandler(Socket socketClient, Server server) {
        this.socket = socketClient;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream objectInputStream =
                        new ObjectInputStream(socket.getInputStream());

                PrintWriter printWriter =
                        new PrintWriter(socket.getOutputStream(), true)
        ) {
            Partecipazione partecipazione =
                    (Partecipazione) objectInputStream.readObject();

            if (server.verificaPartecipazione(partecipazione)) {
                String risposta = server.aggiungiPartecipazione(partecipazione);
                printWriter.println(risposta);
            } else {
                printWriter.println("NOT_ACCEPTED");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}