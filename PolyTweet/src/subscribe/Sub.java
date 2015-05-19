package subscribe;

import javax.jms.*;
import javax.naming.*;

public class Sub implements MessageListener {

    private javax.jms.Connection connect = null;
    private javax.jms.Session receiveSession = null;
    Context context = null;

    private void configurer() throws JMSException {

        try {	// Create a connection

            context = new InitialContext();
            context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            connect = factory.createConnection();

            this.configurerSouscripteur();
            connect.start(); // on peut activer la connection. 
        } catch (JMSException | NamingException jmse) {
            jmse.printStackTrace();
        }
    }

    private void configurerSouscripteur() throws JMSException, NamingException {
        // Pour consommer, il faudra simplement ouvrir une session 
        receiveSession = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // et dire dans cette session quelle queue(s) et topic(s) on accèdera et dans quel mode
        Topic topic = (Topic) context.lookup("dynamicTopics/topicExo2");
        System.out.println("Nom du topic " + topic.getTopicName());
        MessageConsumer topicReceiver = receiveSession.createConsumer(topic);//,"Conso");//,"typeMess = 'important'");
        //topicReceiver.setMessageListener(this); 
        //ESSAI d'une reception synchrone
        connect.start(); // on peut activer la connection. 
        while (true) {
            Message m = topicReceiver.receive();
            System.out.print("recept synch: ");
            onMessage(m);
        }
    }

    public static void main(String[] args) {
        try {
            (new Sub()).configurer();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessage(Message message) {
        // Methode permettant au souscripteur de consommer effectivement chaque msg recu
        // via le topic auquel il a souscrit
        try {
            System.out.print("Recu un message du topic: " + ((MapMessage) message).getString("nom"));
            System.out.println(((MapMessage) message).getString("num"));
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
