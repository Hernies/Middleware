package com.jmsproject;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

public class Broker {
    private Connection myConn;
    private Session mySess;
    private static String[] nombres= { "Centro", "Norte", "Sur", "Este", "Oeste" };

    
    public Broker(Session mySess,Connection myConn){
        this.myConn = myConn;
        this.mySess = mySess;
    }
    
    public void crearDelegaciones() {
    try {
        // Create teams of delegates
        ArrayList<Thread> threads = new ArrayList<>();

        for (String nombre : nombres) {
            Thread thread = new Thread(() -> {
                Delegacion delegacion = new Delegacion(nombre, mySess, myConn);
                delegacion.run();
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        // Keep the main thread alive to wait for messages
        while (true) {
            Thread.sleep(1000);
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
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