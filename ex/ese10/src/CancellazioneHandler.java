package ese10.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class CancellazioneHandler extends Thread {

    private final DatagramSocket datagramSocket;
    private final Server server;

    public CancellazioneHandler(DatagramSocket datagramSocket, Server server) {
        this.datagramSocket = datagramSocket;
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[512];

                DatagramPacket richiestaPacket =
                        new DatagramPacket(buffer, buffer.length);

                datagramSocket.receive(richiestaPacket);

                String testo = new String(
                        richiestaPacket.getData(),
                        0,
                        richiestaPacket.getLength(),
                        StandardCharsets.UTF_8
                ).trim();

                int idProtocollo = Integer.parseInt(testo);

                boolean successo =
                        server.cancellaPrenotazione(idProtocollo);

                String risposta = String.valueOf(successo);

                byte[] datiRisposta =
                        risposta.getBytes(StandardCharsets.UTF_8);

                DatagramPacket rispostaPacket = new DatagramPacket(
                        datiRisposta,
                        datiRisposta.length,
                        richiestaPacket.getAddress(),
                        richiestaPacket.getPort()
                );

                datagramSocket.send(rispostaPacket);

            } catch (NumberFormatException e) {
                System.out.println("ID protocollo non valido ricevuto");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}