package com.mycompany.ahorcados;

import java.util.ArrayList;
import java.util.TreeSet;

public class PalabraOculta {
    private String palabra;
    private TreeSet<Character> caracteresDescubiertos;
    private ArrayList<Integer> indicesDescubiertos;

    public PalabraOculta(String palabra) {
        this.palabra = palabra;
        indicesDescubiertos = new java.util.ArrayList<>();
        caracteresDescubiertos = new java.util.TreeSet<>();
    }
    
    // Regresa la palabra, pero los indices no descubiertos
    // son sustituidos por guiones
    public String getPalabraCodificada () {
        char[] palabraCodificada = new char[palabra.length()];
        for ( int i = 0 ; i < indicesDescubiertos.size() ; i++ ) {
            palabraCodificada[indicesDescubiertos.get(i)] = palabra.charAt(indicesDescubiertos.get(i));
        }
        for (  int i = 0 ; i < palabraCodificada.length ; i++) {
            if ( palabraCodificada[i] == '\u0000' ) {
                palabraCodificada[i] = '_';
            }
        }
        return String.valueOf(palabraCodificada);
    }
    
    public boolean descubrirCaracter ( char caracter ) {
        char[] palabraDescubierta = palabra.toCharArray();
        boolean descubrio = false;
        for ( int i = 0 ; i < palabraDescubierta.length ; i++ ) {
            if ( palabraDescubierta[i] == caracter ) {
                if ( !caracteresDescubiertos.contains(caracter) ) {
                    indicesDescubiertos.add(i);
                }
                descubrio = true;
            }
        }
        if ( descubrio ) {
            caracteresDescubiertos.add(caracter);
        }
        return descubrio;
    }
    
    // Regresa true si todos los caracteres fueron descubiertos
    // Regresa false si falta algún caracter por descubrir
    public boolean isDescubierta () {
        
        return indicesDescubiertos.size() == palabra.length();
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public ArrayList<Integer> getIndicesDescubiertos() {
        return indicesDescubiertos;
    }

    public void setIndicesDescubiertos(ArrayList<Integer> indicesDescubiertos) {
        this.indicesDescubiertos = indicesDescubiertos;
    }
    
    public static void main(String[] args) {
        PalabraOculta p = new PalabraOculta("Hola");
        if ( !p.descubrirCaracter('a') )
            System.out.println(p.getPalabraCodificada());
    }
    
}
