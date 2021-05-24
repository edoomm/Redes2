
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileExplorerUtilities {
    public static boolean existsFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
    
    public static void createFilePath(File file) throws IOException {
        System.out.println("Creando path: " + file.getCanonicalPath());
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
    }
}
