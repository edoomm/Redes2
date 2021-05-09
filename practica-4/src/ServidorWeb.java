
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServidorWeb {

    public static final int PUERTO = 8000;
    public static final int POOL_SIZE = 2;
    
    ServerSocket ss;
    private ExecutorService pool;

    class Manejador extends Thread {

        protected Socket socket;
        protected PrintWriter pw;
        protected BufferedOutputStream bos;
        protected BufferedReader br;
        protected String FileName;

        public Manejador(Socket _socket) throws Exception {
            this.socket = _socket;
        }

        public void run() {
            try {
                // Remove for production
                // Used a delay to show the thread pool behavior
                sleep(2000);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bos = new BufferedOutputStream(socket.getOutputStream());
                pw = new PrintWriter(new OutputStreamWriter(bos));
                String line = br.readLine();
                if (line == null) {
                    pw.print("<html><head><title>Servidor WEB");
                    pw.print("</title><body bgcolor=\"#AACCFF\"<br>Linea Vacia</br>");
                    pw.print("</body></html>");
                    socket.close();
                    return;
                }

                System.out.println("\nCliente Conectado desde: " + socket.getInetAddress());
                System.out.println("Por el puerto: " + socket.getPort());
                System.out.println("Datos: " + line + "\r\n\r\n");

                if (line.indexOf("?") == -1) {
                    getArch(line);
                    // POST REQUEST
                    if (line.toUpperCase().startsWith("POST")) {
                        requestPost(br);
                    } else if ( line.toUpperCase().startsWith("PUT") ) {
                        // Extract the file name from the request and save it 
                        // into the FileName class attribute
                        getArch(line);
                        // We get the request raw data information
                        StringBuilder payload = new StringBuilder();
                        while (br.ready()) {
                            payload.append((char) br.read());
                        }
                        String plstr = payload.toString();
                        // Request content is after two consecutive '\r\n'
                        // We extract the content to write it into the file
                        String fileContent = plstr.split("\r\n\r\n")[1];
                        // Create file and write content
                        System.out.println(FileName);
                        File newFile = new File(FileName);
                        newFile.createNewFile();
                        FileWriter myWriter = new FileWriter(newFile);
                        myWriter.write(fileContent);
                        myWriter.close();
                        sendPutResponse(FileName);
                    } else if (line.toUpperCase().startsWith("HEAD")) {
                        System.out.println("Solicitud HEAD");
                        BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(FileName));
                        byte[] buf = new byte[1024];
                        int tam_bloque = 0;
                        if (bis2.available() >= 1024) {
                            tam_bloque = 1024;
                        } else {
                            bis2.available();
                        }

                        int tam_archivo = bis2.available();
                        sendResponse(getFileExtension(FileName), tam_archivo);
                    } else if (FileName.compareTo("") == 0) {
                        SendA("index.htm");
                    } else {
                        SendA(FileName);
                    }
                    // if (!FileName.equals(null))
                    // 	System.out.println(FileName);
                } else if (line.toUpperCase().startsWith("GET")) {
                    requestGet(line);
                } else if (line.toUpperCase().startsWith("POST")) {
                    // Creo que este no va
                } else {
                    pw.println("HTTP/1.0 501 Not Implemented");
                    pw.println();
                }
                pw.flush();
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void getArch(String line) {
            int i;
            int f;
            if (line.toUpperCase().startsWith("GET") ||
                    line.toUpperCase().startsWith("HEAD") ||
                    line.toUpperCase().startsWith("PUT")
                ) {
                i = line.indexOf("/");
                f = line.indexOf(" ", i);
                FileName = line.substring(i + 1, f);
            }
        }

        public void SendA(String fileName, Socket sc) {
            //System.out.println(fileName);
            int fSize = 0;
            byte[] buffer = new byte[4096];
            try {
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //sendHeader();
                FileInputStream f = new FileInputStream(fileName);
                int x = 0;
                while ((x = f.read(buffer)) > 0) {
                    //		System.out.println(x);
                    out.write(buffer, 0, x);
                }
                out.flush();
                f.close();
            } catch (FileNotFoundException e) {
                //msg.printErr("Transaction::sendResponse():1", "El archivo no existe: " + fileName);
            } catch (IOException e) {
                //			System.out.println(e.getMessage());
                //msg.printErr("Transaction::sendResponse():2", "Error en la lectura del archivo: " + fileName);
            }

        }

        public void SendA(String arg) {
            try {
                int b_leidos = 0;
                BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(arg));
                byte[] buf = new byte[1024];
                int tam_bloque = 0;
                if (bis2.available() >= 1024) {
                    tam_bloque = 1024;
                } else {
                    bis2.available();
                }

                int tam_archivo = bis2.available();
                String fileExtension = getFileExtension(arg);
                /**
                 * ********************************************
                 */
                sendResponse(fileExtension, tam_archivo);

                // System.out.println(sb);
                //out.println("HTTP/1.0 200 ok");
                //out.println("Server: Axel Server/1.0");
                //out.println("Date: " + new Date());
                //out.println("Content-Type: text/html");
                //out.println("Content-Length: " + mifichero.length());
                //out.println("\n");
                /**
                 * ********************************************
                 */
                while ((b_leidos = bis2.read(buf, 0, buf.length)) != -1) {
                    bos.write(buf, 0, b_leidos);
                }
                bos.flush();
                bis2.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        private void requestGet(String line) {
            StringTokenizer tokens = new StringTokenizer(line, "?");
            String req_a = tokens.nextToken();
            String req = tokens.nextToken();
            System.out.println("Token1: " + req_a + "\r\n\r\n");
            System.out.println("Token2: " + req + "\r\n\r\n");
            pw.println("HTTP/1.0 200 Okay");
            pw.flush();
            pw.println();
            pw.flush();
            pw.print("<html><head><title>SERVIDOR WEB");
            pw.flush();
            pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
            pw.flush();
            pw.print("<h3><b>" + req + "</b></h3>");
            pw.flush();
            pw.print("</center></body></html>");
            pw.flush();
        }

        private void requestPost(BufferedReader br) {
            try {
                System.out.println("Solicitud POST");
                //code to read the post payload data
                StringBuilder payload = new StringBuilder();
                while (br.ready()) {
                    payload.append((char) br.read());
                }
                // obtaining parameters
                String plstr = payload.toString();
                String lastParameter = "Accept-Language: en-US,en;q=0.9";
                plstr = plstr.substring(plstr.indexOf(lastParameter) + lastParameter.length() + 4, plstr.length() - 1);
                sendResponse(plstr);

                pw.println("HTTP/1.0 200 Okay");
                pw.flush();
                pw.println();
                pw.flush();
                pw.print("<html><head><title>POST");
                pw.flush();
                pw.print("</title></head><body><center><h1><br>POST realizado</br></h1>");
                pw.flush();
                pw.print("</center></body></html>");
                pw.flush();
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
            }
        }

        /**
         * *
         * Obtains the extension file of a given file name
         *
         * @param fileName The name of the file to retrieve its extension
         * @return The substring that corresponds to the file extension
         */
        private String getFileExtension(String fileName) {
            return fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        }

        /**
         * Sends the HTTP response. This response is used for a GET request
         *
         * @param fileExtension The file extension in order to know the MIME
         * Type file
         * @param tam_archivo The lenght of the file given in bytes
         */
        private void sendResponse(String fileExtension, int tam_archivo) {
            try {
                String sb = "";
                sb = sb + "HTTP/1.0 200 ok\n";
                sb = sb + "Server: Axel Server/1.0 \n";
                sb = sb + "Date: " + new Date() + " \n";
                sb = sb + "Content-Type: " + MimeTypes.getMimeType(fileExtension) + " \n";
                sb = sb + "Content-Length: " + tam_archivo + " \n";
                sb = sb + "\n";
                bos.write(sb.getBytes());
                bos.flush();

                System.out.println(sb);
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
            }
        }

        /**
         * Sends a HTTP response. This is used for POST requests.
         *
         * @param payload The payload string where the parameters are
         */
        private void sendResponse(String payload) {
            try {
                sendResponse(payload, "200");
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
            }
        }

        private void sendResponse(String payload, String codeResponse) throws Exception {
            String sb = "";
            sb = sb + "HTTP/1.0 " + codeResponse + " ok\n";
            sb = sb + "Server: Axel Server/1.0 \n";
            sb = sb + "Date: " + new Date() + " \n";
            sb = sb + payload + "\n";
            sb = sb + "\n";
            System.out.println(sb);
        }
        
        private void sendPutResponse ( String fileName ) {
            try {
                String sb = "";
                sb = sb + "HTTP/1.1 201 Created\n";
                sb = sb + "Content-Location: " + fileName + "\n";
                bos.write(sb.getBytes());
                bos.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ServidorWeb() throws Exception {
        System.out.println("Iniciando Servidor.......");
        this.ss = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado:---OK");
        System.out.println("Esperando por Cliente....");
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        for (;;) {
            Socket accept = ss.accept();
            pool.execute(new Manejador(accept));
        }
    }

    public static void main(String[] args) throws Exception {
        ServidorWeb sWEB = new ServidorWeb();
    }

}
