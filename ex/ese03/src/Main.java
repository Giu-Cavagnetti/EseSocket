package ese03.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Uso:");
            System.out.println("java ese03.src.Main portaLocale [portaPeer1 portaPeer2 ...]");
            return;
        }

        int portaLocale = Integer.parseInt(args[0]);

        List<Peer> peers = new ArrayList<>();

        for (int i = 1; i < args.length; i++) {
            int portaPeer = Integer.parseInt(args[i]);
            peers.add(new Peer("localhost", portaPeer));
        }

        ChatServer cs = new ChatServer(peers, portaLocale);

        cs.startServer();
        cs.startConsole();
    }
}