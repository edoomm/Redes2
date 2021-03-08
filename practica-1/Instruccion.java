
import java.io.Serializable;
import java.util.ArrayList;

/*
    Clase contenedora de una instruccion o comando
*/
public class Instruccion implements Serializable {
    private String comando;
    private ArrayList<String> argumentos;
    
    public Instruccion () {
        comando = "";
        argumentos = new java.util.ArrayList<>();
    }
    
    // Constructor que recibe un comando en crudo.
    // Ej. "send file1.txt file2.txt"
    // Separa los parámetros por espacios
    public Instruccion (String comandoCrudo) {
        String[] partesComando = comandoCrudo.split(" ");
        argumentos = new ArrayList<>();
        if ( comandoCrudo.length() == 0 ) {
            comando = "";   
            return;
        }
        for ( int i = 1 ; i < partesComando.length ; i++ ) {
            argumentos.add(partesComando[i]);
        }
        this.comando = partesComando[0];
    }

    public Instruccion(String comando, ArrayList<String> argumentos) {
        this.comando = comando;
        this.argumentos = argumentos;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public ArrayList<String> getArgumentos() {
        return argumentos;
    }

    public void setArgumentos(ArrayList<String> argumentos) {
        this.argumentos = argumentos;
    }
    
    public String getArgumento (int index) {
        if ( index <= argumentos.size() - 1 ) {
            return argumentos.get(index);
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        String argumentosString = "Argumentos = { ";
        for ( int i = 0 ; i < argumentos.size() ; i++ ) {
            argumentosString += argumentos.get(i) + " ";
        }
        argumentosString += "}";
        return "Instruccion{" + "comando=" + comando + argumentosString;
    }
    
    
    
}
