package ese06.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Calendar;

public class Server {

    private int serverPort;
    private Calendar deadline;
    private Accepter accepter;

    public Server(int serverPort, Calendar deadline) {
        this.serverPort = serverPort;
        this.deadline = deadline;

        accepter = new Accepter(serverPort);
        accepter.start();
    }

    public class Accepter extends Thread {

        private int port;
        private boolean accetta;
        private ServerSocket serverSocket;

        public Accepter(int port) {
            this.port = port;
            this.accetta = true;

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            while (accetta) {
                try {
                    Calendar now = Calendar.getInstance();

                    long tempoRimanente = deadline.getTimeInMillis() - now.getTimeInMillis();

                    if (tempoRimanente <= 0) {
                        accetta = false;
                        System.out.println("Tempo a disposizione per salutare terminato");
                        break;
                    }

                    serverSocket.setSoTimeout((int) tempoRimanente);

                    Socket clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String msg = in.readLine();
                    System.out.println("Messaggio ricevuto: " + msg);

                    out.println("Ciao dal server");
                    clientSocket.close();

                } catch (SocketTimeoutException e) {
                    accetta = false;
                    System.out.println("Tempo a disposizione per salutare terminato");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int serverPort = 9000;

        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.SECOND, 30);

        Server server = new Server(serverPort, deadline);

        System.out.println("Server avviato sulla porta " + serverPort);
        System.out.println("Accetto saluti per 30 secondi.");
    }
}