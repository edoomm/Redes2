import Logic.MulticastServer;
import Logic.MulticastClient;
import InformationObjects.RMIInformation;
import java.io.IOException;

public class MulticastServerTest {
    public static void main(String[] args) {
        try {
            MulticastServer multicastServer = 
                    new MulticastServer(new RMIInformation("Hola", 1234));
            
            multicastServer.start();
            
            
            MulticastClient multicastClient = new MulticastClient();
            
            multicastClient.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
