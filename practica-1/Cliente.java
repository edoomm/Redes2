import java.io.DataOutputStream;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket cl = new Socket("localhost", 3000);
            System.out.println("Conexion con servidor exitosa");

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

            dos.writeInt(1);

            dos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
