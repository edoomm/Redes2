import java.io.File;
import java.util.Scanner;

public class Consola {
    private String ubicacion;
    private Scanner lector;
    private EjecutorDeComandos ejecutor;
    
    public Consola () {
        ubicacion = "";
        lector = new Scanner (System.in);
        ejecutor = new EjecutorDeComandos(ubicacion);
    }
    
    private void mostrarUbicacion() {
        File file = new File(ubicacion);
        String rutaAbsoluta = file.getAbsolutePath();
        
        System.out.println(rutaAbsoluta + " $: ");
        //leerComando();
    }
    
    public void leerComando () {
        String comando = "";
        while ( !comando.equals("exit") ) {
            mostrarUbicacion();
            comando = lector.nextLine();
            
            ejecutor.ejecutarComando(comando);
        }
    }
    
    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.leerComando();
    }
    
}
