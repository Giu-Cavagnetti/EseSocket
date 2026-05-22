package ese02.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientHttp {

    private static int port = 8080;

    public static void main(String[] args){

        try{
            Socket s = new Socket("localhost", port);
            System.out.println("Client connesso al server");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            //Richiesta la server
            out.println("GET / HTTP/1.0");
            out.println();

            String line;

            //Risposta dal server
            while((line = in.readLine()) != null){
                System.out.println(line);

            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
