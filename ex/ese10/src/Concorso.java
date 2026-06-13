package ese10.src;

import java.time.Instant;

public record Concorso(String id, int numeroPostiVincitori, Instant scadenza) {
}
