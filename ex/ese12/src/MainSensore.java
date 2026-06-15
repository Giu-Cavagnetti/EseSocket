package ese12.src;

public class MainSensore {

    public static void main(String[] args) {

        Sensore sensore = new Sensore("S1");

        sensore.registraNotifiche();

        sensore.ascoltaNotifiche();

        sensore.inviaStato(25.0, 40.0);
    }
}