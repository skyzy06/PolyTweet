
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import polytweet.entity.Hashtag;
import polytweet.entity.Tweet;
import polytweet.interfaces.PolyInterface;
import polytweet.entity.User;

/**
 *
 * @author Thomas
 */
public class PolyClient implements MessageListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PolyClient poly = new PolyClient("localhost", 1099);
    }

    private int port;
    private String hostname;
    private Connection connect = null;
    private Session session = null;
    private PolyInterface inferace = null;
    public Context context = null;

    public PolyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

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

        try {
            Registry r = LocateRegistry.getRegistry(hostname, port);
            System.out.println(r);
            inferace = (PolyInterface) r.lookup("polytweet");
            System.out.println("*** Account creation and login tests ***");
            System.out.println(inferace.createAccount("Skyzy", "polytweet"));
            User u = inferace.login("Skyzy", "polytweet");

            System.out.println("*** Hashtags creation tests ***");
            System.out.println(inferace.createHashtag("baude"));
            System.out.println(inferace.createHashtag("baude"));
            System.out.println(inferace.createHashtag("mosser"));
            MessageConsumer consumer;
            for (Hashtag h : u.getListHashtag()) {
                consumer = session.createConsumer((Topic) context.lookup("dynamicTopics/" + h.getName()));
                consumer.setMessageListener(this);
            }

            for (int i = 0; i < 3; i++) {
                postMessage(u.getPseudo(), "un tweet", "baude");
                Thread.sleep(1000);
            }
            System.out.println("Test follow : " + inferace.followHashtag("baude", "Skyzy"));
            System.out.println(inferace.login("Skyzy", "polytweet"));
        } catch (RemoteException | NotBoundException | NamingException | JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean postMessage(String author, String message, String destinationName) {
        session.c
        try {
            MapMessage newMessage = session.createMapMessage();
            newMessage.setString("Author", author);
            newMessage.setString("Content", message);
            return postMessage(newMessage, destinationName);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean postMessage(Message message, String destinationName) {
        try {
            MessageProducer producer = session.createProducer((Topic) context.lookup("dynamicTopics/" + destinationName));
            producer.send(message);
            System.out.println("message sent.");
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onMessage(Message message) {
        MapMessage mmessage = (MapMessage) message;
        try {
            System.out.println("New tweet :\n\t" + mmessage.getString("Author") + " -> " + mmessage.getString("Content"));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
