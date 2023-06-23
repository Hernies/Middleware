package com.jmsproject;
import java.io.File;
public class Prueba {
    public static void main(String[] args) {
        File file = new File("jmsproject/src/main/Ficheros_Practica_1/Centro_OFICINA_1.txt");
        System.out.println(file.getName());
        String[] nombreFichero = file.getName().split("_");
        System.out.println(nombreFichero[0]);
        System.out.println(nombreFichero[2].split(".txt")[0]);
    }
}