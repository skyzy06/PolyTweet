
import java.rmi.*;
import java.rmi.registry.*;
import javax.jms.*;
import javax.naming.*;
import polytweet.entity.Hashtag;
import polytweet.entity.User;
import polytweet.interfaces.PolyInterface;

/**
 *
 * @author Thomas
 */
public class PolyClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PolyClient("localhost", 1099);
    }

    private Connection connect = null;
    private Session session = null;
    private PolyInterface inferace = null;
    public Context context = null;

    private User user;

    public PolyClient(String hostname, int port) {

        // creation de la connexion
        try {
            this.context = new InitialContext();
            this.context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            this.context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            this.connect = factory.createConnection();
            this.session = this.connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.connect.start(); // activation de la connection. 
        } catch (JMSException | NamingException e) {
            System.err.println("Erreur d'initialisation de la connexion");
            e.printStackTrace();
        }

        // connexion au RMIregistry
        Registry r = null;
        try {
            r = LocateRegistry.getRegistry(hostname, port);
            inferace = (PolyInterface) r.lookup("polytweet");
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Erreur de connection au RMIregistry");
            e.printStackTrace();
        }

        System.out.println("*** Test de création et de login d'utilisataeur ***");
        try {
            inferace.createAccount("Skyzy", "polytweet");
            user = inferace.login("Skyzy", "polytweet");
        } catch (RemoteException e) {
            System.err.println("Erreur de creation/login de compte.");
            e.printStackTrace();
        }

        System.out.println("*** Test de création des hashtags ***");
        try {
            inferace.createHashtag("baude");
            inferace.createHashtag("mosser");
        } catch (RemoteException e) {
            System.err.println("Erreur de création des hashtags.");
            e.printStackTrace();
        }

        System.out.println("*** Abonnement au(x) hashtag(s) ***");
        MessageConsumer consumer;
        try {
            for (Hashtag h : user.getListHashtag()) {
                consumer = session.createConsumer((Topic) context.lookup("dynamicTopics/" + h.getName()));
                consumer.setMessageListener(user);
            }
        } catch (JMSException | NamingException e) {
            System.err.println("Erreur d'abonnement, veuillez renouveller votre forfait ^^");
            e.printStackTrace();
        }

        System.out.println("*** Post des premiers tweets ***");
        for (int i = 0; i < 3; i++) {
            postTweet(user.getPseudo(), "tweet n°" + i, "baude");
        }

        System.out.println("*** Test de follow ***");
        try {
            inferace.followHashtag("baude", "Skyzy");
        } catch (RemoteException e) {
            System.err.println("Je n'arrive pas à te suive");
            e.printStackTrace();
        }

    }

    private void postTweet(String pseudo, String tweetMsg, String hashtag) {
        MapMessage tweet = null;
        MessageProducer producer = null;
        try {
            tweet = session.createMapMessage();
            tweet.setString("pseudo", pseudo);
            tweet.setString("tweet", tweetMsg);
            producer = session.createProducer((Topic) context.lookup("dynamicTopics/" + hashtag));
            producer.send(tweet);
            System.out.println("Tweet posté :)");
        } catch (JMSException | NamingException e) {
            System.err.println("Le tweet n'a pas pu être posté");
            e.printStackTrace();
        }
    }
}
