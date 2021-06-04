import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import static java.lang.ProcessBuilder.Redirect.Type.APPEND;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.Iterator;
/**
 *
 * @author axel
 */
public class ServidorNIO {
    private int PORT = 9999;
    private ServerSocketChannel server;
    private Selector selector;
    
    private int                 tematica;
    private final String[]      tematicas = {"Redes", "Geografia", "Mascotas"};
    private final String[][]    palabrasTematicas = {
                                                      {"Protocolos", "Internet", "Topologia", "Ethernet", "Checksum", "Paquete", "Encabezado"}, // Redes
                                                      {"Relieve", /*"Altitud", "Clima", "Acantilado", "Meseta", "Hidrografía", "Archipielago"*/}, // Geografía
                                                      {"Hamster", "Aves", "Peces", "Gato", "Serpiente", "Perro", "Tortuga", "Conejo"}  // Mascotas 
                                                      };
    
    public ServidorNIO () throws IOException {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.socket().bind(new InetSocketAddress(PORT));
        selector = Selector.open();
        server.register(selector,SelectionKey.OP_ACCEPT);
    }
    
    public void iniciar() {
        while(true){
            try {
                selector.select();
                Iterator<SelectionKey>it= selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey k = (SelectionKey)it.next();
                    it.remove();
                    if(k.isAcceptable()){
                        SocketChannel cl = server.accept();
                        System.out.println("Cliente conectado desde->"+
                            cl.socket().getInetAddress().getHostAddress()+":"+cl.socket().getPort());
                        cl.configureBlocking(false);
                        cl.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                        continue;
                    }
                    if(k.isReadable()){
                        SocketChannel ch =(SocketChannel)k.channel();
                        ByteBuffer b = ByteBuffer.allocate(2000);
                        b.clear();
                        int n = ch.read(b);
                        b.flip();

                        String msj = new String(b.array(),0,n);
                        if(msj.equalsIgnoreCase("SALIR")){
                            System.out.println("Cerrando conexión");
                            ch.close();
                        } else if (msj.contains("Tiempo")) {
                            System.out.println("Registrando tiempo");
                            String filename= "tiempos.txt";
                            FileWriter fw = new FileWriter(filename,true);
                            fw.write("Tiempo de " + ch.socket().getInetAddress().getHostAddress()+":"+ch.socket().getPort()+" : " + msj + "\n");
                            fw.close();
                            ch.close();
                        } else {
                            tematica = Integer.parseInt(msj);

                            GeneradorDeTablero generador = new GeneradorDeTablero(tematicas[tematica], palabrasTematicas[tematica]);
                            Tablero tablero = generador.generarTablero();
                            System.out.println("Enviado tablero: ");
                            System.out.println(tablero);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(tablero);
                            oos.flush();
                            byte[] tableroBytes = baos.toByteArray();
                            b = ByteBuffer.wrap(tableroBytes);
                            ch.write(b);
                            continue;
                        }//else
                    }
                }
            } catch (IOException ioe) {
                System.out.println("IOException thrown");
                continue;
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        ServidorNIO s = new ServidorNIO();
        s.iniciar();
    }
}