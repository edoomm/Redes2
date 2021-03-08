
import java.io.Serializable;
import java.util.ArrayList;


// Clase que guarda los archivos de un directorio así como su tipo de archivo
public class Directorio implements Serializable {
    private ArrayList<MetainformacionArchivo> archivos;
    
    public Directorio () {
        archivos = new java.util.ArrayList<>();
    }

    public ArrayList<MetainformacionArchivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(ArrayList<MetainformacionArchivo> archivos) {
        this.archivos = archivos;
    }

    public void agregarArchivo ( MetainformacionArchivo infoArchivo ) {
        archivos.add(infoArchivo);
    }

    @Override
    public String toString() {
        String texto = "";
        for ( int i = 0 ; i < archivos.size() ; i++ ) {
            if ( archivos.get(i).esDirectorio() )
                texto += "  " + archivos.get(i).getNombreArchivo() + "/\n";
            else
                texto += "  " + archivos.get(i).getNombreArchivo() + "\n";
        }
        return texto;
    }
}
