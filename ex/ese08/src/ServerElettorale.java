package ese08.src;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerElettorale {

    private static final int TCP_PORT = 5000;
    private static final int MULTICAST_PORT = 6000;
    private static final String MULTICAST_ADDRESS = "230.0.0.1";

    private Map<String, Integer> votiNazionali = new HashMap<>();   //Partito - numeroVoti
    private Map<String, Set<Integer>> sezioniProcessate = new HashMap<>();  //Comune - sezioniProcessate, CROTONE-> [2,4,6]

    public static void main(String[] args) {
        ServerElettorale server = new ServerElettorale();
        server.avvia();
    }

    private void avvia() {
        new Thread(() -> inviaRiepilogoPeriodico()).start();
        try{
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);

            while(true){
                Socket clientSocket = serverSocket.accept();
                new GestoreClient(clientSocket, this).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void inviaRiepilogoPeriodico() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket();

            while (true) {
                String riepilogo = creaRiepilogo();
                byte[] buf = riepilogo.getBytes();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
                socket.send(packet);

                System.out.println("Inviato riepilogo multicast: " + riepilogo);
                Thread.sleep(30000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized String creaRiepilogo() {
        return "Riepilogo voti nazionali: \n" +
                votiNazionali.toString();
    }


    public synchronized void registraVoto(VotoComune voto) {

        String comune = voto.nomeComune();
        int sezione = voto.idSezione();

        // Se il comune non esiste ancora nella mappa,
        // creo il suo insieme di sezioni processate
        sezioniProcessate.putIfAbsent(comune, new HashSet<>());

        // Recupero l'insieme delle sezioni già viste per quel comune
        Set<Integer> sezioniDelComune = sezioniProcessate.get(comune);

        // Se quella sezione è già stata ricevuta, la scarto
        if (sezioniDelComune.contains(sezione)) {
            System.out.println("Sezione già processata: " + comune + " - " + sezione);
            return;
        }

        // Sezione nuova: la segno come processata
        sezioniDelComune.add(sezione);

        // Ora posso sommare i voti ai totali nazionali
        for (String partito : voto.votiPerPartito().keySet()) {

            int votiRicevuti = voto.votiPerPartito().get(partito);      //Partito -> n voti
            int votiAttuali = votiNazionali.getOrDefault(partito, 0);

            votiNazionali.put(partito, votiAttuali + votiRicevuti);
        }

        System.out.println("Voto registrato: " + voto);
        System.out.println("Totali nazionali aggiornati: " + votiNazionali);
    }
}
