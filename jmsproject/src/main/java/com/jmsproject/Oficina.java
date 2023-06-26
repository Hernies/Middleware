package com.jmsproject;
import java.io.Serializable;

//com.sun.messaging package import

public class Oficina implements Serializable{
    private String distrito;
    private String precio;
    private String nHab;
    private String nBan;
    private String superficie;

    
    public Oficina(String distrito, String precio, String nHab, String nBan, String superficie) {
        this.distrito = distrito;
        this.precio = precio;
        this.nHab = nHab;
        this.nBan = nBan;
        this.superficie = superficie;
    }
    //empty constructor
    public Oficina() {
    }

    //getters and setters
    public String getPrecio() {
        return precio;
    }
    public void setPrecio(String precio) {
        this.precio = precio;
    }
    public String getnHab() {
        return nHab;
    }
    public void setnHab(String nHab) {
        this.nHab = nHab;
    }
    public String getnBan() {
        return nBan;
    }
    public void setnBan(String nBan) {
        this.nBan = nBan;
    }
    public String getSuperficie() {
        return superficie;
    }
    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }
    public String getDistrito() {
        return distrito;
    }
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }   
    //to string method
    @Override
    public String toString() {
        return "Oficina{" + "distrito=" + distrito + ", precio=" + precio + ", nHab=" + nHab + ", nBan=" + nBan + ", superficie=" + superficie + '}';
    }
}