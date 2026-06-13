package ese09.src;

import java.io.Serializable;
import java.util.List;

public class AutobusClient implements Serializable {

    private String id;
    private String posizioneAttuale;
    private List<String> posizioniDaCoprire;
    private long ultimoAggiornamentoPosizione;

    public AutobusClient(String id, List<String> posizioniDaCoprire) {
        this.id = id;
        this.posizioniDaCoprire = posizioniDaCoprire;

        if (posizioniDaCoprire != null && !posizioniDaCoprire.isEmpty()) {
            this.posizioneAttuale = posizioniDaCoprire.get(0);
            posizioniDaCoprire.remove(0);
        } else {
            this.posizioneAttuale = "SCONOSCIUTA";
        }

        this.ultimoAggiornamentoPosizione = System.currentTimeMillis();
    }

    public List<String> getPosizioniDaCoprire() {
        return posizioniDaCoprire;
    }

    public String setPosizioneSuccessiva() {
        if (posizioniDaCoprire == null || posizioniDaCoprire.isEmpty()) {
            return posizioneAttuale;
        }

        posizioneAttuale = posizioniDaCoprire.get(0);
        posizioniDaCoprire.remove(0);

        return posizioneAttuale;
    }

    public String getId() {
        return id;
    }

    public String getPosizioneAttuale() {
        return posizioneAttuale;
    }

    public long getUltimoAggiornamentoPosizione() {
        return ultimoAggiornamentoPosizione;
    }

    public void setUltimoAggiornamentoPosizione(long ultimoAggiornamentoPosizione) {
        this.ultimoAggiornamentoPosizione = ultimoAggiornamentoPosizione;
    }

    public String toMessage() {
        return id + ";" + posizioneAttuale + ";" + ultimoAggiornamentoPosizione;
    }

    @Override
    public String toString() {
        return "AutobusClient{" +
                "id='" + id + '\'' +
                ", posizioneAttuale='" + posizioneAttuale + '\'' +
                ", ultimoAggiornamentoPosizione=" + ultimoAggiornamentoPosizione +
                '}';
    }
}