package ese09.src;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        CentraleOperativa centraleOperativa = new CentraleOperativa();
        CentroControlloTraffico centroControlloTraffico = new CentroControlloTraffico();

        centraleOperativa.start();
        centroControlloTraffico.start();

        DatagramSocket socketAutobus1 = new DatagramSocket();
        DatagramSocket socketAutobus2 = new DatagramSocket();

        List<String> percorso1 = new ArrayList<>(
                Arrays.asList("ZonaA", "ZonaB", "ZonaC", "ZonaD")
        );

        List<String> percorso2 = new ArrayList<>(
                Arrays.asList("ZonaX", "ZonaY", "ZonaZ")
        );

        AutobusClient autobusClient1 = new AutobusClient("BUS1", percorso1);
        AutobusClient autobusClient2 = new AutobusClient("BUS2", percorso2);

        AutobusT autobus1 = new AutobusT(socketAutobus1, autobusClient1);
        AutobusT autobus2 = new AutobusT(socketAutobus2, autobusClient2);

        autobus1.start();
        autobus2.start();
    }
}