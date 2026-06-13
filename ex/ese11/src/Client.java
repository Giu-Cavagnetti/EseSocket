package ese11.src;

import java.io.*;
import java.net.*;

public class Client{

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6666;

    private static final String MULTICAST_ADDRESS = "239.255.0.1";
    private static final int MULTICAST_PORT = 5000;

    private String idClient;

    public Client(String idClient) {
        this.idClient = idClient;
    }

    public void inviaRichiesta(String messaggio) {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                )
        ) {
            out.println(messaggio);

            String risposta;
            while ((risposta = in.readLine()) != null) {
                System.out.println(risposta);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void richiediClassifica() {
        String messaggio = idClient + ";" + "RICHIEDI_CLASSIFICA";
        inviaRichiesta(messaggio);
    }

    public void ascoltaMulticast() {
        new Thread(() -> {
            try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT)) {

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                multicastSocket.joinGroup(group);

                while (true) {
                    byte[] buffer = new byte[1024];

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);

                    String msg = new String(
                            packet.getData(),
                            0,
                            packet.getLength()
                    );

                    System.out.println("[MULTICAST] " + msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {

        Client client = new Client("Client01");

        client.ascoltaMulticast();

        String messaggio = client.idClient + ";" +
                            "INSERISCI_RISULTATO" + ";" +
                            "Match001" + ";" +
                            "Milan" + ";" +
                            "Juve" + ";" +
                            "3" + ";" +
                            "1" + ";" +
                            "2025-01-15 20:45";

        client.inviaRichiesta(messaggio);
        client.richiediClassifica();
    }
}