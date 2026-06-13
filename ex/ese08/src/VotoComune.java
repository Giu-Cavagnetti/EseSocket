package ese08.src;

import java.io.Serializable;
import java.util.Map;

public record VotoComune(

        String nomeComune,
        int idSezione,
        Map<String, Integer> votiPerPartito)

        implements Serializable {
}
