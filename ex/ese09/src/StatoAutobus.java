package ese09.src;

public class StatoAutobus {

    private String id;
    private String posizioneAttuale;
    private long ultimoCambioPosizione;
    private long ultimoMessaggioRicevuto;
    private boolean allarmeInviato;

    public StatoAutobus(String id, String posizioneAttuale, long ultimoCambioPosizione) {
        this.id = id;
        this.posizioneAttuale = posizioneAttuale;
        this.ultimoCambioPosizione = ultimoCambioPosizione;
        this.ultimoMessaggioRicevuto = System.currentTimeMillis();
        this.allarmeInviato = false;
    }

    public String getId() {
        return id;
    }

    public String getPosizioneAttuale() {
        return posizioneAttuale;
    }

    public long getUltimoCambioPosizione() {
        return ultimoCambioPosizione;
    }

    public long getUltimoMessaggioRicevuto() {
        return ultimoMessaggioRicevuto;
    }

    public boolean isAllarmeInviato() {
        return allarmeInviato;
    }

    public void setAllarmeInviato(boolean allarmeInviato) {
        this.allarmeInviato = allarmeInviato;
    }

    public void aggiorna(String nuovaPosizione, long ultimoAggiornamentoRicevuto) {
        this.ultimoMessaggioRicevuto = System.currentTimeMillis();

        if (!this.posizioneAttuale.equals(nuovaPosizione)) {
            this.posizioneAttuale = nuovaPosizione;
            this.ultimoCambioPosizione = ultimoAggiornamentoRicevuto;
            this.allarmeInviato = false;
        }
    }
}