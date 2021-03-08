
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    private ServerSocket socketServidor;
    private Socket socketCliente;
    private ObjectOutputStream flujoSalida;
    private ObjectInputStream flujoEntrada;
    private ExploradorDeArchivos explorador;
    
    
    public Servidor () {
        socketServidor = null;
        socketCliente = null;
        flujoSalida = null;
        flujoEntrada = null;
        explorador = new ExploradorDeArchivos(System.getProperty("user.home") + "\\desktop");
    }
    
    public Servidor (String rutaInicial) {
        socketServidor = null;
        socketCliente = null;
        flujoSalida = null;
        flujoEntrada = null;
        explorador = new ExploradorDeArchivos(rutaInicial);
    }
    
    // Se inicializan los sockets y los flujos
    public void iniciarServidor () {
        try {
            System.out.println("Waiting for connections...");
            socketServidor = new ServerSocket(1309);
            socketCliente = socketServidor.accept();
            flujoSalida = new ObjectOutputStream(socketCliente.getOutputStream());
            flujoEntrada = new ObjectInputStream(socketCliente.getInputStream());
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    // Se inicia la recepcion de instrucciones del cliente
    public void iniciarServicio () {
        try {
            while (true) {
                leerInstruccion();
            }
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        } catch ( java.lang.ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
        }
    }
    
    // Metodo que recibe las instrucciones, las clasifica y llama
    // al metodo correspondiente para ejecutarlas
    public void leerInstruccion () throws IOException, ClassNotFoundException {
        Instruccion instruccion = (Instruccion)flujoEntrada.readObject();
        switch ( instruccion.getComando() ) {
            case "lss": // Listar el directorio actual
                listarDirectorio();
            break;
            case "cds": // Cambiar directorio actual
                cambiarDirectorio(instruccion.getArgumento(0));
            break;
            case "rm": // elimina un archivo en el directorio actual
                
            break;
            case "mkdirs": // Cre un nuevo directorio en el directorio actual
                crearDirectorio(instruccion.getArgumento(0));
            break;
            case "send": // Recibe un nuevo archivo y lo guarda en el directorio actual
                recibirArchivo();
            break;
            case "get": // Envia un archivo del directorio actual
                
            break;
        }
    }
    
    // Envia una lista del directorio actual al cliente
    private void listarDirectorio () {
        Directorio lista = explorador.listarDirectorio();
        try {
            flujoSalida.writeObject(lista);
            flujoSalida.flush();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    // Cambia el directorio actual del servidor
    private void cambiarDirectorio (String ruta) {
        try {
            explorador.cambiarDirectorio(ruta);
            flujoSalida.writeObject(explorador.obtenerRuta());
            flujoSalida.flush();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    // Crea un nuevo directorio con nombre "nombre"
    private void crearDirectorio (String nombre) {
        explorador.crearDirectorio(nombre);
    }
    
    // Se recibe un archivo desde el cliente
    private void recibirArchivo () {
        try {
            MetainformacionArchivo infoArchivo = (MetainformacionArchivo)flujoEntrada.readObject();
            FileOutputStream flujoSalidaArchivo = new FileOutputStream(infoArchivo.getNombreArchivo());
            
            long longitudArchivo = infoArchivo.getLongitudArchivo();
            long bytesLeidos = 0, bytesRestantes = longitudArchivo;
            
            byte[] buffer = new byte[1500];
            while (bytesRestantes > 0 ) {
                InformacionArchivo datosArchivo = (InformacionArchivo)flujoEntrada.readObject();
                buffer = datosArchivo.getDatos();
                flujoSalidaArchivo.write(buffer,0,buffer.length);
                bytesRestantes -= buffer.length;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

}
