
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ClienteNIO {
    
    private Scanner scanner;
    private boolean jugando;
    private Cronometro cronometro;
    private String host = "127.0.0.1";
    private int PORT = 9999;
    private boolean firstTime;
    private BufferedReader bufferedReader;
    private SocketChannel client;
    private Selector selector;
    private String tiempoFinal;
    
    public ClienteNIO() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        client = SocketChannel.open();
        selector = Selector.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(host,PORT));
        client.register(selector, SelectionKey.OP_CONNECT);
        
        scanner = new Scanner(System.in);
        jugando = true;
        cronometro = new Cronometro();
        firstTime = true;
        
        tiempoFinal = "";
    }
    
    public String elegirTematica() {
        String opcion = "";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escoja su temática:\n----------\n0) Redes\n1) Geografia\n2) Mascotas\n-1) Salir");
        System.out.println("Opcion: ");
            opcion = scanner.nextLine();
        return opcion;
    }
     
    public void conectarse(SelectionKey k) throws ClosedChannelException {
        SocketChannel ch = (SocketChannel)k.channel();
        if(ch.isConnectionPending()){
            try{
                ch.finishConnect();
                System.out.println("Conexion establecida.. Escribe un mensaje <ENTER> para enviar \"SALIR\" para teminar");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        ch.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
    }
    
    public void iniciarCliente() throws IOException, ClassNotFoundException {
        while(true){
                selector.select();
                Iterator<SelectionKey>it =selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey k = (SelectionKey)it.next();
                    it.remove();
                    if(k.isConnectable()){
                        conectarse(k);
                    } else if(k.isWritable()) {
                        System.out.println("WRITEABLE");
                        SocketChannel ch2 = (SocketChannel)k.channel();
                        if (firstTime) {
                            String tematica = elegirTematica();
                            if (tematica.equalsIgnoreCase("SALIR")) {
                                System.out.println("Saliendo");
                                ByteBuffer b = ByteBuffer.wrap(tematica.getBytes());
                                ch2.write(b);
                                ch2.close();
                                return;
                            }
                            ByteBuffer b = ByteBuffer.wrap(tematica.getBytes());
                            ch2.write(b);
                            firstTime = false;
                        } else if (tiempoFinal.equalsIgnoreCase("SALIR")){
                            System.out.println("Saliendo del juego");
                            ByteBuffer b = ByteBuffer.wrap(("SALIR").getBytes());
                            ch2.write(b);
                            return;
                        } else {
                            System.out.println("Enviando tiempo del jugador");
                            ByteBuffer b = ByteBuffer.wrap(("Tiempo: " + tiempoFinal).getBytes());
                            ch2.write(b);
                            return;
                        }
                        k.interestOps(SelectionKey.OP_READ);
                    } else if(k.isReadable()){
                        System.out.println("READABLE");
                        SocketChannel ch2 = (SocketChannel)k.channel();
                        ByteBuffer b = ByteBuffer.allocate(50000);
                        b.clear();
                        int n = ch2.read(b);
                        b.flip();
                        
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b.array()));
                        Tablero tablero = (Tablero)ois.readObject();
                        
                        System.out.println("Se recibió el tablero");
                        
                        iniciarJuego(tablero);
                        
                        k.interestOps(SelectionKey.OP_WRITE);
                    }
                }
           }
    }
    
    private Posicion leerPosicion () {
        try {
            int fila = scanner.nextInt(), columna = scanner.nextInt();
            return new Posicion ( fila, columna);
        } catch (Exception e) {
            System.out.println("Entrada inválida");
            return null;
        }
    }
    
    public void iniciarJuego(Tablero tablero) {
        try {
            jugando = true;
            while ( jugando ) {
                cronometro.iniciar();
                System.out.println(tablero);
                System.out.println("Ingresa fila y columna de inicio");
                Posicion inicio = leerPosicion();
                if (inicio == null) break;
                System.out.println("Ingresa fila y columna de fin");
                Posicion fin = leerPosicion();
                if (inicio == null) break;

                if ( tablero.descubrirPalabra(inicio, fin) ) {
                    if ( tablero.palabrasFaltantes() > 0 )
                        System.out.println("Yuju! \nDescubriste una palabra!\nRestan: " + tablero.palabrasFaltantes() + " palabras");
                    else {
                        System.out.println("Felicidades! \nGanaste!");
                        cronometro.detener();
                        tiempoFinal = cronometro.getHorasTranscurridas() + " hrs. " + 
                                cronometro.getMinutosTranscurridos() + " mins. " + cronometro.getSegundosTranscurridos() + " segs.";
                        System.out.println("Tu tiempo: " + tiempoFinal);
                        cronometro.reiniciar();
                        jugando = false;
                        break;
                    }
                } else {
                    System.out.println("No es una palabra correcta :(\nIntenta de nuevo");
                }
            }
            if (tiempoFinal.length() == 0) {
                tiempoFinal = "Salir";
            }
        } catch (Exception e) {
            System.err.println("Error al iniciar el juego");
            e.printStackTrace();
        }
    
    }
    
    public static void main(String[] args){
        try {
            ClienteNIO cliente = new ClienteNIO();
            cliente.iniciarCliente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
