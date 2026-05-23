package ese03.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {

    private List<Peer> peers;
    private int port;
    private List <ChatConnection> connessioniEffettuate;

    public ChatServer(List<Peer> peers, int port) {
        this.peers = peers;
        this.port = port;
        this.connessioniEffettuate = new ArrayList<ChatConnection>();
    }

    public void addPeer(Peer p){
        peers.add(p);
    }


    public void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server in ascolto sulla porta " + port);

                while (true) {
                    Socket socket = serverSocket.accept();

                    System.out.println("Nuova connessione ricevuta da " + socket.getInetAddress());

                    ChatConnection ch = new ChatConnection(socket);
                    connessioniEffettuate.add(ch);

                    ch.startReading();
                }

            } catch (IOException e) {
                System.out.println("Errore nel server: " + e.getMessage());
            }
        });

        serverThread.start();
    }

    private void visualizeCommand(){
        System.out.println("Scegli l'operazione da effettuare");
        System.out.println("1) Visualizza tutti i peers");
        System.out.println("2) Connettiti ad un peer");
        System.out.println("3) Invia un messaggio");
        System.out.println("4) ESCI");

    }

    public void startConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            visualizeCommand();

            int azione = Integer.parseInt(scanner.nextLine());

            switch (azione) {
                case 1 -> {
                    System.out.println("Lista peers:");
                    for (int i = 0; i < peers.size(); i++) {
                        System.out.println(i + ") " + peers.get(i));
                    }
                }

                case 2 -> {
                    System.out.println("Seleziona quale peer: ");

                    for (int i = 0; i < peers.size(); i++) {
                        System.out.println(i + ") " + peers.get(i));
                    }

                    int peer = Integer.parseInt(scanner.nextLine());

                    try {
                        Peer p = peers.get(peer);

                        Socket socket = new Socket(p.getHost(), p.getPort());

                        ChatConnection ch = new ChatConnection(socket);
                        connessioniEffettuate.add(ch);

                        ch.startReading();

                        System.out.println("Connessione riuscita con " + p);

                    } catch (IOException e) {
                        System.out.println("Errore durante la connessione: " + e.getMessage());
                    }
                }

                case 3 -> {
                    if (connessioniEffettuate.isEmpty()) {
                        System.out.println("Nessuna connessione attiva.");
                        break;
                    }

                    System.out.println("Connessioni attive:");
                    for (int i = 0; i < connessioniEffettuate.size(); i++) {
                        System.out.println(i + ") " + connessioniEffettuate.get(i));
                    }

                    System.out.println("Scegli la connessione a cui inviare:");
                    int sceltaConnessione = Integer.parseInt(scanner.nextLine());

                    if (sceltaConnessione < 0 || sceltaConnessione >= connessioniEffettuate.size()) {
                        System.out.println("Connessione non valida.");
                        break;
                    }

                    System.out.println("Scrivi il messaggio:");
                    String messaggio = scanner.nextLine();

                    ChatConnection ch = connessioniEffettuate.get(sceltaConnessione);
                    ch.send(messaggio);
                }

                case 4 -> {
                    System.out.println("Uscita...");
                    running = false;
                }

                default -> {
                    System.out.println("Scelta non valida.");
                }
            }
        }
        scanner.close();
    }
}
