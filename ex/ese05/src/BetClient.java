package ese05.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class BetClient {

    private int myPort;
    private int sPort;
    private InetAddress groupAddress;
    private InetAddress serverAddress;
    private Socket s;

    public BetClient(InetAddress groupAddress, InetAddress serverAddress, int sPort, int myPort){
        this.groupAddress = groupAddress;
        this.serverAddress = serverAddress;
        this.sPort = sPort;
        this.myPort = myPort;

        try{
            s = new Socket(serverAddress, sPort);
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public boolean placeBet(int nCavallo, long puntata){
        String e = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            String bet = nCavallo + " " + puntata;
            out.println(bet);

        } catch (IOException ioe){
            System.out.println(ioe);
        }
        return e.equals("Scommessa accettata");
    }

    public void riceviElencoVincitori(){
        try{
            MulticastSocket ms = new MulticastSocket(myPort);
            ms.joinGroup(groupAddress);
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            ms.receive(packet);
            String elenco = new String (packet.getData());
            System.out.println("Elenco vincitori :" + "\n" + elenco);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] args){
        int serverPort = 8001;
        int myPort = 8002;

        try{
            InetAddress group = InetAddress.getByName("230.0.0.1");
            InetAddress server = InetAddress.getByName("127.0.0.1");
            BetClient client=new BetClient(group, server, serverPort, myPort);

            int cavallo=((int)(Math.random()*12))+1; // cavallo su cui scommette, tra 1 e 12
            int cifra=((int)(Math.random()*100))+1; // cifra su cui scommette, tra 1 e 100 euro

            if (client.placeBet(cavallo, cifra))
                client.riceviElencoVincitori();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
