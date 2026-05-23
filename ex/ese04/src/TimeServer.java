package ese04.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeServer {

    public static void main(String[] args){
        final int port = 3355;
        final int LUNGHEZZA_DEFAULT = 256;

        DatagramSocket socket = null;

        try{
            socket = new DatagramSocket(port);
            byte[] buf = new byte[LUNGHEZZA_DEFAULT];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String localita = new String (packet.getData(), 0, packet.getLength());
            Calendar myCalendar = Calendar.getInstance(TimeZone.getTimeZone(localita));

            String response = ("Time in " + localita + " is " + myCalendar.get(Calendar.HOUR) + ":" + myCalendar.get(Calendar.MINUTE));
            buf = response.getBytes();

            InetAddress address = packet.getAddress();
            int portR = packet.getPort();
            packet = new DatagramPacket(buf, buf.length,address,portR);
            socket.send(packet);

        } catch (IOException e){
            e.printStackTrace();
        }



    }
}
