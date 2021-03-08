
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Cliente {
    private Socket socket;
    private ObjectOutputStream flujoSalida;
    private ObjectInputStream flujoEntrada;
    
    public Cliente () {
        socket = null;
        flujoSalida = null;
        flujoEntrada = null;
    }
    
    // Inicializamos el socket y los flujos
    public boolean establecerConexion () {
        try {
            socket = new Socket ("127.0.0.1", 1309);
            flujoSalida = new java.io.ObjectOutputStream(socket.getOutputStream());
            flujoEntrada = new java.io.ObjectInputStream(socket.getInputStream());
            return true;
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            return false;
        }
    }
    
    // Método para enviar una instrucción al servidor
    public void enviarInstruccion ( Instruccion instruccion ) {
        try {
            flujoSalida.writeObject(instruccion);
            flujoSalida.flush();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }    
    
    // Envia un archivo hacia el servidor
    public void enviarArchivo( File archivo ) {
        try {
            // Flujo para leer la información del archivo
            FileInputStream flujoEntradaArchivo = new FileInputStream(archivo);
            // Formamos paquete con la metainformación del archivo
            long longitudArchivo = archivo.length();
            String nombreArchivo = archivo.getName();
            boolean esDirectorio = archivo.isDirectory();
            
            MetainformacionArchivo infoArchivo = new MetainformacionArchivo(nombreArchivo, longitudArchivo, esDirectorio);
            // Enviamos la metainformación del archivo
            flujoSalida.writeObject(infoArchivo);
            // Proceso de transferencia del archivo
            byte[] buffer = new byte[1500];
            long bytesLeidos = 0, bytesRestantes = longitudArchivo;
            // Mientras falten bytes por enviar y se puedan leer bytes del 
            // archivo, se envía un paquete de información
            while (bytesRestantes > 0 && (bytesLeidos = flujoEntrada.read(buffer)) != -1) {
                flujoSalida.writeObject(infoArchivo);
                flujoSalida.flush();
                bytesRestantes -= bytesLeidos;
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    // Recibe un objeto de cualquier tipo
    public Object recibirObjeto () {
        try {
            Object objetoRecibido = flujoEntrada.readObject();
            return objetoRecibido;
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return null;
    }
}
