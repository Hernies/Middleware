package com.jmsproject;
import java.io.Serializable;

public class DelegacionObj implements Serializable {
    //Delegacion attributes: precio, nHab, nBan, superficie, delegacion, distrito
    private int precio;
    private int nHab;
    private int nBan;
    private int superficie;
    private float precioM2;
    private String distrito;
    
    //constructor for Delegacion and its attributes, adding it to the Delegaciones hashmap
    public DelegacionObj(int precio, int nHab, int nBan, int superficie, float precioM2, String delegacion, String distrito){
        this.precio = precio;
        this.nHab = nHab;
        this.nBan = nBan;
        this.precioM2 = precioM2;
        this.superficie = superficie;
        this.distrito = distrito;
       
    }
    public DelegacionObj() {
	}
	//getters and setters
    public int getPrecio() {
        return precio;
    }
    public void setPrecio(int precio) {
        this.precio = precio;
    }
    public int getnHab() {
        return nHab;
    }
    public void setnHab(int nHab) {
        this.nHab = nHab;
    }
    public int getnBan() {
        return nBan;
    }
    public void setnBan(int nBan) {
        this.nBan = nBan;
    }
    public int getSuperficie() {
        return superficie;
    }
    public void setSuperficie(int superficie) {
        this.superficie = superficie;
    }
    public String getDistrito() {
        return distrito;
    }
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
    public float getPrecioM2() {
        return precioM2;
    }
    public void setPrecioM2(float precioM2) {
        this.precioM2 = precioM2;
    }
}
