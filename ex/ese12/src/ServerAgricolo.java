package ese12.src;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAgricolo {

    private static final int PORTA_STATI = 3000;
    private static final int PORTA_REGISTRAZIONE = 4000;
    private static final int PORTA_UDP_CLIENT = 4000;

    private static final double RANGE_ACCETTATO = 0.05;

    private static final LocalTime ORA_MIN = LocalTime.of(8, 0);
    private static final LocalTime ORA_MAX = LocalTime.of(13, 0);

    private final Map<String, List<StatoSensore>> statiPerSensore = new ConcurrentHashMap<>();
    private final Map<String, Integer> progressiviPerSensore = new ConcurrentHashMap<>();
    private final Map<String, InetAddress> sensoriRegistrati = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ServerAgricolo server = new ServerAgricolo();
        server.avvia();
    }

    public void avvia() {
        new Thread(this::avviaServerRegistrazioni).start();
        avviaServerStati();
    }

    private void avviaServerStati() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA_STATI)) {
            System.out.println("Server stati avviato sulla porta " + PORTA_STATI);

            while (true) {
                Socket socket = serverSocket.accept();
                new GestisciRichiestaSensore(socket, this).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void avviaServerRegistrazioni() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA_REGISTRAZIONE)) {
            System.out.println("Server registrazioni avviato sulla porta " + PORTA_REGISTRAZIONE);

            while (true) {
                Socket socket = serverSocket.accept();
                new GestisciRegistrazioneNotifiche(socket, this).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verificaOrario() {
        LocalTime oraAttuale = LocalTime.now();

        return !oraAttuale.isBefore(ORA_MIN) && !oraAttuale.isAfter(ORA_MAX);
    }

    public void registraSensorePerNotifiche(String idSensore, InetAddress indirizzo) {
        sensoriRegistrati.put(idSensore, indirizzo);
        System.out.println("Sensore registrato per notifiche: " + idSensore + " - " + indirizzo);
    }

    public synchronized String elaboraStato(StatoSensore statoRicevuto) {
        if (deveEssereRifiutato(statoRicevuto)) {
            return "Rifiutato: valori troppo simili alle medie precedenti";
        }

        int nuovoProgressivo = progressiviPerSensore.getOrDefault(statoRicevuto.id(), 0) + 1;
        progressiviPerSensore.put(statoRicevuto.id(), nuovoProgressivo);

        StatoSensore statoSalvato = new StatoSensore(
                statoRicevuto.id(),
                nuovoProgressivo,
                statoRicevuto.temperaturaAria(),
                statoRicevuto.umiditaSuolo()
        );

        statiPerSensore
                .computeIfAbsent(statoSalvato.id(), k -> new ArrayList<>())
                .add(statoSalvato);

        inviaNotificheAgliAltriSensori(statoSalvato);

        return "Accettato: numero progressivo assegnato = " + nuovoProgressivo;
    }

    private boolean deveEssereRifiutato(StatoSensore stato) {
        List<StatoSensore> statiStessoSensore = statiPerSensore.get(stato.id());

        if (statiStessoSensore == null || statiStessoSensore.isEmpty()) {
            return false;
        }

        double mediaUmiditaStessoSensore = calcolaMediaUmidita(statiStessoSensore);

        List<StatoSensore> tuttiGliStati = new ArrayList<>();

        for (List<StatoSensore> lista : statiPerSensore.values()) {
            tuttiGliStati.addAll(lista);
        }

        if (tuttiGliStati.isEmpty()) {
            return false;
        }

        double mediaTemperaturaGlobale = calcolaMediaTemperatura(tuttiGliStati);

        boolean umiditaSimile = dentroRange5Percento(
                stato.umiditaSuolo(),
                mediaUmiditaStessoSensore
        );

        boolean temperaturaSimile = dentroRange5Percento(
                stato.temperaturaAria(),
                mediaTemperaturaGlobale
        );

        return umiditaSimile && temperaturaSimile;
    }

    private double calcolaMediaUmidita(List<StatoSensore> lista) {
        double somma = 0;

        for (StatoSensore stato : lista) {
            somma += stato.umiditaSuolo();
        }

        return somma / lista.size();
    }

    private double calcolaMediaTemperatura(List<StatoSensore> lista) {
        double somma = 0;

        for (StatoSensore stato : lista) {
            somma += stato.temperaturaAria();
        }

        return somma / lista.size();
    }

    private boolean dentroRange5Percento(double valore, double media) {
        return Math.abs(valore - media) <= Math.abs(media) * RANGE_ACCETTATO;
    }

    private void inviaNotificheAgliAltriSensori(StatoSensore stato) {
        String messaggio = stato.toString();
        byte[] buffer = messaggio.getBytes();

        for (Map.Entry<String, InetAddress> entry : sensoriRegistrati.entrySet()) {
            String idRegistrato = entry.getKey();
            InetAddress indirizzo = entry.getValue();

            if (!idRegistrato.equals(stato.id())) {
                try (DatagramSocket socketUDP = new DatagramSocket()) {
                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            indirizzo,
                            PORTA_UDP_CLIENT
                    );

                    socketUDP.send(packet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}