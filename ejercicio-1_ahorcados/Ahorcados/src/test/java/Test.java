
import java.util.logging.Level;
import java.util.logging.Logger;


public class Test {
    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo: " + (fin - inicio) / 1000);
    }
}
