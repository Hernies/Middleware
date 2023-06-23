package com.jmsproject;

//importar javaJMS
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.messaging.ConnectionConfiguration;
import javax.jms.*;

public class Oficina {
    // Oficina tiene nombre de delegacion y numero de oficina
    private String file;
    private String delegacion;
    private int numeroOficina;
    private ArrayList<Inmueble> inmuebles;
    private Session session;
    public MessageProducer producer;
    public Topic topic;

    // Constructor
    public Oficina(String file, Session session) throws JMSException, IOException {
        this.file = file;

        if (file.startsWith("Norte")) {
            this.delegacion = "Norte";
            topic = new com.sun.messaging.Topic("Norte");
        }

        else if (file.startsWith("Sur")) {
            this.delegacion = "Sur";
            topic = new com.sun.messaging.Topic("Sur");
        }

        else if (file.startsWith("Este")) {
            this.delegacion = "Este";
            topic = new com.sun.messaging.Topic("Este");
        }

        else if (file.startsWith("Oeste")) {
            this.delegacion = "Oeste";
            topic = new com.sun.messaging.Topic("Oeste");
        }

        else if (file.startsWith("Centro")) {
            this.delegacion = "Centro";
            topic = new com.sun.messaging.Topic("Centro");
        }

        else {
            System.out.println("Delegacion no encontrada");
        }
        // numeroOficina is the las element before .txt
        this.numeroOficina = Integer.parseInt(file.split("_")[2].split(".txt")[0]);
        this.session = session;
        producer = session.createProducer(topic);
        inmuebles = new ArrayList<Inmueble>();
        cargarInmuebles(file);
        enviarInmueble();

    }

    // Metodo para leer el fichero de texto linea a linea
    public void cargarInmuebles(String file) throws IOException {

        // String nombreFichero = delegacion + "_OFICINA_" + numeroOficina + ".txt";
        FileReader fr = new FileReader("../Ficheros_Practica_1" + file);
        BufferedReader br = new BufferedReader(fr);
        String linea;
        while ((linea = br.readLine()) != null) {
            // separar los datos de la linea por comas
            String[] datos = linea.split(",");
            // crear un inmueble con el getter de delegacion y los datos de la linea
            Inmueble inmueble = new Inmueble(delegacion, datos[0], Integer.parseInt(datos[1]),
                    Integer.parseInt(datos[2]), Integer.parseInt(datos[3]), Integer.parseInt(datos[4]));

            // añadir el inmueble a la lista de inmuebles de la oficina
            inmuebles.add(inmueble);
        }
        // Print inmuebles
        for (Inmueble inmueble : inmuebles) {
            System.out.println(inmueble.toString());
        }
        br.close();
    }

    public void enviarInmueble() throws JMSException {
        // crear conexion con el servidor de mensajeria
        ObjectMessage msg = session.createObjectMessage();
        msg.setObject(inmuebles);
        msg.setStringProperty("delegacion", delegacion);
        msg.setIntProperty("numero oficina", numeroOficina);
        System.out.println("Enviando mensaje a " + delegacion + "de la oficina" + numeroOficina);
        this.producer.send(msg);
    }

    public static void main(String[] args) throws JMSException {
        try {
            ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String[] ficheros;
            File f = new File("../Ficheros_Practica_1");
            ficheros = f.list();
            for (String file : ficheros) {
                Oficina oficina = new Oficina(file, session);
            }
            session.close();
            connection.close();
        } catch (Exception jmse) {
            System.out.println("Error al crear la conexion\n");
        }
    }
}