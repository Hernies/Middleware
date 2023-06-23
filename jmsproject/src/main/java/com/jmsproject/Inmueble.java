package com.jmsproject;

public class Inmueble {
    // crear constructor de inmueble con distrito,precio,numero de dormitorios,numero de baños y superficie
    private String delegacion;
    private String distrito;
    private int precio;
    private int numeroDormitorios;
    private int numeroBanos;
    private int superficie;
    private int preciom2;
    
    //Constructor
    public Inmueble(String delegacion, String distrito, int precio, int numeroDormitorios, int numeroBanos, int superficie) {
        this.delegacion = delegacion;
        this.distrito = distrito;
        this.precio = precio;
        this.numeroDormitorios = numeroDormitorios;
        this.numeroBanos = numeroBanos;
        this.superficie = superficie;
    }

    public Inmueble(String delegacion, String distrito, int precio, int numeroDormitorios, int numeroBanos, int superficie, int preciom2) {
        this.delegacion = delegacion;
        this.distrito = distrito;
        this.precio = precio;
        this.numeroDormitorios = numeroDormitorios;
        this.numeroBanos = numeroBanos;
        this.superficie = superficie;
        this.preciom2 = preciom2;
    }

    //getters 
    public String getDelegacion() {
        return delegacion;
    }

    public String getDistrito() {
        return distrito;
    }

    public int getPrecio() {
        return precio;
    }

    public int getNumeroDormitorios() {
        return numeroDormitorios;
    }

    public int getNumeroBanos() {
        return numeroBanos;
    }   

    public int getSuperficie() {
        return superficie;
    }

    public int getPreciom2() {
        return preciom2;
    }
}