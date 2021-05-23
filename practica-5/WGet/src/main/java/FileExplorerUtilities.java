
import java.io.File;
import java.io.FileWriter;


public class FileExplorerUtilities {
    public static boolean existsFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
    
    public static void createFilePath(File file) {
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
    }
}
