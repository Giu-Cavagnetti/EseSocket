package ese04.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TimeClient {
    public static void main(String[] args) throws IOException {
        String hostname = "localhost";
        int serverPort = 3355;

        DatagramSocket socket = new DatagramSocket();
        System.out.println("Inseriscila località di cui vuoi sapere l'ora: ");
        Scanner s = new Scanner(System.in);
        String localita = s.nextLine();

        byte[] buf = localita.getBytes();

        InetAddress address = InetAddress.getByName(hostname);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);

        socket.send(packet);

        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());

        System.out.println("Response: " + received);

        socket.close();
    }
}