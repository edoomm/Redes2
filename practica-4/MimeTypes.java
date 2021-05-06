public class MimeTypes {
    public static final String PDF = "application/pdf";
    public static final String HTML = "text/html";

    /**
     * Obtains the correspondant MIME Type of a given extension
     * Note: The method is implemented with if stataments due to the original project runs in a lower version than 7
     * @param fileExtension The extension of a file
     * @return The MIME Type in a string
     */
    public static String getMimeType(String fileExtension) {
        String ext = fileExtension.toLowerCase();

        if (ext == "pdf")
            return PDF;
        else if (ext == "html" || ext == "htm" || ext == "stm")
            return HTML;
        else
            return null;
    }
}
