package ese13.src;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String idCliente;

    private final static String HOST_NAME = "currency.dimes.unical.it",
                                MULTICAST_ID = "239.255.0.1";

    private final static int SERVER_PORT = 2222,
                            MULTICAST_PORT = 5000;

    public Client(String idCliente){
        this.idCliente = idCliente;
    }

    public void mandaRichiesta(Richiesta richiesta){

        try{
            InetAddress inetAddress = InetAddress.getByName(HOST_NAME);
            Socket socket = new Socket(inetAddress, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(richiesta);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            dtoRispostaClient risposta = (dtoRispostaClient) in.readObject();

            System.out.println("Risposta ricevuta: " + risposta);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registraMulticast(){
        new RicevitoreMulticast().start();
    }

    public static void main (String args[]){
        Client client = new Client("001");
        client.registraMulticast();
        client.mandaRichiesta(new Richiesta(client.idCliente, "EUR", "DOL", 23.5));
    }
}
