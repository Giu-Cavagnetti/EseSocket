package ese13.src;

import java.io.Serializable;

public record Richiesta(String idCliente,
                        String codicePartenza,
                        String codiceDestinazione,
                        double importo)
        implements Serializable {

    @Override
    public String toString(){
        return idCliente + ";" +
                codicePartenza + ";" +
                codiceDestinazione + ";" +
                importo;
    }
}
