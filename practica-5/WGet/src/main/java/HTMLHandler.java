import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
* HTMLHandler class provides an interface to process
* an HTML document to get specified tags.
* 
* @version 1.0
*/
public class HTMLHandler {

    private static String getDocumentContent( String fileName ) throws FileNotFoundException, IOException {
        File file = new File(fileName);
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
    public static List<String> getTagsContent(String fileName) throws IOException {
        List<String> tags = null;
        String filePath = fileName.substring(0, fileName.lastIndexOf("/") + 1);
        try {
            String documentContent = getDocumentContent(fileName);
            tags = Stream.concat(
                    getResources(documentContent, "href", filePath).stream(), 
                    getResources(documentContent, "src", filePath).stream())
                    .collect(Collectors.toList());
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
    private static List<String> getResources(String content, String target, String filePath) {
        List<String> resources = new ArrayList<String>();
        // first index of the first href
        int index = content.indexOf(target);
        while (index >= 0) {
            // sets start and end indexes
            int start = content.indexOf("\"", index + 1) + 1;
            int end = content.indexOf("\"", start + 1);
            // getting the name of the resource
            String resourceName = content.substring(start, end);
            if (!resourceName.contains("/"))
                resources.add(filePath + resourceName);
            else if (resourceName.charAt(0) == '/')
                resources.add(filePath.substring(0, filePath.indexOf("/") + 1) + content.substring(start + 1, end));
            else 
                resources.add(filePath + resourceName);
            // updating index in order to look for the next resource
            index = content.indexOf(target, end + 1);
        }

        return resources;
    }

}
