package ese11.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ServerCalcistico {

    private static final int SERVER_PORT = 6666;
    private static final int NUMERO_MAX_RICHIESTE = 5;

    private final Set<String> squadreValide = Set.of(
            "Milan", "Juve", "Inter", "Roma", "Lazio", "Atalanta"
    );

    private final Map<String, dtoClient> partiteInserite = new HashMap<>();
    private final Classifica classifica = new Classifica();

    private final Semaphore semaforoInserimenti = new Semaphore(NUMERO_MAX_RICHIESTE);

    private int partiteUltimi10Minuti = 0;
    private int golUltimi10Minuti = 0;

    public void avvia() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            new ThreadResoconto(this).start();

            System.out.println("Server avviato sulla porta " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new GestisciRichiesta(this, socket).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String inserisciRisultato(dtoClient dto) {
        if (!semaforoInserimenti.tryAcquire()) {
            return "Errore: server in sovraccarico, richiesta rifiutata";
        }

        try {
            synchronized (this) {

                if (!squadreValide.contains(dto.squadraCasa()) ||
                        !squadreValide.contains(dto.squadraOspite())) {
                    return "Errore: una o entrambe le squadre non esistono";
                }

                if (dto.golCasa() < 0 || dto.golOspite() < 0) {
                    return "Errore: numero di gol non valido";
                }

                if (partiteInserite.containsKey(dto.codicePartita())) {
                    return "Errore: partita già inserita";
                }

                partiteInserite.put(dto.codicePartita(), dto);
                classifica.aggiornaDaPartita(dto);

                partiteUltimi10Minuti++;
                golUltimi10Minuti += dto.golCasa() + dto.golOspite();

                return "Risultato inserito correttamente";
            }

        } finally {
            semaforoInserimenti.release();
        }
    }

    public synchronized String richiediClassifica() {
        return classifica.toString();
    }

    public synchronized String generaResocontoEResetta() {
        double mediaGol;

        if (partiteUltimi10Minuti == 0) {
            mediaGol = 0.0;
        } else {
            mediaGol = (double) golUltimi10Minuti / partiteUltimi10Minuti;
        }

        String resoconto = "Partite ultimi 10 minuti: " + partiteUltimi10Minuti +
                " | Media gol per partita: " + mediaGol;

        partiteUltimi10Minuti = 0;
        golUltimi10Minuti = 0;

        return resoconto;
    }

    public static void main(String[] args) {
        ServerCalcistico server = new ServerCalcistico();
        server.avvia();
    }
}