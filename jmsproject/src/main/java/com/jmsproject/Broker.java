package com.jmsproject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

public class Broker {
    private Delegacion[] delegaciones;
    private Connection myConn;
    private Session mySess;
    private static String[] nombres= { "Centro", "Norte", "Sur", "Este", "Oeste" };

    
    public Broker(Session mySess,Connection myConn){
        this.myConn = myConn;
        this.mySess = mySess;
        delegaciones = new Delegacion[5];
    }
    
    public void crearDelegaciones() throws JMSException, InterruptedException{
         ExecutorService exec = Executors.newFixedThreadPool(delegaciones.length);
	        for (int i=0; i < delegaciones.length; i++){
	            String region = nombres[i];
	            delegaciones[i] = new Delegacion(region, mySess, myConn); 
	            exec.execute(delegaciones[i]);
	        }
	        exec.shutdown();
    }
   
    public static void main(String[] args) throws InterruptedException {
        ConnectionFactory myConnFactory;
        Session mySess;
		try {
			myConnFactory = new com.sun.messaging.ConnectionFactory();
			Connection myConn = myConnFactory.createConnection();
			mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Broker broker = new Broker(mySess, myConn);
            broker.crearDelegaciones();
			mySess.close();
			myConn.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }
}