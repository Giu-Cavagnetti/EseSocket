package ese13.src;

import java.io.Serializable;

public record dtoRispostaClient(String messaggio, double importo, double tassoCambio) implements Serializable {
}
