package ese10.src;

import java.io.Serializable;

public record Partecipazione(
        String idConcorso,
        String nome,
        String cognome,
        String codiceFiscale,
        String curriculum
) implements Serializable {
}