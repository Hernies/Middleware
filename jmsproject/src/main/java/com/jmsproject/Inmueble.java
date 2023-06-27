package com.jmsproject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

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

    public Inmueble(String fichero, Session sesion) throws JMSException, IOException {
        this.fichero = fichero;
        if (fichero.startsWith("Centro")) {
            this.delegacion = "Centro";
            myTopic = sesion.createTopic("Centro");
        } else if (fichero.startsWith("Norte")) {
            this.delegacion = "Norte";
            myTopic = sesion.createTopic("Norte");
        } else if (fichero.startsWith("Sur")) {
            this.delegacion = "Sur";
            myTopic = sesion.createTopic("Sur");
        } else if (fichero.startsWith("Este")) {
            this.delegacion = "Este";
            myTopic = sesion.createTopic("Este");
        } else if (fichero.startsWith("Oeste")) {
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
        String []idsel= this.fichero.split("_");
        String id = idsel[idsel.length-1].charAt(0)+"";
        ofiMessage.setIntProperty("IDOficina",Integer.parseInt(id));
        System.out.println("Enviando mensaje a " + delegacion);
        msgProducer.send(ofiMessage);
    }

    private void procesar() throws IOException {
        try {
            File file = new File(Paths.get(System.getProperty("user.dir"), "jmsproject/src/main/Ficheros_Practica_1/").toString()+"/"+ fichero);
            Scanner scanner = new Scanner(file);

            // Saltamos la primera linea
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] datos = line.split(";");

                if (datos.length == 5) {
                    String distrito = datos[0].trim();
                    String precio = datos[1].trim();
                    String nHab = datos[2].trim();
                    String nBanos = datos[3].trim();
                    String superficie = datos[4].trim();

                    Oficina oficina = new Oficina(distrito, precio, nHab, nBanos, superficie);
                    oficinas.add(oficina);

                    System.out.println("Oficina: " + oficina.toString());
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // main function
    public static void main(String[] args) throws JMSException {
        try {
            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection myConn = myConnFactory.createConnection();
            Session mySesion = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // MessageProducer producer = mySesion.createProducer();
            String relPath = "jmsproject/src/main/Ficheros_Practica_1";
            Path filePath = Paths.get(System.getProperty("user.dir"), relPath);
            String[] path = new File(filePath.toString()).list();
            for (String pathname : path) {
                new Inmueble(pathname, mySesion);
            }
            mySesion.close();
            myConn.close();
        } catch (Exception jsme) {
            jsme.printStackTrace();
        }
    }

}
