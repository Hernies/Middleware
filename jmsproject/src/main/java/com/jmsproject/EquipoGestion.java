package com.jmsproject;
import java.util.ArrayList;
import javax.jms.*;

public class EquipoGestion {
	static Session mySess;
	private static Connection myConn;
	private Topic myTopic;
	private MessageConsumer myConsumer;
	private static String[] equiposGestores={"Negocio","Direccion"};
	private static ArrayList<DelegacionObj> listaRecibidos;
	// Los equipos gestores con consumidores de mensajes
	// implementar DelegacionListener
	// printear por pantalla

	public static class DelegacionesListener implements MessageListener {
		public boolean recibido = false;

		@Override
		public void onMessage(Message msg) {
			if (msg instanceof ObjectMessage) {
				recibido = true;
				ObjectMessage objMsg = (ObjectMessage) msg;
				try {
					listaRecibidos = (ArrayList<DelegacionObj>) objMsg.getObject();
					for (DelegacionObj del : listaRecibidos){
						System.out.println("\tLeyendo el mensaje\n\t"
							+ del.toString()); //FIXME debería printear nada más recibe el mensaje
					}

				} catch (Exception e) {
					System.out.println("Exception in onMessage(): " + e.toString());
				}
			}
		}
	}

	
	public EquipoGestion (String equipoGestor){
		listaRecibidos = new ArrayList<DelegacionObj>();
		try {
			// Create a JMS topic
			myTopic = mySess.createTopic(equipoGestor); 
			// Create a JMS publisher and subscriber
			myConsumer = mySess.createConsumer(myTopic);
			// Set a JMS message listener and include the class that will handle the message
			EquipoGestion.DelegacionesListener myListener = new DelegacionesListener();
			myConsumer.setMessageListener(myListener);
			myConn.start();
			// TODO MIENTRAS NO SE HAYA RECIBIDO EL MENSAJE
			while(!myListener.recibido){
				System.out.println("\tEsperando datos por 100 ms ... ");
				Thread.sleep(100);
			}
		} catch (JMSException e) {
			System.out.println("Error al inicializar JMS");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// escribir constructor meter el main en la clase y hacer un main q inicialice 2 equipos gestores: direccion y negocio

	public static void main(String[] args) throws JMSException {
			// Crea una connection factory
			com.sun.messaging.ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
			// Crea una JMS connection
			myConn = connectionFactory.createConnection();
			// Crea una JMS session
			mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//Crea equipoGestor
			for (String equipoGestor : equiposGestores) {
				new EquipoGestion(equipoGestor);
			}
			myConn.close();
			mySess.close();

	}

}