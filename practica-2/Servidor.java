import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

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
    private final String[][]    palabrasTematicas = {
                                                      {"Protocolos", "Internet", "Topologia", "Ethernet", "Checksum", "Paquete", "Encabezado"}, // Redes
                                                      {"Relieve", "Altitud", "Clima", "Acantilado", "Meseta", "Hidrografía", "Archipielago"}, // Geografía
                                                      {"Hamster", "Aves", "Peces", "Gato", "Serpiente", "Perro", "Tortuga", "Conejo"}  // Mascotas 
                                                      };

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
            if ( tematica == -1 ) return;
            System.out.println("Temática escogida: " + tematicas[tematica]);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al obtener tematica");
            e.printStackTrace();
        }
    }

    private void iniciarJuego() {
        try {
            while ( true ) {
                obtenerTematica();
                if ( tematica == -1 ) break;
                enviarTablero();
            }
            //cerrarFlujos();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error al iniciar juego");
            e.printStackTrace();
        }
    }
    
    private void enviarTablero() throws IOException {
        GeneradorDeTablero generador = new GeneradorDeTablero(tematicas[tematica], palabrasTematicas[tematica]);
        Tablero tablero = generador.generarTablero();
        System.out.println("Enviado tablero: ");
        System.out.println(tablero);
        oos.writeObject(tablero);
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
