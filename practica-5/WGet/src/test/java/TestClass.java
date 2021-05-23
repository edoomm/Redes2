
import java.net.MalformedURLException;
import java.net.URL;

public class TestClass {
    public static void main(String[] args) {
        try {
            String fileName = "148.204.58.221/axel/aplicaciones/sockets/java/Cliente_O.java";
            String filePath = fileName.substring(0, fileName.lastIndexOf("/") + 1);
            filePath = fileName.substring(0, fileName.indexOf("/") + 1);
            System.out.println("PATH: " + filePath);
            URL url = new URL("http://148.204.58.221/axel/aplicaciones/sockets/java/Cliente_O.java");
            System.out.println(url.getProtocol());
            System.out.println(url.openConnection().getContentType().contains("html"));
            //System.out.println(url.getHost());
        } catch (Exception murle) {
            murle.printStackTrace();
        }
    }
}
