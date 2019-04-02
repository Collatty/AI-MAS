import Components.State;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {

    public Client(BufferedReader serverMessages) {
        System.out.println();

    }

    public static void main(String[] args) {

        System.out.println("hello world");
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        try {
            State initialState = new State(serverMessages);
            System.out.println(initialState.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }




        Client client = new Client(serverMessages);

        //TODO main method for running client program
    }
}
