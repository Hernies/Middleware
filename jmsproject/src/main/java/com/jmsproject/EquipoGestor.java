package com.jmsproject;

import java.util.ArrayList;

import javax.jms.*;

public class EquipoGestor {
	private static Session mySess;
	private static Connection myConn;
	private Topic myTopic;
	private MessageConsumer myConsumer;
	private static String[] equiposGestores = { "Negocio", "Direccion" };
	private static ArrayList<DelegacionObj> listaRecibidos;


	public static class DelegacionesListener implements MessageListener {
		public boolean recibido = false;

		@SuppressWarnings("unchecked")
		public void onMessage(Message msg) {
			if (msg instanceof ObjectMessage) {
				recibido = true;
				ObjectMessage objMsg = (ObjectMessage) msg;
				try {
					listaRecibidos = (ArrayList<DelegacionObj>) objMsg.getObject();
					for (DelegacionObj del : listaRecibidos) {
						System.out.println("\tLeyendo el mensaje\n\t" + del.toString());
					}

				} catch (Exception e) {
					System.out.println("Exception in onMessage(): " + e.toString());
				}
			}
		}
	}

	public EquipoGestor(String equipoGestor) {
		listaRecibidos = new ArrayList<DelegacionObj>();
		try {
			// Create a JMS topic
			myTopic = mySess.createTopic(equipoGestor);
			// Create a JMS publisher and subscriber
			myConsumer = mySess.createConsumer(myTopic);
			// Set a JMS message listener and include the class that will handle the message
			EquipoGestor.DelegacionesListener myListener = new DelegacionesListener();
			myConsumer.setMessageListener(myListener);
			myConn.start();
		} catch (JMSException e) {
			System.out.println("Error al inicializar JMS");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Crea una connection factory
		com.sun.messaging.ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		// Crea una JMS connection
		try {
			myConn = connectionFactory.createConnection();
			// Crea una JMS session
			mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Crea equipos gestores
			ArrayList<Thread> threads = new ArrayList<>();
			for (String equipoGestor : equiposGestores) {
				Thread thread = new Thread(() -> {
					new EquipoGestor(equipoGestor);
				});
				threads.add(thread);
				thread.start();
			}

			// Wait for all threads to finish
			for (Thread thread : threads) {
				thread.join();
			}

			myConn.close();
			mySess.close();
		} catch (JMSException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}
