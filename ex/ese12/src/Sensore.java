package ese12.src;

import java.io.*;
import java.net.*;

public class Sensore {

    private static final String SERVER_HOST = "localhost";
    private static final int PORTA_STATI = 3000;
    private static final int PORTA_REGISTRAZIONE = 4000;
    private static final int PORTA_UDP = 4000;

    private final String id;

    public Sensore(String id) {
        this.id = id;
    }

    public void inviaStato(double temperaturaAria, double umiditaSuolo) {
        try (
                Socket socket = new Socket(SERVER_HOST, PORTA_STATI);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            StatoSensore stato = new StatoSensore(
                    id,
                    0,
                    temperaturaAria,
                    umiditaSuolo
            );

            objectOutputStream.writeObject(stato);
            objectOutputStream.flush();

            String risposta = bufferedReader.readLine();
            System.out.println("Risposta server: " + risposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registraNotifiche() {
        try (
                Socket socket = new Socket(SERVER_HOST, PORTA_REGISTRAZIONE);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {

            objectOutputStream.writeObject(id);
            objectOutputStream.flush();

            System.out.println("Sensore " + id + " registrato al servizio notifiche");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ascoltaNotifiche() {
        new Thread(() -> {
            try (DatagramSocket datagramSocket = new DatagramSocket(PORTA_UDP)) {

                System.out.println("Sensore " + id + " in ascolto UDP sulla porta " + PORTA_UDP);

                while (true) {
                    byte[] buffer = new byte[1024];

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    String messaggio = new String(
                            packet.getData(),
                            0,
                            packet.getLength()
                    );

                    System.out.println("[NOTIFICA] " + messaggio);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}