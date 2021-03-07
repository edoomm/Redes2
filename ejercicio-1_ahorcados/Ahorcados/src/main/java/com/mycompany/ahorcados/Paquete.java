package com.mycompany.ahorcados;

import java.io.Serializable;

public class Paquete implements Serializable {
    private String palabra;
    private String mensaje;
    private char caracter;

    public Paquete() {
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public char getCaracter() {
        return caracter;
    }

    public void setCaracter(char caracter) {
        this.caracter = caracter;
    }
    
}
