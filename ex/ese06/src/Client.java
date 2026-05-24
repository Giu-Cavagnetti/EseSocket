package ese06.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private int serverPort;
    private InetAddress serverAddress;

    public Client(int serverPort, InetAddress serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public void saluta() {
        try {
            Socket s = new Socket(serverAddress, serverPort);
            System.out.println("Connessione con il server riuscita");

            BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            String saluto = "Ciao server";
            out.println(saluto);

            String risposta = in.readLine();
            System.out.println("Risposta ricevuta dal server: " + risposta);

            s.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        Client client = new Client(9000, serverAddress);

        client.saluta();
    }
}