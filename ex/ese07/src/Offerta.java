package ese07.src;

import java.io.Serializable;

public class Offerta implements Serializable {

    private int idPArtecipante;
    private int importoRichiesto;

    public Offerta(int idPArtecipante, int importoRichiesto) {
        this.idPArtecipante = idPArtecipante;
        this.importoRichiesto = importoRichiesto;
    }

    public int getIdPArtecipante() {
        return idPArtecipante;
    }

    public int getImportoRichiesto() {
        return importoRichiesto;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "idPArtecipante=" + idPArtecipante +
                ", importoRichiesto=" + importoRichiesto +
                '}';
    }
}
