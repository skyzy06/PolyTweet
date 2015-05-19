package publish;

import javax.jms.*;
import javax.naming.*;

public class Pub {

    private Connection connect = null;
    private Session sendSession = null;
    private MessageProducer sender = null;
    private Queue queue = null;
    InitialContext context = null;

    private void configurer() throws JMSException {

        try {	// Create a connection
            // Si le producteur et le consommateur étaient codés séparément, ils auraient eu ce même bout de code

            context = new InitialContext();
            context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            connect = factory.createConnection();
            this.configurerPublisher();
            //connect.start(); // on peut activer la connection. 
        } catch (JMSException | NamingException jmse) {
            jmse.printStackTrace();
        }
        this.publier();
    }

    private void configurerPublisher() throws JMSException, NamingException {
        // Dans ce programme, on decide que le producteur decouvre la queue (ce qui la crééra si le nom n'est pas encore utilisé) 
        // et y accedera au cours d'1 session
        sendSession = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = (Topic) context.lookup("dynamicTopics/topicExo2");
        sender = sendSession.createProducer(topic);

    }

    private void publier() throws JMSException {
        for (int i = 1; i <= 10; i++) {
            //Fabriquer un message
            MapMessage mess = sendSession.createMapMessage();
            mess.setInt("num", i);
            mess.setString("nom", i + "-");
            if (i % 2 == 0) {
                mess.setStringProperty("typeMess", "important");
            }
            if (i == 1) {
                mess.setIntProperty("numMess", 1);
            }
            //Poster ce message dans la queue
            sender.send(mess); // equivaut à publier dans le topic
        }
    }

    public static void main(String[] args) {
        try {
            (new Pub()).configurer();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
