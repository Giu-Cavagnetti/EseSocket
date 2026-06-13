package ese10.src;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Server {

    private static final int PORTA_TCP = 3000;
    private static final int PORTA_UDP = 4000;
    private static final int PORTA_MULTICAST = 5000;
    private static final String MULTICAST_ADDRESS = "230.0.0.1";

    private final Map<Integer, Partecipazione> listaPartecipanti;
    private final Map<String, Concorso> concorsi;
    private final Set<String> concorsiNotificati;

    private final AtomicInteger prossimoProtocollo;

    public Server(List<Concorso> concorsiAttivi) {
        this.listaPartecipanti = new ConcurrentHashMap<>();
        this.concorsi = new ConcurrentHashMap<>();
        this.concorsiNotificati = ConcurrentHashMap.newKeySet();
        this.prossimoProtocollo = new AtomicInteger(1);

        for (Concorso c : concorsiAttivi) {
            concorsi.put(c.id(), c);
        }
    }

    public void avvia() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORTA_TCP);
            DatagramSocket datagramSocket = new DatagramSocket(PORTA_UDP);

            new CancellazioneHandler(datagramSocket, this).start();

            new Thread(this::controllaScadenze).start();

            System.out.println("Server avviato...");

            while (true) {
                Socket socketClient = serverSocket.accept();
                new RichiesteHandler(socketClient, this).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void controllaScadenze() {
        while (true) {
            try {
                for (Concorso concorso : concorsi.values()) {
                    if (!verificaScadenza(concorso.id()) &&
                            concorsiNotificati.add(concorso.id())) {

                        mandaVincitoriInMulticast(concorso);
                    }
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void mandaVincitoriInMulticast(Concorso concorso) {
        try {
            List<Partecipazione> candidatiDelConcorso =
                    listaPartecipanti.values()
                            .stream()
                            .filter(p -> p.idConcorso().equals(concorso.id()))
                            .collect(Collectors.toList());

            Collections.shuffle(candidatiDelConcorso);

            int numeroVincitori = Math.min(
                    concorso.numeroPostiVincitori(),
                    candidatiDelConcorso.size()
            );

            StringBuilder messaggio = new StringBuilder();

            messaggio.append("ID_CONCORSO: ")
                    .append(concorso.id())
                    .append(" - VINCITORI: ");

            for (int i = 0; i < numeroVincitori; i++) {
                messaggio.append(candidatiDelConcorso.get(i).codiceFiscale());

                if (i < numeroVincitori - 1) {
                    messaggio.append(", ");
                }
            }

            byte[] buffer = messaggio.toString().getBytes(StandardCharsets.UTF_8);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    group,
                    PORTA_MULTICAST
            );

            try (MulticastSocket multicastSocket = new MulticastSocket()) {
                multicastSocket.send(packet);
            }

            System.out.println("Messaggio multicast inviato: " + messaggio);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verificaPartecipazione(Partecipazione partecipazione) {
        if (partecipazione == null) {
            return false;
        }

        Concorso concorso = concorsi.get(partecipazione.idConcorso());

        if (concorso == null) {
            return false;
        }

        return verificaScadenza(concorso.id()) &&
                campoValido(partecipazione.idConcorso()) &&
                campoValido(partecipazione.nome()) &&
                campoValido(partecipazione.cognome()) &&
                campoValido(partecipazione.codiceFiscale()) &&
                campoValido(partecipazione.curriculum());
    }

    private boolean campoValido(String campo) {
        return campo != null && !campo.isBlank();
    }

    public synchronized String aggiungiPartecipazione(Partecipazione partecipazione) {
        int idProtocollo = prossimoProtocollo.getAndIncrement();

        listaPartecipanti.put(idProtocollo, partecipazione);

        return idProtocollo + " " + Instant.now();
    }

    public boolean verificaScadenza(String idConcorso) {
        Concorso concorso = concorsi.get(idConcorso);

        if (concorso == null) {
            return false;
        }

        return Instant.now().isBefore(concorso.scadenza());
    }

    public synchronized boolean cancellaPrenotazione(int idProtocollo) {
        return listaPartecipanti.remove(idProtocollo) != null;
    }
}