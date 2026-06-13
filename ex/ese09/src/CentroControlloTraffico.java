package ese09.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class CentroControlloTraffico extends Thread {

    private static final int CLIENT_PORT = 2222;
    private static final int CENTRALE_PORT = 3333;
    private static final String HOST_CENTRALE = "localhost";

    private static final long TRE_ORE = 30 * 1000;
    private static final long TEMPO_CONTROLLO = 5 * 1000;

    private ConcurrentHashMap<String, StatoAutobus> listaAutobusAttivi;

    public CentroControlloTraffico() {
        this.listaAutobusAttivi = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        Thread controlloThread = new Thread(() -> controllaAutobus());
        controlloThread.start();

        try (DatagramSocket socket = new DatagramSocket(CLIENT_PORT)) {

            byte[] buffer = new byte[1024];

            System.out.println("Centro controllo in ascolto UDP sulla porta " + CLIENT_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String messaggio = new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                );

                registraPosizioneNuova(messaggio);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registraPosizioneNuova(String messaggio) {
        String[] parti = messaggio.split(";");

        if (parti.length != 3) {
            System.out.println("Messaggio non valido: " + messaggio);
            return;
        }

        String idAutobus = parti[0];
        String posizione = parti[1];
        long ultimoAggiornamentoPosizione;

        try {
            ultimoAggiornamentoPosizione = Long.parseLong(parti[2]);
        } catch (NumberFormatException e) {
            System.out.println("Timestamp non valido nel messaggio: " + messaggio);
            return;
        }

        StatoAutobus stato = listaAutobusAttivi.get(idAutobus);

        if (stato == null) {
            stato = new StatoAutobus(
                    idAutobus,
                    posizione,
                    ultimoAggiornamentoPosizione
            );

            listaAutobusAttivi.put(idAutobus, stato);

            System.out.println("Nuovo autobus registrato: " +
                    idAutobus + " in posizione " + posizione);

        } else {
            stato.aggiorna(posizione, ultimoAggiornamentoPosizione);

            System.out.println("Aggiornato autobus: " +
                    idAutobus + " in posizione " + posizione);
        }
    }

    private void controllaAutobus() {
        while (true) {
            try {
                long ora = System.currentTimeMillis();

                for (StatoAutobus stato : listaAutobusAttivi.values()) {

                    long tempoDaUltimoCambio = ora - stato.getUltimoCambioPosizione();

                    if (tempoDaUltimoCambio >= TRE_ORE && !stato.isAllarmeInviato()) {
                        inviaAllarmeAllaCentrale(stato);
                        stato.setAllarmeInviato(true);
                    }
                }

                Thread.sleep(TEMPO_CONTROLLO);

            } catch (InterruptedException e) {
                System.out.println("Thread controllo autobus interrotto");
                break;
            }
        }
    }

    private void inviaAllarmeAllaCentrale(StatoAutobus stato) {
        try (
                Socket socket = new Socket(HOST_CENTRALE, CENTRALE_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String allarme = "Allerta: autobus " +
                    stato.getId() +
                    " fermo nella posizione " +
                    stato.getPosizioneAttuale() +
                    " da più di 3 ore";

            out.println(allarme);

            System.out.println("Allarme inviato alla centrale: " + allarme);

        } catch (IOException e) {
            System.out.println("Errore invio allarme alla centrale");
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<String, StatoAutobus> getListaAutobusAttivi() {
        return listaAutobusAttivi;
    }
}