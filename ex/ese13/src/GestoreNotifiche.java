package ese13.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class GestoreNotifiche extends Thread{

    private Server server;
    private int multicastPort;
    private String address;
    private final static int COOLDOWN = 15;


    public GestoreNotifiche(Server server, int multicastPort, String multicastId) {
        this.server = server;
        this.multicastPort = multicastPort;
        address = multicastId;
    }

    public void run(){
        try{
            MulticastSocket multicastSocket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(address);

            while(true){

                Thread.sleep(COOLDOWN * 60 * 1000L);
                String messaggioMulticast = server.getResoconto();

                byte[] buf = messaggioMulticast.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName(address), multicastPort);
             multicastSocket.send(datagramPacket);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
