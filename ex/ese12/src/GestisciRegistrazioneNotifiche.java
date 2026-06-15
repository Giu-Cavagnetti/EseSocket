package ese12.src;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class GestisciRegistrazioneNotifiche extends Thread {

    private final Socket socket;
    private final ServerAgricolo serverAgricolo;

    public GestisciRegistrazioneNotifiche(Socket socket, ServerAgricolo serverAgricolo) {
        this.socket = socket;
        this.serverAgricolo = serverAgricolo;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())
        ) {

            String idSensore = (String) objectInputStream.readObject();
            InetAddress indirizzoClient = socket.getInetAddress();

            serverAgricolo.registraSensorePerNotifiche(idSensore, indirizzoClient);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}