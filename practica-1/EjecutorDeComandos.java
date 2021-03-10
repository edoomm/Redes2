
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
                return directorio.toString() + "\n";
            case "cdl": // Cambiar ruta actual local
                if ( instruccion.getArgumentos().size() > 0 ) {
                    explorador.cambiarDirectorio(instruccion.getArgumento(0));
                    return "Current Directory: " + explorador.obtenerRuta() + "\n";
                } else {
                    return "No such file or directory" + "\n";
                }
            case "mkdirl":
                explorador.crearDirectorio(instruccion.getArgumento(0));
                return "Directory "+ instruccion.getArgumento(0) +" created" + "\n";
            case "pwdl":
                return "Current Local Directory: " + explorador.obtenerRuta() + "\n";
            // Comandos remotos
            case "lss": // Listar directorio actual del servidor
                cliente.enviarInstruccion(instruccion);
                Directorio directorioServidor = (Directorio)cliente.recibirObjeto();
                return directorioServidor.toString() + "\n";
            case "cds": // Cambiar ruta actual del servidor
                cliente.enviarInstruccion(instruccion);
                return "Server Directory Changed\n";
            case "send": // Enviar archivos o directorios
                enviarArchivos(instruccion);
                return "Files Succesfullly Sent" + "\n";
            case "get": // Pide al servidor los archivos especificados
                cliente.enviarInstruccion(instruccion);
                recibirArchivos();
                return "Files Succesfully Received" + "\n";
            case "rms":
                cliente.enviarInstruccion(instruccion);
                return "File Succesfully Deleted" + "\n";
            case "mkdirs":
                cliente.enviarInstruccion(instruccion);
                return "Directory Succesfully Created" + "\n";
            case "exit":
                cliente.enviarInstruccion(instruccion);
                return "Bye!" + "\n";
            case "pwds":
                cliente.enviarInstruccion(instruccion);
                String directorioActual = (String)cliente.recibirObjeto();
                return directorioActual + "\n";
            case "":
                return "";
            default:
                return "Command not found" + "\n";
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
    
    private void recibirArchivos() {
        while ( true ) {
            String comando = (String)cliente.recibirObjeto();
            System.out.println("Comando recibido: " + comando);
            
            switch (comando) {
                case "finish":
                    return;
                case "rcv":
                    cliente.recibirArchivo(explorador.obtenerRuta());
                break;
                default:
                    System.out.println(ejecutarComando(comando));
            }
        }
    }
    
    
}
