package ese07.src;

import ese06.src.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class Giudice {

    private final static int RICHIESTA_PORT = 2000; //TCP
    private final static int MULTICAST_PORT = 3000; //UDP
    private final static String MULTICAST_ADDRESS = "230.0.0.1";
    private final static int OFFERTE_PORT = 4000; //TCP
    private final static int N = 10;


    public static void main (String[] args) throws ClassNotFoundException{

        try{
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            //Fase 1: ricevi richiesta
            ServerSocket server = new ServerSocket(RICHIESTA_PORT);
            Socket accettaRichiesta = server.accept();
            ObjectInputStream ois = new ObjectInputStream(accettaRichiesta.getInputStream());
            Richiesta richiesta = (Richiesta) ois.readObject();
            System.out.println("Ricevuta" + richiesta);

            //Fase 2: invia richiesta ai partecipanti
            inviaRichiestaAiPartecipanti(richiesta, group);

            //Fase 3: riceve offerte dai partecipanti
            Offerta oVincente = riceviOfferte();
            System.out.println("Offerta vincente" + oVincente);

            //Fase 4: comunica il vincitore a tutti i partecipanti in multicast
            ObjectOutputStream oos = new ObjectOutputStream(accettaRichiesta.getOutputStream());
            oos.writeObject(oVincente);
            inviaEsitoGara(oVincente, group);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void inviaRichiestaAiPartecipanti(Richiesta richiesta, InetAddress group) {

        try{
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            String r = richiesta.getDescrizioneOpera() + " " + richiesta.getImportoMax();
            byte[] buf = r.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
            multicastSocket.send(packet);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Offerta riceviOfferte() {
        Offerta offertaVincente = null;

        try{
            ServerSocket server = new ServerSocket(OFFERTE_PORT);

            for(int i = 0; i < N; i++){
                Socket partecipante = server.accept();
                ObjectInputStream ois = new ObjectInputStream(partecipante.getInputStream());

                Offerta offertaPartecipante = (Offerta) ois.readObject();

                if(offertaVincente == null) offertaVincente = offertaPartecipante;

                else if((offertaPartecipante.getImportoRichiesto() < offertaVincente.getImportoRichiesto()) ||
                ((offertaPartecipante.getImportoRichiesto()) == offertaPartecipante.getImportoRichiesto() &&
                        offertaPartecipante.getIdPArtecipante() < offertaVincente.getIdPArtecipante()))
                offertaVincente = offertaPartecipante;

                partecipante.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return offertaVincente;
    }

    private static void inviaEsitoGara(Offerta oVincente, InetAddress group) {

        try{
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            String message = oVincente.getIdPArtecipante() + " - " + oVincente.getIdPArtecipante();

            byte[] buf = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
            multicastSocket.send(packet);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
