package com.jmsproject;

import com.sun.messaging.ConnectionConfiguration;

import java.util.ArrayList;

import javax.jms.*;

public class Productor {
    public void enviarInmueble(ObjectMessage msg, ArrayList<Inmueble> lista) throws JMSException{
        // crear conexion con el servidor de mensajeria
        msg.setObject(lista);
        ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("Delegacion");
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