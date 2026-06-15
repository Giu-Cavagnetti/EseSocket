package ese12.src;

import java.io.*;
import java.net.Socket;

public class GestisciRichiestaSensore extends Thread {

    private final Socket socket;
    private final ServerAgricolo serverAgricolo;

    public GestisciRichiestaSensore(Socket socket, ServerAgricolo serverAgricolo) {
        this.socket = socket;
        this.serverAgricolo = serverAgricolo;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {

            if (!serverAgricolo.verificaOrario()) {
                printWriter.println("Rifiutato: richiesta fuori orario");
                return;
            }

            StatoSensore statoSensore = (StatoSensore) objectInputStream.readObject();

            String risposta = serverAgricolo.elaboraStato(statoSensore);

            printWriter.println(risposta);

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