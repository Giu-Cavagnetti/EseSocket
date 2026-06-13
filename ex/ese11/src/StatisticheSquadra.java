package ese11.src;

public class StatisticheSquadra implements Comparable<StatisticheSquadra> {

    private String nomeSquadra;
    private int punti;
    private int differenzaReti;
    private int golFatti;
    private int golSubiti;

    public StatisticheSquadra(String nomeSquadra) {
        this.nomeSquadra = nomeSquadra;
        this.punti = 0;
        this.differenzaReti = 0;
        this.golFatti = 0;
        this.golSubiti = 0;
    }

    public StatisticheSquadra(String nomeSquadra, int punti, int differenzaReti, int golFatti, int golSubiti) {
        this.nomeSquadra = nomeSquadra;
        this.punti = punti;
        this.differenzaReti = differenzaReti;
        this.golFatti = golFatti;
        this.golSubiti = golSubiti;
    }

    public void aggiornaStatistiche(int golFatti, int golSubiti) {
        this.golFatti += golFatti;
        this.golSubiti += golSubiti;
        this.differenzaReti = this.golFatti - this.golSubiti;

        if (golFatti > golSubiti) {
            this.punti += 3;
        } else if (golFatti == golSubiti) {
            this.punti += 1;
        }
    }

    //li devo ordinare al contrario per avere la classifica in ordine decrescente
    @Override
    public int compareTo(StatisticheSquadra o) {
        int confrontoPunti = Integer.compare(o.punti, this.punti);
        if (confrontoPunti != 0) {
            return confrontoPunti;
        }

        int confrontoDifferenzaReti = Integer.compare(o.differenzaReti, this.differenzaReti);
        if (confrontoDifferenzaReti != 0) {
            return confrontoDifferenzaReti;
        }

        int confrontoGolFatti = Integer.compare(o.golFatti, this.golFatti);
        if (confrontoGolFatti != 0) {
            return confrontoGolFatti;
        }

        return this.nomeSquadra.compareTo(o.nomeSquadra);
    }

    public String getNomeSquadra() {
        return nomeSquadra;
    }

    public int getPunti() {
        return punti;
    }

    public int getDifferenzaReti() {
        return differenzaReti;
    }

    public int getGolFatti() {
        return golFatti;
    }

    public int getGolSubiti() {
        return golSubiti;
    }

    @Override
    public String toString() {
        return nomeSquadra +
                " | punti=" + punti +
                " | differenza reti=" + differenzaReti +
                " | gol fatti=" + golFatti +
                " | gol subiti=" + golSubiti;
    }
}