import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 *
 * @author donaldo
 */
public class CecoD {
    private int             puerto;
    private String          ip;
    private InetAddress     destino;
    private DatagramSocket  cliente;
    private byte[]          bufferEco;
    private final int       longitudMaximaPaquete = 10;
    private final int       longitudMetainformacion = 8; // 2 enteros = 2 * (32 / 8) = 8
    
    public CecoD ( int puerto, String ip ) {
        try {
            this.puerto = puerto;
            this.ip = ip;
            this.destino = InetAddress.getByName(ip);
            cliente = new DatagramSocket();
        } catch ( UnknownHostException uhe ) {
            uhe.printStackTrace();
        } catch ( SocketException se ) {
            se.printStackTrace();
        }
    }
    
    public void start () {
        BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
        try {
            while ( true ) {
                System.out.println("Escribe un mensaje, <Enter> para enviar, \"salir\" para terminar");
                String mensaje = lector.readLine();
                if ( mensaje.compareToIgnoreCase("salir") == 0 ) {
                    System.out.println("Programa Terminado");
                    lector.close();
                    cliente.close();
                    System.exit(0);
                } else {
                    bufferEco = new byte[mensaje.length()];
                    enviarMensaje( mensaje );
                    String ecoRecibido = new String(bufferEco);
                    System.out.println("Eco Recibido: " + ecoRecibido);
                }
            }
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    public void enviarMensaje (String mensaje) throws IOException{
        byte[] bytesMensaje = mensaje.getBytes();
        if ( bytesMensaje.length > longitudMaximaPaquete ) {
            bufferEco = new byte[bytesMensaje.length];
            int tp = (int)(bytesMensaje.length / longitudMaximaPaquete);
            for(int j = 0 ; j < tp ; j++) {
                byte[] segmento = Arrays.copyOfRange(
                    bytesMensaje,
                    j * longitudMaximaPaquete, 
                    ((j * longitudMaximaPaquete) + (longitudMaximaPaquete))
                );
                enviarPaquete(j + 1, segmento.length, segmento);
                
                System.out.println("Enviando fragmento " + (j+1) + " de " + (tp + 1) + "\n desde: " +
                    (j*longitudMaximaPaquete) + " hasta " + 
                    ((j*longitudMaximaPaquete) + (longitudMaximaPaquete)));
                String fragmento  = new String (segmento);
                System.out.println("Segmento enviado: " + fragmento);
                byte[] segmentoRecibido = recibirPaquete();
                fragmento = new String (segmentoRecibido);
                System.out.println("Segmento recibido: " + fragmento);
                for(int i = 0 ; i < longitudMaximaPaquete ; i++) {
                    bufferEco[( j * longitudMaximaPaquete) + i] = segmentoRecibido[i];
                }
            }
            if ( bytesMensaje.length % longitudMaximaPaquete > 0 ){ //bytes sobrantes  
                int sobrantes = bytesMensaje.length % longitudMaximaPaquete;
                byte[] segmento = Arrays.copyOfRange(bytesMensaje, tp*longitudMaximaPaquete, ((tp*longitudMaximaPaquete) + sobrantes));

                enviarPaquete(tp + 1, segmento.length, segmento);

                System.out.println("Enviando fragmento " + (tp + 1) + " de " + (tp + 1) + "\n desde: " +
                    (tp*longitudMaximaPaquete) + " hasta " + 
                    ((tp*longitudMaximaPaquete) + (sobrantes)));
                
                String fragmento = new String (segmento);
                System.out.println("Segmento enviado: " + fragmento);
                byte[] segmentoRecibido = recibirPaquete();
                fragmento = new String (segmentoRecibido);
                System.out.println("Segmento recibido: " + fragmento);
                
                for(int i = 0 ; i < sobrantes ; i++) {
                    bufferEco[(tp*longitudMaximaPaquete)+i] = segmentoRecibido[i];
                }
            }//if
        }else{
            enviarPaquete(1, bytesMensaje.length, bytesMensaje);
            byte[] segmentoRecibido = recibirPaquete();
            for(int i = 0 ; i < bytesMensaje.length ; i++) {
                bufferEco[i] = segmentoRecibido[i];
            }
        }
    }
    
    public void enviarPaquete ( int numero, int longitud, byte[] datos ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(numero);
        dos.writeInt(datos.length);
        dos.write(datos);
        dos.flush();
        byte[] aux = baos.toByteArray();
        DatagramPacket p= new DatagramPacket(aux, aux.length, destino, puerto);
        cliente.send(p);
    }
    
    public byte[] recibirPaquete () throws IOException {
        DatagramPacket p = new DatagramPacket(new byte[65535],65535);
        cliente.receive(p);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
        int num = dis.readInt();
        int tam = dis.readInt();
        String eco = new String(p.getData(), 0, p.getLength());
        byte[] datos = new byte[tam];
        dis.read(datos);
        return datos;
    }    
    
    public static void main(String[] args){
        CecoD cliente = new CecoD(1234, "localhost");
        cliente.start();
    }
    
}
