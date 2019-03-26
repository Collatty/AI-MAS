import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {

    public Client(BufferedReader serverMessages) {

    }

    public static void main(String[] args) {

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        Client client = new Client(serverMessages);
        //TODO main method for running client program
    }
}
