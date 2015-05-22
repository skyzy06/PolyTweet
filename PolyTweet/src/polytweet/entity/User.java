/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polytweet.entity;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Thomas
 */
public class User implements MessageListener {

    private Connection connect = null;
    private Session sendSession = null;
    private Session receiveSession = null;
    private MessageProducer sender = null;
    private Queue queue = null;
    public Context context = null;

    public void writeMessage(String message) {

    }

    public void refresh() {

    }

    private void configurer() throws JMSException {

        try {	// Create a connection

            context = new InitialContext();
            context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            connect = factory.createConnection();

            connect.start(); // on peut activer la connection. 
        } catch (JMSException | NamingException jmse) {
            jmse.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        // Methode permettant au souscripteur de consommer effectivement chaque msg recu
        // via le topic auquel il a souscrit
        try {
            System.out.print("Recu un message du topic: " + ((Tweet) message).getTweet());
            System.out.println(((MapMessage) message).getString("num"));
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
