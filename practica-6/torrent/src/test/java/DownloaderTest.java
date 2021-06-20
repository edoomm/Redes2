
import InformationObjects.FileInformation;
import Logic.FragmentedFileDownloader;
import java.io.File;
import java.util.ArrayList;


public class DownloaderTest {
    public static void main(String[] args) {
        FileInformation fi1 = new FileInformation(
            "127.0.0.1",
            new File("C:\\Users\\Donaldo\\Desktop\\Server2\\file2.txt"),
            "",
            1234);
        
        FileInformation fi2 = new FileInformation(
            "127.0.0.1",
            new File("C:\\Users\\Donaldo\\Desktop\\Server1\\file2.txt"),
            "",
            1235);
        
        ArrayList<FileInformation> list = new ArrayList<FileInformation>();
        list.add(fi1); list.add(fi2);
        
        FragmentedFileDownloader ffd = new FragmentedFileDownloader(
            list,
            "C:\\Users\\Donaldo\\Desktop\\Server3\\");
        
        ffd.downloadFragments();
        System.out.println("Finished downloading");
    }
}
