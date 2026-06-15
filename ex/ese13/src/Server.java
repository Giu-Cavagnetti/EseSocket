package ese13.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Server {

    private final static int OPERAZIONI_MAX = 10,
                            SERVER_PORT = 2222,
                            MULTICAST_PORT = 5000;
    private final static String MULTICAST_ID = "239.255.0.1";

    private final static Semaphore operazioniConcorrenti = new Semaphore(OPERAZIONI_MAX);
    private int numeroRichiesteElaborate = 0;
    private double tassoCambioMedio = 0;

    public static void main(String[] args){
        Server server = new Server();
        server.start();
    }
    public Server(){}

    private void start() {
        try{
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new GestoreNotifiche(this, MULTICAST_PORT, MULTICAST_ID).start();

            while(true){
                Socket socket = serverSocket.accept();
                new GestoreRichieste(this, socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double calcolaCambio() {
        return 0.1 + Math.random() * (5.0 - 0.1);
    }


    public dtoRispostaClient calcolaRichiesta(Richiesta richiesta) {

        if (richiesta.codicePartenza() == null ||
                richiesta.codiceDestinazione() == null ||
                richiesta.idCliente() == null) {
            return new dtoRispostaClient("Errore nei codici", 0, 0);
        }

        if (!operazioniConcorrenti.tryAcquire()) {
            return new dtoRispostaClient(
                    "Richiesta rifiutata, server in sovraccarico",
                    -1,
                    -1
            );
        }

        try {
            double importo = richiesta.importo();
            double tasso = calcolaCambio();
            double importoDopoCambio = importo * tasso;

            synchronized (this) {
                numeroRichiesteElaborate++;
                tassoCambioMedio += tasso;
            }

            return new dtoRispostaClient(
                    "Cambio avvenuto correttamente",
                    importoDopoCambio,
                    tasso
            );

        } finally {
            operazioniConcorrenti.release();
        }
    }

    public synchronized String getResoconto() {
        String messaggio = "Non ci sono state richieste negli ultimi 15 minuti";

        if(numeroRichiesteElaborate != 0){
        messaggio+= "Numero richieste elaborate negli ultimi 15 minuti: " +
                numeroRichiesteElaborate +
                "Tasso medio degli ultimi 15 minuti" +
                tassoCambioMedio / numeroRichiesteElaborate;
        }
        azzeraValori();
        return messaggio;
    }

    private synchronized void azzeraValori() {
        numeroRichiesteElaborate = 0;
        tassoCambioMedio = 0;
    }
}
