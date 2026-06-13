package ese11.src;

import java.io.Serializable;
import java.time.LocalDateTime;

public record dtoClient (String id,
                         String codiceOperazione,
                         String codicePartita,
                         String squadraCasa,
                         String squadraOspite,
                         int golCasa,
                         int golOspite,
                         LocalDateTime dataOra)
implements Serializable{

    @Override
    public String toString() {
        return id + " " +
                codiceOperazione + " " +
                codicePartita + " " +
                squadraCasa + " " +
                squadraOspite + " " +
                golCasa + " " +
                golOspite + " " +
                dataOra;
    }

}
