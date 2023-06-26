package com.jmsproject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

public class Inmueble {
    private String fichero;
	private String delegacion;
	private Session sesion;
    private ArrayList<Oficina> oficinas = new ArrayList<Oficina>();
    public MessageProducer msgProducer;
	public Topic myTopic;

    public Inmueble(String fichero, Session sesion) throws JMSException, IOException{
        this.fichero = fichero;
        if (fichero.startsWith("Centro")){
            this.delegacion = "Centro";
            myTopic = sesion.createTopic("Centro");
        } else if (fichero.startsWith("Norte")){
            this.delegacion = "Norte";
            myTopic = sesion.createTopic("Norte");
        } else if (fichero.startsWith("Sur")){
            this.delegacion = "Sur";
            myTopic = sesion.createTopic("Sur");
        } else if (fichero.startsWith("Este")){
            this.delegacion = "Este";
            myTopic = sesion.createTopic("Este");
        } else if (fichero.startsWith("Oeste")){
            this.delegacion = "Oeste";
            myTopic = sesion.createTopic("Oeste");
        }
        this.sesion = sesion;
        System.out.println(fichero);
        msgProducer = sesion.createProducer(myTopic);
        procesar();
        enviar();
    }

    private void enviar() throws JMSException {
        ObjectMessage ofiMessage = sesion.createObjectMessage();
        ofiMessage.setObject(oficinas);
        ofiMessage.setStringProperty("Delegacion", delegacion);
        System.out.println("Enviando mensaje a " + delegacion);
        msgProducer.send(ofiMessage);
    }

    // TODO MEJORAR PROCESAR
    private void procesar() throws IOException { 
        BufferedReader rdr = new BufferedReader (new FileReader ("../src/Ficheros_Practica_1/"+fichero));
		String strCurrentLine;
		while ((strCurrentLine = rdr.readLine()) != null) {
            Oficina oficina;
			String distrito = " ";
			String precio = "";
			String nHab=" ";
			String nBan=" ";
            String superficie=" ";
			String [] parts = strCurrentLine.split(",");            
            for(int i = 0; i< parts.length; i++){
                System.out.println(parts[i]);
            }
            if(parts.length == 1){
                distrito = parts[0];
            }
            if(parts.length == 2){
                distrito = parts[0];
                precio = parts[1];
            }
            if(parts.length == 3){
                distrito = parts[0];
                precio = parts[1];
                nHab = parts[2];
            }
            if(parts.length == 4){
                distrito = parts[0];
                precio = parts[1];
                nHab = parts[2];
                nBan = parts[3];
            }
            if(parts.length == 5){
                distrito = parts[0];
                precio = parts[1];
                nHab = parts[2];
                nBan = parts[3];
                superficie = parts[4];
            }

			oficina = new Oficina(distrito, precio, nHab, nBan, superficie);
            System.out.println("\n Ofi"+oficina.toString()+"\n");
            oficinas.add(oficina);
        }
		rdr.close();
    }

    //main function
    public static void main(String[] args) throws JMSException {
        try{
            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection myConn = myConnFactory.createConnection();
            Session mySesion = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // MessageProducer producer = mySesion.createProducer();
            String relPath="src/main/Ficheros_Practica_1";
            Path filePath = Paths.get(System.getProperty("user.dir"), relPath);
            String[] path = new File(filePath.toString()).list(); 
            for(String pathname : path){
                 new Inmueble(pathname, mySesion);
                 Thread.sleep(5000);
            }    
            mySesion.close();
            myConn.close();
        } catch (Exception jsme){
            jsme.printStackTrace();
        }
    }
    
}
