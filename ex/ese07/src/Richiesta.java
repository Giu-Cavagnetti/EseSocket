package ese07.src;

import java.io.Serializable;

public class Richiesta implements Serializable {

    private String descrizioneOpera;
    private double importoMax;

    public Richiesta(String descrizioneOpera, double importoMax) {
        this.descrizioneOpera = descrizioneOpera;
        this.importoMax = importoMax;
    }


    public String getDescrizioneOpera() {
        return descrizioneOpera;
    }

    public double getImportoMax() {
        return importoMax;
    }

    @Override
    public String toString() {
        return "Richiesta{" +
                "descrizioneOpera='" + descrizioneOpera + '\'' +
                ", importoMax=" + importoMax +
                '}';
    }
}
