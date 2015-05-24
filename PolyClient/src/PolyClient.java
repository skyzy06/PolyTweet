
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private User user;

    public PolyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        try {	// Create a connection
            this.context = new InitialContext();
            this.context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            this.context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            this.connect = factory.createConnection();
            this.session = this.connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.connect.start(); // on peut activer la connection. 
        } catch (JMSException | NamingException jmse) {
            jmse.printStackTrace();
        }

        try {
            Registry r = LocateRegistry.getRegistry(hostname, port);
            inferace = (PolyInterface) r.lookup("polytweet");
            System.out.println("*** Account creation and login tests ***");
            System.out.println(inferace.createAccount("Skyzy", "polytweet"));
            user = inferace.login("Skyzy", "polytweet");

            System.out.println("*** Hashtags creation tests ***");
            System.out.println(inferace.createHashtag("baude"));
            System.out.println(inferace.createHashtag("baude"));
            System.out.println(inferace.createHashtag("mosser"));
            MessageConsumer consumer;
            for (Hashtag h : user.getListHashtag()) {
                consumer = session.createConsumer((Topic) context.lookup("dynamicTopics/" + h.getName()));
                consumer.setMessageListener(this);
            }

            for (int i = 0; i < 3; i++) {
                postMessage(user.getPseudo(), "un tweet", "baude");
                Thread.sleep(1000);
            }
            System.out.println("Test follow : " + inferace.followHashtag("baude", "Skyzy"));
            System.out.println(inferace.login("Skyzy", "polytweet"));
        } catch (RemoteException | NotBoundException | NamingException | JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean postMessage(String pseudo, String tweetMsg, String hashtag) {
        MapMessage tweet = null;
        System.out.println("test create message of " + pseudo + " with hashtag " + hashtag + " and the content " + tweetMsg);
        try {
            tweet = session.createMapMessage();
            tweet.setString("Author", pseudo);
            tweet.setString("Content", tweetMsg);
            return postMessage(tweet, hashtag);
        } catch (JMSException ex) {
            Logger.getLogger(PolyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postMessage(tweet, hashtag);
    }

    private boolean postMessage(Message tweet, String hashtag) {
        try {
            MessageProducer producer = session.createProducer((Topic) context.lookup("dynamicTopics/" + hashtag));
            producer.send(tweet);
            System.out.println("Tweet posté :)");
            return true;
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(PolyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void onMessage(Message message) {
        MapMessage tweet = (MapMessage) message;
        try {
            System.out.println("Nouveau tweet de @" + tweet.getString("pseudo") + " : " + tweet.getString("tweet"));
        } catch (JMSException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
