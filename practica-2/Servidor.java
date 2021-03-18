import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Clase que recibirá clientes y escogerá y mandará un objeto de tipo Tablero 
 * al cliente para que del lado del cliente se realice la lógica del juego
 */

public class Servidor {
    private Socket              cli;
    private ServerSocket        serv;
    private ObjectOutputStream  oos;
    private ObjectInputStream   ois;
    private int                 tematica;
    private final String[]      tematicas = {"Redes", "Geografia", "Mascotas"};
    private final String[]      palabrasTematica1 = {"Protocolos", "Internet"};
    private final String[]      palabrasTematica2 = {"Protocolos", "Internet"};

    private void cerrarFlujos() {
        try {
            oos.close();
            ois.close();
            cli.close();
            serv.close();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al tratar de cerrar los flujos");
        }
    }

    private void obtenerTematica() {
        try {
            DataInputStream dis = new DataInputStream(cli.getInputStream());
            System.out.println("Esperando temática que escogerá el usuario...");
            tematica = dis.readInt();
            System.out.println("Temática escogida: " + tematicas[tematica]);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al obtener tematica");
            e.printStackTrace();
        }
    }

    private void iniciarJuego() {
        obtenerTematica();
        try {

            System.out.println("Hasta aca vamo bien xd");

            cerrarFlujos();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al iniciar juego");
            e.printStackTrace();
        }
    }

    private void iniciar() {
        try {
            int pto = 1234;
            serv = new ServerSocket(pto);

            System.out.println("Servidor iniciado... Esperando clientes... ");
            cli = serv.accept();
            ois = new ObjectInputStream(cli.getInputStream());
            oos = new ObjectOutputStream(cli.getOutputStream());

            iniciarJuego();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al iniciar clase Servidor");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
