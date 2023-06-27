package com.jmsproject;

import java.util.ArrayList;
import javax.jms.*;

public class Delegacion implements Runnable {
    private Session session;
    private Connection connection;
    private ArrayList<Oficina> inmuebles;
    private ArrayList<DelegacionObj> direccion;
    private ArrayList<DelegacionObj> negocio;
    private String delegacion;
    private Topic myTopic;

    class InmuebleListener implements MessageListener {
        public boolean done = false;

        @SuppressWarnings("unchecked")
        public void onMessage(Message msg) {
            if (msg instanceof ObjectMessage) {
                done = true;
                ObjectMessage objMsg = (ObjectMessage) msg;
                try {
                    inmuebles = (ArrayList<Oficina>) objMsg.getObject();
                    System.out.println("Recibido mensaje de " + objMsg.getStringProperty("Delegacion"));
                    for (Oficina o : inmuebles)
                        System.out.println(o.toString());
                    procesar(objMsg.getStringProperty("Delegacion"));
                    System.out.println("Mensaje procesado de Oficina: " + objMsg.getIntProperty("IDOficina"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

        private void procesar(String delegacion) {
            for (Oficina o : inmuebles) {
                try {
                    if (o.getPrecio().equals(" "))
                        System.out.println("Error en los datos");
                    else if (o.getSuperficie().equals(" "))
                        System.out.println("Error en los datos");
                    else if (o.getnBan().equals(" "))
                        System.out.println("Error en los datos");
                    else if (o.getnHab().equals(" "))
                        System.out.println("Error en los datos");
                    else {
                        System.out.println("precio " + o.getPrecio() + "\n");
                        System.out.println("superficie " + o.getSuperficie() + "\n");
                        System.out.println("nBan " + o.getnBan() + "\n");
                        System.out.println("nHab " + o.getnHab() + "\n");
                        Integer precio = Integer.parseInt(o.getPrecio());
                        Integer nHab = Integer.parseInt(o.getnHab());
                        Integer nBan = Integer.parseInt(o.getnBan());
                        Integer superficie = Integer.parseInt(o.getSuperficie());
                        if (superficie > 0) {
                            String distrito = o.getDistrito();
                            float preciom2 = precio / superficie;
                            DelegacionObj d = new DelegacionObj();
                            d.setDistrito(distrito);
                            d.setPrecio(precio);
                            d.setnHab(nHab);
                            d.setnBan(nBan);
                            d.setSuperficie(superficie);
                            d.setPrecioM2(preciom2);
                            System.out
                                    .println(o.getDistrito() + " " + o.getPrecio() + " " + o.getnHab() + " " + o.getnBan()
                                            + " " + o.getSuperficie());
                            if ((d.getDistrito().equals("Barrio-de-Salamanca") || d.getDistrito().equals("Chamberi"))
                                    && d.getPrecio() > 500000) {
                                direccion.add(d);
                            } else if (preciom2 > 1500 && preciom2 < 3000) {
                                negocio.add(d);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Datos no casteables, error en los:\n");
                    System.out.println(o.getDistrito() + " " + o.getPrecio() + " " + o.getnHab() + " " + o.getnBan()
                            + " " + o.getSuperficie());
                    System.out.println();
                } catch (Exception e) {
                    System.out.println("Error en el procesamiento de los datos");
                    e.printStackTrace();
                }
            }
        }
    }

    public Delegacion(String delegacion, Session mySess, Connection myConn) {
        this.delegacion = delegacion;
        this.session = mySess;
        this.connection = myConn;
        direccion = new ArrayList<DelegacionObj>();
        negocio = new ArrayList<DelegacionObj>();
        try {
            myTopic = session.createTopic(delegacion);
            MessageConsumer consumer = session.createConsumer(myTopic);
            InmuebleListener myListener = new InmuebleListener();
            consumer.setMessageListener(myListener);
            connection.start();
            System.out.println("Esperando un mensaje...");
        } catch (JMSException e) {
            System.out.println("Error al inicializar JMS");
            e.printStackTrace();
        }
    }

    public void enviarMensaje(ArrayList<DelegacionObj> direccion, String equipo) {
        try {
            ObjectMessage objMsg = session.createObjectMessage();
            objMsg.setObject(direccion);
            objMsg.setStringProperty("Delegacion", delegacion);
            System.out.println("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + objMsg.getStringProperty("Delegacion").toString());
            if (equipo.equals("Direccion")) {
                System.out.println("enviando mensaje a Direccion");
                MessageProducer producerD = session.createProducer(session.createTopic("Direccion"));
                producerD.send(objMsg);
            } else if (equipo.equals("Negocio")) {
                System.out.println("enviando mensaje a Negocio");
                MessageProducer producerN = session.createProducer(session.createTopic("Negocio")); 
                producerN.send(objMsg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(myTopic);
            InmuebleListener myListener = new InmuebleListener();
            consumer.setMessageListener(myListener);
            connection.start();
            System.out.println("Esperando un mensaje...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
