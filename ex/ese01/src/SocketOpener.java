package ese01.src;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketOpener extends Thread {

    private String host;
    private int port;
    private Socket socket;

    public SocketOpener(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public static Socket openSocket(String host, int port, int timeout) {
        SocketOpener opener = new SocketOpener(host, port);
        opener.start();

        try {
            opener.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return opener.getSocket();
    }

    public void run() {
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Host sconosciuto: " + host);
        } catch (IOException e) {
            System.out.println("Impossibile connettersi a " + host + ":" + port);
        }
    }
}