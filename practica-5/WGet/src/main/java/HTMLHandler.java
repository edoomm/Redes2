import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    
    /**
     * Gets every resource that should be downloaded in a file that uses these resources
     * @param   file        The file to analyze its contents
     * @return              The resources that should be downloaded
     * @throws  IOException
     */
    public static List<String> getTagsContent(File file) throws IOException {
        List<String> tags = null;

        try {
            String documentContent = getDocumentContent(file);
            System.out.println(documentContent);
            tags = getResources(documentContent);
        } catch (FileNotFoundException fnfe) {
            System.err.println("Exception ocurred because the file couldn't be found");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Exception ocurred while trying to retrieve the input or output stream");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("Excepction ocurred");
            e.printStackTrace();
        }

        return tags;
    }

    /**
     * Gets the name of the files that are within a document content (HTML)
     * @param content   The document to analyze
     * @return          A list of all names of the resources
     */
    private static List<String> getResources(String content) {
        List<String> resources = new ArrayList<String>();
        String href = "href";

        // first index of the first href
        int index = content.indexOf(href);
        while (index >= 0) {
            // sets start and end indexes
            int start = content.indexOf("\"", index + 1) + 1;
            int end = content.indexOf("\"", start + 1);
            // getting the name of the resource
            resources.add(content.substring(start, end));
            // updating index in order to look for the next resource
            index = content.indexOf(href, end + 1);
        }

        return resources;
    }

}
