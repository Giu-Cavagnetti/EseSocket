package ese11.src;

import java.util.HashMap;
import java.util.Map;

public class Classifica {

    private final Map<String, StatisticheSquadra> squadre = new HashMap<>();

    public void aggiornaDaPartita(dtoClient dto) {
        String casa = dto.squadraCasa();
        String ospite = dto.squadraOspite();

        int golCasa = dto.golCasa();
        int golOspite = dto.golOspite();

        squadre.putIfAbsent(casa, new StatisticheSquadra(casa));
        squadre.putIfAbsent(ospite, new StatisticheSquadra(ospite));

        StatisticheSquadra statCasa = squadre.get(casa);
        StatisticheSquadra statOspite = squadre.get(ospite);

        statCasa.aggiornaStatistiche(golCasa, golOspite);
        statOspite.aggiornaStatistiche(golOspite, golCasa);

    }

    public Map<String, StatisticheSquadra> getSquadre() {
        return squadre;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("Classifica squadre");

        squadre.values()
                .stream()
                .sorted()
                .forEach(stat -> ret.append("\n").append(stat));

        return ret.toString();
    }
}