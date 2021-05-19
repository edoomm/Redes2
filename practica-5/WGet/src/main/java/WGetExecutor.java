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
            resourcesToDownload = HTMLHandler.getTagsContent(new File("index.html"));
            System.out.println(resourcesToDownload.toString());
        } catch (IOException ioe) {
            System.err.println("Exception ocurred while trying to retrieve the input or output");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("Exception ocurred");
            e.printStackTrace();
        }
    }
}
