package ese09.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AutobusT extends Thread {

    private DatagramSocket clientSocket;
    private AutobusClient autobusClient;

    private static final String NOME_HOST = "localhost";
    private static final int SERVER_PORT = 2222;

    private static final long DIECI_MINUTI = 10 * 1000;
    private static final long TRE_ORE = 3 * 1000;

    public AutobusT(DatagramSocket clientSocket, AutobusClient autobusClient) {
        this.clientSocket = clientSocket;
        this.autobusClient = autobusClient;
    }

    @Override
    public void run() {
        try {
            while (true) {

                mandaMessaggio();

                long ora = System.currentTimeMillis();

                if (ora - autobusClient.getUltimoAggiornamentoPosizione() >= TRE_ORE) {
                    aggiornaPosizioneInAvanti();
                    autobusClient.setUltimoAggiornamentoPosizione(System.currentTimeMillis());
                }

                Thread.sleep(DIECI_MINUTI);
            }

        } catch (InterruptedException e) {
            System.out.println("Thread autobus interrotto");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mandaMessaggio() {
        try {
            String mex = autobusClient.toMessage();
            byte[] buf = mex.getBytes();

            InetAddress inetAddress = InetAddress.getByName(NOME_HOST);

            DatagramPacket datagramPacket = new DatagramPacket(
                    buf,
                    buf.length,
                    inetAddress,
                    SERVER_PORT
            );

            clientSocket.send(datagramPacket);

            System.out.println("Messaggio inviato: " + mex);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aggiornaPosizioneInAvanti() {
        autobusClient.setPosizioneSuccessiva();
    }
}