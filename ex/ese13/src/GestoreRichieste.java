package ese13.src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestoreRichieste extends Thread{

    private Server server;
    private Socket socket;

    public GestoreRichieste(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    public void run(){
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            Richiesta richiesta = (Richiesta) objectInputStream.readObject();

            dtoRispostaClient risposta = server.calcolaRichiesta(richiesta);

            objectOutputStream.writeObject(risposta);
            objectOutputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try{
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
