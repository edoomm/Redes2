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
    private boolean             jugando;

    // Constructor
    public Cliente() {
        iStream = null;
        oStream = null;
        scanner = new Scanner(System.in);
        jugando = true;
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
            int opcion = -2;
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Escoja su temática:\n----------\n0) Redes\n1) Geografia\n2) Mascotas\n-1) Salir");
            System.out.println("Opcion: ");
            while (opcion < -1 || opcion > 2) {
                opcion = scanner.nextInt();
                if ( opcion == -1 ) {
                    jugando = false;
                    System.exit(0);
                }
                if (opcion < 0 || opcion > 2) {
                    System.out.println("Opcion inválida, vuelva a intentarlo");
                    System.out.print("Opción: ");
                }
            }
            dataOutputStream.writeInt(opcion);
            dataOutputStream.flush();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void iniciarJuego() {
        try {
            while ( jugando ) {
                elegirTematica();
                Tablero tablero = (Tablero)iStream.readObject();
                while ( true ) {
                    System.out.println(tablero);
                    System.out.println("Ingresa fila y columna de inicio");
                    Posicion inicio = leerPosicion();
                    System.out.println("Ingresa fila y columna de fin");
                    Posicion fin = leerPosicion();

                    if ( tablero.descubrirPalabra(inicio, fin) ) {
                        if ( tablero.palabrasFaltantes() > 0 )
                            System.out.println("Yuju! \nDescubriste una palabra!\nRestan: " + tablero.palabrasFaltantes() + " palabras");
                        else {
                            System.out.println("Felicidades! \nGanaste!");
                            break;
                        }
                    } else {
                        System.out.println("No es una palabra correcta :(\nIntenta de nuevo");
                    }
                }
            }
            cerrarFlujos();
        } catch (Exception e) {
            System.err.println("Error al iniciar el juego");
            e.printStackTrace();
        }
    }
    
    private Posicion leerPosicion () {
        int fila = scanner.nextInt(), columna = scanner.nextInt();
        return new Posicion ( fila, columna);
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
