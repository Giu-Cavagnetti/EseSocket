package ese13.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class RicevitoreMulticast extends Thread{
    private final static String MULTICAST_ID = "239.255.0.1";


    public void run(){
        try{
            MulticastSocket socket = new MulticastSocket(5000);
            InetAddress group = InetAddress.getByName(MULTICAST_ID);
            socket.joinGroup(group);

            while(true){
                byte[] buf = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                socket.receive(datagramPacket);

                String msg = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("Multicast" + msg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
