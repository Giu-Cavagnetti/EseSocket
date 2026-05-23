package ese03.src;

public class Peer {

    private String host;
    private int port;

    public Peer(String host, int port){
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String toString() {
        return host + ":" + port;
    }
}
