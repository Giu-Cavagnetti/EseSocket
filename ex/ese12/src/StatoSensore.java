package ese12.src;

import java.io.Serializable;

public record StatoSensore(
        String id,
        int numeroProgressivoStato,
        double temperaturaAria,
        double umiditaSuolo
) implements Serializable {

    @Override
    public String toString() {
        return id + "#" +
                numeroProgressivoStato + "#" +
                "T:" + temperaturaAria + " U:" + umiditaSuolo;
    }
}