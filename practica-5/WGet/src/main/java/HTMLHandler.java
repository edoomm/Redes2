

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
* HTMLHandler class provides an interface to process
* an HTML document to get specified tags.
* 
* @version 1.0
*/
public class HTMLHandler {
    
    
    public static String getDocumentContent( File file ) throws FileNotFoundException, IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String fileContent = "";
        String readLine;
        while ( (readLine = fileReader.readLine()) != null ) {
            fileContent += readLine;
        }
        return fileContent;
    }
    
    public static List<String> getTagsContent( List<String> tags, File file) throws IOException {
        String documentContent = getDocumentContent(file);
        System.out.println(documentContent);
        return tags;
    }
}
