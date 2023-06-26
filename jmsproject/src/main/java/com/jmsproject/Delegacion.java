package com.jmsproject;

import java.util.ArrayList;
import javax.jms.*;

public class Delegacion implements Runnable{ // TODO IMPLEMENT DELEGACIONES AS RUNNABLE
    private static String[] delegaciones = { "Centro", "Norte", "Sur", "Este", "Oeste" };
    private static Session session;
    private static Connection connection;
    
    private ArrayList<Oficina> inmuebles;
    

    private ArrayList<DelegacionObj> direccion;
    private ArrayList<DelegacionObj> negocio;
    

    public static MessageConsumer consumer;
    public MessageProducer producerD;
    public MessageProducer producerN;
    public Topic myTopicD;
    public Topic myTopicN;
    public String delegacion;
    public Topic myTopic;

    // IDEA GENERAL:
    // recibo mensaje del gestor de ficheros
    // "desempaqueto" mensaje
    // creo 1 objeto delegacion por cada objeto inmuebles que recibo
    // hago el calculo de superficie
    // si falla-> printear error datos incorrectos
    // comprobar si es la delegación que corresponde (opcional) y enviar en ese caso
    // añado el objeto delegacion a la lista del mensaje que voy a enviar
    // delegaciones
    // envio mensaje

    // TODO (OPCIONAL) hacer comprobación de que el objeto recibido corresponde a la delegacion
    // que debe

    class InmuebleListener implements MessageListener {
        public boolean done = false;

        @Override
        public void onMessage(Message msg) {
            if (msg instanceof ObjectMessage) {
                done = true;
                ObjectMessage objMsg = (ObjectMessage) msg;
                try {
                    inmuebles = (ArrayList<Oficina>) objMsg.getObject();
                    System.out.println("Recibido mensaje de " + objMsg.getStringProperty("Delegacion"));
                    for(Oficina o: inmuebles) System.out.println(o.toString());
                    procesar(objMsg.getStringProperty("Delegacion"));
                    System.out.println("Mensaje procesado" + objMsg.getIntProperty("IDOficina"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

        // Procesar el mensaje recibido y enviarlo al equipo gestor correspondiente
        private void procesar(String delegacion) {
            // recorres mensaje
            for (Oficina o : inmuebles) {
                try {
                    //parseamos el objeto (si falla el parse a float/integer, se lanza una excepcion)
                    if(o.getPrecio().equals(" "))System.out.println("Error en los datos");
                    else if(o.getSuperficie().equals(" ") )System.out.println("Error en los datos");
                    else if(o.getnBan().equals(" ") )System.out.println("Error en los datos");
                    else if(o.getnHab().equals(" ")) System.out.println("Error en los datos");
                    else{
                    Integer precio = Integer.parseInt(o.getPrecio());
                    Integer nHab = Integer.parseInt(o.getnHab());
                    Integer nBan = Integer.parseInt(o.getnBan());
                    Integer superficie=Integer.parseInt(o.getSuperficie());
                    if(superficie > 0){
                        String distrito= o.getDistrito();
                        float preciom2 = precio/superficie; 
                        DelegacionObj d = new DelegacionObj();
                        d.setDistrito(distrito);
                        d.setPrecio(precio);
                        d.setnHab(nHab);
                        d.setnBan(nBan);
                        d.setSuperficie(superficie);
                        d.setPrecioM2(preciom2);
                        System.out.println(o.getDistrito() + " "+ o.getPrecio() + " " + o.getnHab() + " " + o.getnBan() + " " + o.getSuperficie());
                        //condiciones de inmueble relevante para equipo gestor Dirección
                        if((d.getDistrito().equals("Barrio-de-Salamanca") || d.getDistrito().equals("Chamberi")) && d.getPrecio() > 500000){
                            direccion.add(d);
                        }
                        else if(preciom2 > 1500 && preciom2 < 3000){
                            negocio.add(d);
                        }
                    }
                }
                } catch (NumberFormatException e) { 
                    //TODO mejorar información por error de parseo
                    System.out.println("Datos no casteables, error en los:\n");
                    System.out.println(o.getDistrito() + " "+o.getPrecio() + " " + o.getnHab() + " " + o.getnBan() + " " + o.getSuperficie());
                    System.out.println();
                } catch (Exception e) {
                    System.out.println("Error en el procesamiento de los datos");
                    e.printStackTrace();
                }
            }
        }
    }

    // Class constructor
    public Delegacion(String delegacion) throws JMSException, InterruptedException { //FIXME
        this.delegacion = delegacion;
        direccion = new ArrayList<DelegacionObj>();
        negocio = new ArrayList<DelegacionObj>();
        try {
            // Creamos un JMS topic
            myTopic = session.createTopic(delegacion);
            myTopicN = session.createTopic("Negocio");
            myTopicD = session.createTopic("Direccion");
        // Creamos un consumidor y productor JMS TODO ESTO SOBRA, NO HACE FALTA CREAR MULTIPLES CONSUMIDORES
            consumer = session.createConsumer(myTopic);
            producerN = session.createProducer(myTopicN);
            producerD = session.createProducer(myTopicD);
            // Creamos un listener para el consumidor
            InmuebleListener myListener = new InmuebleListener();
            consumer.setMessageListener(myListener);
            connection.start();
            System.out.println("Esperando un mensaje...");
            while (!myListener.done) {
                System.out.println("\t comprobando si hay mensajes para enviar ... ");
                //si la cola de datos relevantes no esta vacia, enviar mensaje
                if(!direccion.isEmpty()){
                    System.out.println("Enviando mensaje a dirección ...");
                    enviarMensaje(direccion, "Direccion");
                }
                if(!negocio.isEmpty()){
                    System.out.println("Enviando mensaje a negocio ...");
                    enviarMensaje(negocio, "Negocio");
                }
                Thread.sleep(100);
            }
            
            //enviar mensajes a equipos gestores
            //cerrar conexion 
        } catch (JMSException e) {
            System.out.println("Error al inicializar JMS");
            e.printStackTrace();
        }
    }

    // FUNCION QUE ENVIA EL MENSAJE
    public void enviarMensaje(ArrayList<DelegacionObj> direccion, String equipo) {
        try {
            ObjectMessage objMsg = session.createObjectMessage();
            objMsg.setObject(direccion);
            objMsg.setStringProperty("Delegacion", delegacion);
            System.out.println("\n"+"\n"+"\n"+"\n"+"\n"+"\n"+objMsg.getStringProperty("Delegacion").toString());
            if(equipo.equals("Direccion")){
                System.out.println("enviando mensaje a Direccion");
                producerD.send(objMsg);
            }
            else if(equipo.equals("Negocio")){
                System.out.println("enviando mensaje a Negocio");
                producerN.send(objMsg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
            }
    }

    // implement run
    public void run() {
        try {
             //TODO RELLENAR CON COMPORTAMIENTO EXTRA
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
