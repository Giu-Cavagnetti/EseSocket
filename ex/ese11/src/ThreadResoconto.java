package ese11.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ThreadResoconto extends Thread {

    private final ServerCalcistico serverCalcistico;

    private static final int MULTICAST_PORT = 5000;
    private static final String MULTICAST_ADDRESS = "239.255.0.1";

    public ThreadResoconto(ServerCalcistico serverCalcistico) {
        this.serverCalcistico = serverCalcistico;
    }

    @Override
    public void run() {
        try (MulticastSocket multicastSocket = new MulticastSocket()) {

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            while (true) {

                Thread.sleep(10 * 60 * 1000);

                String resoconto = serverCalcistico.generaResocontoEResetta();

                byte[] buffer = resoconto.getBytes();

                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        group,
                        MULTICAST_PORT
                );

                multicastSocket.send(packet);

                System.out.println("Resoconto multicast inviato: " + resoconto);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}