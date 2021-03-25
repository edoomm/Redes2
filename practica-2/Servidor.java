import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                                                      {"Relieve", /*"Altitud", "Clima", "Acantilado", "Meseta", "Hidrografía", "Archipielago"*/}, // Geografía
                                                      {"Hamster", "Aves", "Peces", "Gato", "Serpiente", "Perro", "Tortuga", "Conejo"}  // Mascotas 
                                                      };

    private void cerrarFlujos() {
        try {
            oos.close();
            ois.close();
            cli.close();
            serv.close();
        } catch (Exception e) {
            System.out.println("Error al tratar de cerrar los flujos");
            e.printStackTrace();
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
            System.out.println("Error al obtener tematica");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un subarreglo aleatorio de un arreglo dado
     * @param arr   El arreglo completo
     * @param n     El tamño del subarreglo
     * @return      El subarreglo de tamaño n con elementos aleatorios de arr
     */
    private String[] obtenerSubArregloAleatorio(String[] arr, int n) {
        // verifica que el número dado no sea mayor al tamaño del arreglo dado
        if (n > arr.length)
            return arr;

        // crea una lista para poder mezclarla
        List<String> list = new ArrayList<String>(arr.length);
        for(String s : arr)
            list.add(s);
        Collections.shuffle(list);

        // obtiene un subarreglo de tamaño n, para lo que se obtuvo en la lista
        String[] subArr = new String[n];
        for (int i = 0; i < subArr.length; i++) {
            subArr[i] = list.get(i);
        }

        return subArr;
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
            System.out.println("Error al iniciar clase Servidor");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
