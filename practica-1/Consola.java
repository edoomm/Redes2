import java.io.File;
import java.util.Scanner;

public class Consola {
    private Scanner lector;
    private EjecutorDeComandos ejecutor;
    
    public Consola () {
        lector = new Scanner (System.in);
        ejecutor = new EjecutorDeComandos(System.getProperty("user.home") + "\\desktop");
    }
    
    // Muestra el prompt en consola
    private void mostrarPrompt() {
        String ubicacion = ejecutor.obtenerUbicacion();
        System.out.println(ubicacion + " $: ");
    }
    
    // Ciclo infinito para leer los comandos
    // Ver "EjecutorDeComandos.ejecutarComando()" para ver
    // los comandos posibles
    public void leerComando () {
        String comando = "";
        String respuesta = "";
        while ( !comando.equals("exit") ) {
            mostrarPrompt();
            comando = lector.nextLine();
            respuesta = ejecutor.ejecutarComando(comando);
            System.out.println(respuesta);
        }
    }
    
    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.leerComando();
    }
    
}
