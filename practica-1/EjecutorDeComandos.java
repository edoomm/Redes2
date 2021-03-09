
import java.io.File;
import java.util.ArrayList;
/*
    Clase comunicadora entre la Consola, el ExploradorDeArchivos y 
    el Cliente
*/
public class EjecutorDeComandos {
    private ExploradorDeArchivos explorador;
    private Cliente cliente;
    private String ruta;

    public EjecutorDeComandos(String rutaDeInicio) {
        ruta = rutaDeInicio;
        explorador = new ExploradorDeArchivos();
        cliente = new Cliente();
        cliente.establecerConexion();
    }
    
    public String obtenerUbicacion () {
        return explorador.obtenerRuta();
    }
    
    public String ejecutarComando (String comando) {
        Instruccion instruccion = new Instruccion(comando);
        
        switch ( instruccion.getComando() ) {
            // Comandos locales
            case "lsl": // listar directorio actual local
                Directorio directorio = explorador.listarDirectorio();
                return directorio.toString();
            case "cdl": // Cambiar ruta actual local
                if ( instruccion.getArgumentos().size() > 0 ) {
                    explorador.cambiarDirectorio(instruccion.getArgumento(0));
                    return "";
                } else {
                    return "No such file or directory";
                }
            // Comandos remotos
            case "lss": // Listar directorio actual del servidor
                cliente.enviarInstruccion(instruccion);
                Directorio directorioServidor = (Directorio)cliente.recibirObjeto();
                return directorioServidor.toString();
            case "cds": // Cambiar ruta actual del servidor
                cliente.enviarInstruccion(instruccion);
                String respuesta = (String)cliente.recibirObjeto();
                return "Current directory: \n" + respuesta;
            case "send": // Enviar archivos o directorios
                enviarArchivos(instruccion);
                return "Operation Succesful";
            case "get":
                // return cliente.obtenerArchivos(argumentos);
                return "";
            case "rms":
                cliente.enviarInstruccion(instruccion);
                return "Operation Succesful";
            case "mkdirs":
                cliente.enviarInstruccion(instruccion);
                return "Operation Succesful";
            case "":
                return "";
            default:
                return "Command not found";
        }
    }
    
    public void enviarArchivos(Instruccion instruccion) {
        ArrayList<String> archivos = instruccion.getArgumentos();
        for ( int i = 0 ; i < archivos.size() ; i++ ) {
            String ruta = explorador.obtenerRuta() + "\\" + archivos.get(i);
            enviarArchivo(ruta);
        }
    }
    
    public void enviarArchivo (String ruta) {
        File archivo = new File(ruta);
        if ( archivo.exists() ) {
            if ( archivo.isDirectory() ) {
                ejecutarComando("mkdirs " + archivo.getName());
                ejecutarComando("cds " + archivo.getName());
                File[] subarchivos = archivo.listFiles();
                for ( int i = 0 ; i < subarchivos.length ; i++ ) {
                    enviarArchivo(subarchivos[i].getAbsolutePath());
                }
                ejecutarComando("cds ..");
            } else {
                Instruccion instruccion = new Instruccion("send " + archivo.getName());
                cliente.enviarInstruccion(instruccion);
                cliente.enviarArchivo(archivo);
            }
        } else {
            System.out.println("No existe el archivo");
        }
    }
    
}
