import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main class that will recieve an URL and will download its contents
 */

public class WGetExecutor {
    public static void main(String[] args) {
        
        List<String> resourcesToDownload;
        try {
            WGet wget = new WGet("http://148.204.58.221/axel/aplicaciones/");
            wget.start();
        } catch (Exception ioe) {
            System.err.println("Exception ocurred while trying to retrieve the input or output");
            ioe.printStackTrace();
        }
    }
}
