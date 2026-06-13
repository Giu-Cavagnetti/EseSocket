package ese10.src;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MainServer {

    public static void main(String[] args) {
        List<Concorso> concorsi = new ArrayList<>();

        concorsi.add(new Concorso(
                "CONCORSO1",
                2,
                Instant.now().plusSeconds(60)
        ));

        concorsi.add(new Concorso(
                "CONCORSO2",
                1,
                Instant.now().plusSeconds(90)
        ));

        Server server = new Server(concorsi);
        server.avvia();
    }
}