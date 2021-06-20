
import java.io.IOException;
import java.io.RandomAccessFile;

public class Writer {
    String content;
    
    public Writer() { 
        content = "This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test This is a relatively long string to test";
    }
    
    public void writeToFile() {
        System.out.println("content length: " + content.length());
        int threadNo = 2;
        int fragmentLength = content.length() / threadNo;
        for (int i = 0 ; i < threadNo ; i++) {
            new Thread(new WriterThread(i*fragmentLength, fragmentLength)).start();
        }
    }
    
    class WriterThread implements Runnable {
        private int initialPosition;
        private int fragmentLength;
        private RandomAccessFile raf;
        
        public WriterThread(int initialPosition, int fragmentLength) {
            this.initialPosition = initialPosition;
            this.fragmentLength = fragmentLength;
            try {
                raf = new RandomAccessFile("test.txt", "rw");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.out.println("From " + initialPosition + " end " + (initialPosition + fragmentLength));
        }
        
        @Override
        public void run() {
            try {
                raf.seek(initialPosition);
                raf.write(content.getBytes(), initialPosition, fragmentLength);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        Writer w = new Writer();
        w.writeToFile();
    }
}
