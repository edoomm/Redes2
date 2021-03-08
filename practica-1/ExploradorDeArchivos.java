
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/*
    Clase que se encarga del manejo del sistema de archivos
*/
public class ExploradorDeArchivos {
    private String ruta;
    
    public ExploradorDeArchivos () {
        // Se configura el escritorio como acrpeta por defecto
        ruta = System.getProperty("user.home") + "\\desktop";
    }
    
    public ExploradorDeArchivos (String ruta) {
        this.ruta = ruta;
    }
    
    // Devuelve una lista de los directorios/archivos de la ruta actual
    public Directorio listarDirectorio () {
        File archivo = new File(ruta);
        File[] archivos = archivo.listFiles();
        Directorio directorioActual = new Directorio ();
        
        if ( archivos == null ) {
            System.out.println("El archivo es null");
            return directorioActual;
        }
        for ( int i = 0 ; i < archivos.length ; i++ ) {
            String nombre = archivos[i].getName();
            long longitud = archivos[i].length();
            boolean esDirectorio = archivos[i].isDirectory();
            directorioActual.agregarArchivo(new MetainformacionArchivo(nombre, longitud, esDirectorio));
        }
        return directorioActual;
    }
    
    public String obtenerRuta () {
        return ruta;
    }
    
    // Navega un directorio hacia atrás en la ruta actual
    private void navegarAtras () {
        String[] partesRuta = ruta.split("\\\\");
        if ( partesRuta.length > 0 )
            ruta = ruta.replace(partesRuta[partesRuta.length - 1], "");
        int i = ruta.length() - 1;
        int cont = 0;
        while ( i > 0 && ruta.charAt(i--) == '\\' ) cont++;
        ruta = ruta.substring(0, ruta.length() - cont);
    }
    
    // Cambia el directorio actual
    // Se puede navegar a las carpetas del directorio actual (ej. "desktop"),
    // hacia atras (ej. "..") o hacia una ruta absoluta  (ej. "C:\Users") 
    public boolean cambiarDirectorio (String nuevaRuta) {
        if ( nuevaRuta.equals("..") ) {
            navegarAtras();
            return true;
        }
        if ( nuevaRuta.split("\\\\").length <= 1 ) {
            nuevaRuta = ruta + "\\" + nuevaRuta;
        }
        File file = new File(nuevaRuta);
        if ( file.exists() ) {
            ruta = nuevaRuta;
            return true;
        } else {
            return false;
        }
    }
    
    // Crea un directorio con el nombre "nombre"
    // en la ruta actual
    public boolean crearDirectorio (String nombre) {
        File file = new File(ruta + "\\" + nombre);
        file.mkdirs();
        return true;
    }
    
    // Remueve un archivo con nombre "nombre" en la 
    // carpeta actual
    public boolean removerArchivo(String nombre) {
        return false;
    }
     
    
    public static void main(String[] args) {
        ExploradorDeArchivos e = new ExploradorDeArchivos();
        
        System.out.println(e.obtenerRuta());
        System.out.println(e.listarDirectorio());
        
        e.crearDirectorio("Prueba");
        
        e.cambiarDirectorio("prueba");
        
        System.out.println(e.obtenerRuta());
        System.out.println(e.listarDirectorio());
        
        
    }
    
}
