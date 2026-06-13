package ese08.src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class GestoreClient extends Thread {
    private Socket clientSocket;
    private ServerElettorale serverElettorale;

    public GestoreClient(Socket clientSocket, ServerElettorale serverElettorale) {
        this.clientSocket = clientSocket;
        this.serverElettorale = serverElettorale;
    }

    public void run(){
        try{
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            while(true){
                VotoComune voto = (VotoComune) ois.readObject();
                System.out.println("Ricevuto: " + voto);
                serverElettorale.registraVoto(voto);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try{
                clientSocket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}
