import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final String[]      tematicas = {"Redes", "Geografia", "Animales"};
    private final String[][]    palabrasTematicas = {
        {"PROTOCOLO", "INTERNET", "PODCAST", "BROADCAST", "FACEBOOK", "CHAT", "CONEXION", "EXTRANET", "INFRANET", "WAN", 
        "WWW", "ARP", "OSPF", "UNICAST", "DHCP", "PAQUETE", "ETHERNET", "SERVIDOR", "CLIENTE", "SOCKET"},
        {"ISRAEL", "NIGERIA", "ITALIA", "ARGENTINA", "PERU", "TURQUIA", "LAOS", "AUSTRALIA", "MEXICO", "RUMANIA",
        "COAHUILA", "ZACATECAS", "CHIHUAHUA", "SINALOA", "CAMPECHE", "COLIMA", "VERACRUZ", "CDMX", "PUEBLA", "GUERRERO"},
        {"PERRO", "GATO", "CABALLO", "IGUANA", "PANDA", "LEON", "LOBO", "VACA", "TORO", "PEZ", 
        "COLIBRI", "CACATUA", "RINOCERONTE", "ELEFANTE", "ZOPILOTE", "MAPACHE", "MOSCA", "NUTRIA", "OCELOTE", "AVESTRUZ"}
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
        obtenerTematica();
        try {
            String tematica = this.tematicas[this.tematica];
            String[] palabras = obtenerSubArregloAleatorio(palabrasTematicas[this.tematica], 15);

            // TODO: Acá irá la construcción del objeto tablero, será algo así como
            // new Tablero(tematica, palabras)

            cerrarFlujos();
        } catch (Exception e) {
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
            System.out.println("Error al iniciar clase Servidor");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
