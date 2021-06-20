import Logic.MulticastClient;
import java.io.IOException;

public class MulticastClientTest {
    public static void main(String[] args) {
        try {
            MulticastClient multicastClient = 
                    new MulticastClient();
            multicastClient.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

