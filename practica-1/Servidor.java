
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
                System.out.println("Listando directorio");
                listarDirectorio();
            break;
            case "cds": // Cambiar directorio actual
                System.out.println("Cambiando directorio a: " + explorador.obtenerRuta() + "\\" + instruccion.getArgumento(0));
                cambiarDirectorio(instruccion.getArgumento(0));
            break;
            case "rms": // elimina un archivo en el directorio actual
                removerArchivo(instruccion.getArgumento(0));
            break;
            case "mkdirs": // Crea un nuevo directorio en el directorio actual
                System.out.println("Creando directorio: " + instruccion.getArgumento(0));
                crearDirectorio(instruccion.getArgumento(0));
            break;
            case "send": // Recibe un nuevo archivo y lo guarda en el directorio actual
                recibirArchivo();
            break;
            case "get": // Envia un archivo del directorio actual
                enviarArchivos(instruccion.getArgumentos());
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
    
    // Elimina el archivo con el nombre "nombre"
    // del directorio actual
    private void removerArchivo(String nombre) {
        explorador.removerArchivo(nombre);
    }
    
    private void eliminarDirectorio (String nombre) {
        explorador.removerArchivo(nombre);
    }
    
    // Se recibe un archivo desde el cliente
    private void recibirArchivo () {
        try {
            MetainformacionArchivo infoArchivo = (MetainformacionArchivo)flujoEntrada.readObject();
            String nombreArchivo = explorador.obtenerRuta() + "\\" + infoArchivo.getNombreArchivo();
            FileOutputStream flujoSalidaArchivo = new FileOutputStream(nombreArchivo);
            
            long longitudArchivo = infoArchivo.getLongitudArchivo();
            long bytesRestantes = longitudArchivo;
            int bytesLeidos = 0;
            
            DataInputStream flujoDatosEntrada = new java.io.DataInputStream(socketCliente.getInputStream());
            
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
    
    private void enviarArchivos ( ArrayList<String> nombresArchivos ) {
        try {
            for ( int i = 0 ; i < nombresArchivos.size() ; i++ ) {
                File archivo = new File(explorador.obtenerRuta() + "\\" + nombresArchivos.get(i));
                if ( archivo.exists() ) {
                    enviarArchivos(archivo);
                }
            }
            System.out.println("Enviando instruccion: " + "finish ");
            flujoSalida.writeObject("finish");
            flujoSalida.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void enviarArchivos (File archivo) throws IOException {
        if ( archivo.isDirectory() ) {
            System.out.println("Enviando instruccion: " + "mkdirl " + archivo.getName());
            flujoSalida.writeObject("mkdirl " + archivo.getName());
            flujoSalida.flush();
            System.out.println("Enviando instruccion: " + "cdl " + archivo.getName());
            flujoSalida.writeObject("cdl " + archivo.getName());
            flujoSalida.flush();
            File[] subarchivos = archivo.listFiles();
                for ( int i = 0 ; i < subarchivos.length ; i++ ) {
                    enviarArchivos(subarchivos[i]);
                }
            System.out.println("Enviando instruccion: " + "cdl ..");
            flujoSalida.writeObject("cdl ..");
            flujoSalida.flush();
        } else {
            System.out.println("Enviando instruccion: " + "rcv ..");
            flujoSalida.writeObject("rcv");
            flujoSalida.flush();
            enviarArchivo(archivo);
        }
    }
    
    public void enviarArchivo ( File archivo ) {
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
            DataOutputStream flujoDatosSalida = new DataOutputStream(socketCliente.getOutputStream());
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
    
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciarServidor();
        servidor.iniciarServicio();
    }

}
