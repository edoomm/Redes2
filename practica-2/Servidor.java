import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Clase que recibirá clientes y escogerá y mandará un objeto de tipo Tablero 
 * al cliente para que del lado del cliente se realice la lógica del juego
 */

public class Servidor {
    private ObjectOutputStream  oos;
    private ObjectInputStream   ois;
    private int                 tematica;
    private final String[]      tematicas = {"Redes", ""};
    private final String[]      palabrasTematica1 = {"Protocolos", "Internet"};
    private final String[]      palabrasTematica2 = {"Protocolos", "Internet"};

    private static void iniciarJuego() {
        
    }

    private static void iniciar() {
        try {
            int pto = 1234;
            ServerSocket s = new ServerSocket(pto);
            System.out.println("Servidor iniciado... Esperando clientes... ");
            Socket c = s.accept();

        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public static void main(String[] args) {
        iniciar();
    }
}
