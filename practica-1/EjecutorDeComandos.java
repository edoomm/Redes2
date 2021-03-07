
import java.util.ArrayList;

public class EjecutorDeComandos {
    private ExploradorDeArchivos explorador;
    private Cliente cliente;
    private String ruta;

    public EjecutorDeComandos(String rutaDeInicio) {
        ruta = rutaDeInicio;
        explorador = new ExploradorDeArchivos();
    }
    
    public String ejecutarComando (String comando) {
        String[] partesComando = comando.split(" ");
        if ( partesComando.length == 0 ) return "";
        String instruccion = partesComando[0];
        ArrayList<String> argumentos = new ArrayList<>();
        for ( int i = 1 ; i < partesComando.length ; i++ ) {
            argumentos.add(partesComando[i]);
        }
        
        switch ( comando ) {
            // Comandos locales
            case "lsl": // listar directorio actual local
                ArrayList<String> listaArchivos = explorador.listarDirectorio(ruta);
                return obtenerCadena(listaArchivos);
            case "cdl": // Cambiar ruta actual local
                return "";
            // Comandos remotos
            case "lss": // Listar directorio actual del servidor
                // return cliente.listarDirectorioServidor();
                return "";
            case "cdls": // Cambiar ruta actual del servidor
                // return cliente.listarDirectorioServidor();
                return "";
            case "send":
                // return cliente.enviarArchivos(argumentos);
                return "";
            case "get":
                // return cliente.obtenerArchivos(argumentos);
                return "";
            case "rm":
                // return cliente.eliminarArchivos(argumentos);
                return "";
            case "mkdir":
                // return cliente.crearDirectorio(argumentos);
                return "";
            default:
                return "Command not found";
        }
    }
    
    public String obtenerCadena ( ArrayList<String> listaArchivos ) {
        String textoLista = "";
        for ( int i = 0 ; i < listaArchivos.size() ; i++ ) {
            textoLista += "   " + listaArchivos.get(i) + "\n";
        }
        return textoLista;
    }
}
