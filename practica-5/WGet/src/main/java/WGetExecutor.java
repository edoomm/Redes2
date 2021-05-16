
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WGetExecutor {
    public static void main(String[] args) {
        try {
            HTMLHandler.getTagsContent(null, new File("index.html"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
