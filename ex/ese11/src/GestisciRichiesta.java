package ese11.src;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestisciRichiesta extends Thread {

    private final ServerCalcistico serverCalcistico;
    private final Socket socket;

    public GestisciRichiesta(ServerCalcistico serverCalcistico, Socket socket) {
        this.serverCalcistico = serverCalcistico;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String messaggio = in.readLine();

            if (messaggio == null) {
                out.println("Errore: messaggio vuoto");
                return;
            }

            String[] parts = messaggio.split(";");

            String idClient = parts[0];
            String operazione = parts[1];

            if (operazione.equals("INSERISCI_RISULTATO")) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                dtoClient dto = new dtoClient(
                        idClient,
                        operazione,
                        parts[2],
                        parts[3],
                        parts[4],
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6]),
                        LocalDateTime.parse(parts[7], formatter)
                );

                String risposta = serverCalcistico.inserisciRisultato(dto);
                out.println(risposta);

            } else if (operazione.equals("RICHIEDI_CLASSIFICA")) {

                String classifica = serverCalcistico.richiediClassifica();
                out.println(classifica);

            } else {
                out.println("Errore: operazione non supportata");
            }

        } catch (Exception e) {
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