package com.jmsproject;

import java.util.ArrayList;
import java.util.List;
import com.sun.messaging.ConnectionConfiguration;
import javax.jms.*;

public class Delegacion {
    private String nombre;
    private List<Oficina> oficinas;
    private EquipoGestion equipoGestion;
    private ArrayList<Inmueble> inmueblesCambiados;

    public Delegacion(String nombre, List<Oficina> oficinas, EquipoGestion equipoGestion) {
        this.nombre = nombre;
        this.oficinas = new ArrayList<>();
        this.equipoGestion = equipoGestion;
    }

    public void recibirMensaje(ObjectMessage message) throws JMSException {
        Object object = message.getObject();
        if (object instanceof ArrayList) {
            ArrayList<Inmueble> inmuebles = (ArrayList<Inmueble>) message.getObject();
            for (Inmueble inmueble : inmuebles) {
                Inmueble inmuebleCambiado = calcularPrecio(inmueble);
                inmueblesCambiados.add(inmuebleCambiado);
            }
        }
        else{
            System.out.println("Error no se ha recibido un arraylist\n");
        }
        enviarInmueblesCambiados(null,inmueblesCambiados);
    }

    public Inmueble calcularPrecio(Inmueble inmueble) {
        int precio = inmueble.getPrecio();
        int numeroDormitorios = inmueble.getNumeroDormitorios();
        int numeroBanos = inmueble.getNumeroBanos();
        int superficie = inmueble.getSuperficie();
        int precioFinal = inmueble.getPrecio()/inmueble.getSuperficie();
        Inmueble inmuebleDelegacion = new Inmueble(inmueble.getDelegacion(), inmueble.getDistrito(), precio, numeroDormitorios, numeroBanos, superficie, precioFinal);
        return inmuebleDelegacion;
    }

    private void enviarInmueblesCambiados(ObjectMessage msg, ArrayList<Inmueble> lista) throws JMSException{
         // crear conexion con el servidor de mensajeria
         msg.setObject(lista);
         ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
         Connection connection = null;
         try {
             connection = connectionFactory.createConnection();
             connection.start();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             Destination destination = session.createQueue("Oficina");
             MessageProducer producer = session.createProducer(destination);
             TextMessage message = session.createTextMessage();
             message.setText(msg.toString());
             producer.send(message);
             System.out.println("Mensaje enviado: " + message.getText());
             session.close();
             connection.close();
         } catch (JMSException e) {
             e.printStackTrace();
         }
    }
}