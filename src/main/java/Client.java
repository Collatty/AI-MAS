import Components.State;
import Utilities.LevelReader;

import java.io.BufferedReader;
import java.io.IOException;

public class Client {

    public Client(BufferedReader serverMessages) {
        System.out.println();

    }

    public static void main(String[] args) throws IOException{

        System.out.println("Ballefrans"); //CLIENTNAME - INITATING SERVER COMMUNICATION
        LevelReader levelReader = new LevelReader();
        try {
            State initialState = new State(levelReader.initial, levelReader.goal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Move(W);Move(E)");




        //Client client = new Client(serverMessages);

        //TODO main method for running client program
    }
}
