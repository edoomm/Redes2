import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(3000);
            System.out.println("Servidor inciado... ");

            while (true) {
                Socket cl = s.accept();
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                int opcion = dis.readInt();

                System.out.println("Cliente escoge opcion " + opcion);

                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
