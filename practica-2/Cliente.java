import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private Socket              socket;
    private ObjectInputStream   iStream;
    private ObjectOutputStream  oStream;
    private Scanner             scanner;

    // Constructor
    public Cliente() {
        iStream = null;
        oStream = null;
        scanner = new Scanner(System.in);
    }

    // Métodos

    private void cerrarFlujos() {
        try {
            oStream.close();
            iStream.close();
            socket.close();
            scanner.close();
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error al cerrar los flujos");
            e.printStackTrace();
        }
    }

    private void elegirTematica() {
        try {
            int opcion = -1;
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Escoja su temática:\n----------\n0) Redes\n1) Geografia\n2) Mascotas");
            System.out.print("Opcion: ");
            while (opcion < 0 || opcion > 2) {
                opcion = scanner.nextInt();
                if (opcion < 0 || opcion > 2) {
                    System.out.println("Opcion invalida, vuelva a intentarlo");
                    System.out.print("Opcion: ");
                }
            }
            dataOutputStream.writeInt(opcion);

            dataOutputStream.flush();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void iniciarJuego() {
        elegirTematica();
        try {
            System.out.println("Igual hasta acá vamo bien jeje");

            cerrarFlujos();
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error al iniciar el juego");
            e.printStackTrace();
        }
    }

    private void conectarse() {
        try {
            int pto = 1234;
            socket = new Socket("localhost", pto);

            oStream = new ObjectOutputStream(socket.getOutputStream());
            iStream = new ObjectInputStream(socket.getInputStream());

            if (socket != null) {
                iniciarJuego();
            } else {
                System.out.println("Socket indefinido. Error al conectarse");
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al conectarse al servidor");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Cliente().conectarse();
    }
}
