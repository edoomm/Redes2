
import java.io.Serializable;

/*
    Guarda información sobre un archivo
*/
public class MetainformacionArchivo implements Serializable {
    private String nombreArchivo;
    private long longitudArchivo;
    private boolean esDirectorio;

    public MetainformacionArchivo() {
    }

    public MetainformacionArchivo(String nombreArchivo, long longitudArchivo, boolean esDirectorio) {
        this.nombreArchivo = nombreArchivo;
        this.longitudArchivo = longitudArchivo;
        this.esDirectorio = esDirectorio;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public long getLongitudArchivo() {
        return longitudArchivo;
    }

    public void setLongitudArchivo(long longitudArchivo) {
        this.longitudArchivo = longitudArchivo;
    }

    public boolean esDirectorio() {
        return esDirectorio;
    }

    public void setEsDirectorio(boolean esDirectorio) {
        this.esDirectorio = esDirectorio;
    }
    
}
