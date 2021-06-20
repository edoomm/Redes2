
import Logic.FragmentFileSender;


public class SenderTest {
    public static void main(String[] args) {
        try {
            FragmentFileSender ffs1 = new FragmentFileSender(1234);
            ffs1.start();
            
            FragmentFileSender ffs2 = new FragmentFileSender(1235);
            ffs2.start();
            
            
            ffs1.join();
            ffs2.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
