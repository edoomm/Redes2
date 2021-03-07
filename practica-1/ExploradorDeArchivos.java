
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExploradorDeArchivos {
    
    public ExploradorDeArchivos () {
        
    }
    
    public ArrayList<String> listarDirectorio (String ruta) {
        File archivo = new File(ruta);
        File[] archivos = archivo.listFiles();
        ArrayList<String> listadoArchivos = new java.util.ArrayList<>();
        try {
            for ( int i = 0 ; i < archivos.length ; i++ ) {
                listadoArchivos.add(archivos[i].getCanonicalPath());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return listadoArchivos;
    }
            
}
