
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
            flujoSalida.flush();
            // Proceso de transferencia del archivo
            DataOutputStream flujoDatosSalida = new DataOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[1500];
            long bytesRestantes = longitudArchivo;
            int bytesLeidos = 0;
            // Mientras falten bytes por enviar y se puedan leer bytes del 
            // archivo, se envía un paquete de información
            System.out.println("Enviando archivo: " + archivo.getAbsolutePath());
            while (bytesRestantes > 0 && (bytesLeidos = flujoEntradaArchivo.read(buffer)) != -1) {
                flujoDatosSalida.write(buffer, 0, bytesLeidos);
                flujoDatosSalida.flush();
                bytesRestantes -= bytesLeidos;
                System.out.println("Restante: " + bytesRestantes);
            }
            flujoEntradaArchivo.close();
            System.out.println("Archivo : " + archivo.getAbsolutePath() + " enviado");
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
    
    public void recibirArchivo (String ruta) {
        try {
            MetainformacionArchivo infoArchivo = (MetainformacionArchivo)flujoEntrada.readObject();
            String nombreArchivo = ruta + "\\" + infoArchivo.getNombreArchivo();
            FileOutputStream flujoSalidaArchivo = new FileOutputStream(nombreArchivo);
            
            long longitudArchivo = infoArchivo.getLongitudArchivo();
            long bytesRestantes = longitudArchivo;
            int bytesLeidos = 0;
            
            DataInputStream flujoDatosEntrada = new java.io.DataInputStream(socket.getInputStream());
            
            byte[] buffer = new byte[1500];
            System.out.println("Recibiendo archivo: " + nombreArchivo);
            while (bytesRestantes > 0 && (bytesLeidos = flujoDatosEntrada.read(buffer))!= -1) {
                System.out.println("Escribiendo datos al archivo");
                flujoSalidaArchivo.write(buffer, 0, bytesLeidos);
                flujoSalidaArchivo.flush();
                bytesRestantes -= bytesLeidos;
                System.out.println("Restante: " + bytesRestantes + " bytes");
            }
            flujoSalidaArchivo.close();
            System.out.println("Archivo : " + nombreArchivo + " recibido");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
}
