
import java.io.Serializable;
/*
    Clase contenedora de la información en crudo de un archivo
    Se usa para hacer la transferencia de archivos
*/
public class InformacionArchivo implements Serializable {
    private byte[] datos;

    public InformacionArchivo() {
    }

    public InformacionArchivo(byte[] datos) {
        this.datos = datos;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }
    
}
