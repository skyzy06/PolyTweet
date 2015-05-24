
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import polytweet.entity.Hashtag;
import polytweet.entity.User;
import polytweet.interfaces.PolyInterface;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class Demo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Demo("localhost", 1099);
    }
    
    private Connection connect = null;
    private Session session = null;
    private PolyInterface inferace = null;
    public Context context = null;
    
    private User user;
    
    public Demo(String hostname, int port) {

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
        
        int command;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Login ***");
        System.out.println("(You account will be created if it doesn't exist)");
        String login, pwd;
        
        try {
            while (user == null) {
                System.out.print("Your pseudo : ");
                login = sc.nextLine();
                System.out.print("Your password : ");
                pwd = sc.nextLine();
                user = inferace.login(login, pwd);
                if (user == null) {
                    System.err.println("Mot de passe incorrect !");
                }
            }
        } catch (RemoteException ex) {
            System.err.println("Impossible de créer/se connecter à votre compte");
        }

        // abonnement au hashtag déjà followé
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
        
        while (true) {
            printMenu();
            command = sc.nextInt();
            switch (command) {
                case 0:
                    System.out.println("Au revoir");
                    System.exit(0);
                    break;
                case 1 :
                    String ht;
                    //clean scanner
                    ht= sc.nextLine();
                    System.out.println("Donnez un nom d'hashtag");
                    ht = sc.nextLine();
                    
                    try{
                        inferace.createHashtag(ht);
                    }
                    catch(RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                case 2 :
                    //clean scanner
                    ht= sc.nextLine();
                    System.out.println("Donnez un nom d'hashtag");
                    ht = sc.nextLine();
                    try
                    {
                        inferace.followHashtag(ht, user.getPseudo());
                    }catch(RemoteException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 3 :
                    try
                    {
                        List<Hashtag> result;
                        result = inferace.listMyHashTag(user.getPseudo());
                        for(Hashtag h : result)
                        {
                            System.out.println(h.getName());
                        }
                    }catch(RemoteException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                
                default:
                    System.out.println("Commande inconnue\n\n");
            }
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
    
    private static void printMenu() {
        String result = "*** PolyTweet Menu ***\n"
                + "0 . Exit\n"
                + "1 . Create a new hashtag (login required)\n"
                + "2 . follow a hashtag (login required)\n"
                + "3 . list my hashtag (login required)\n";
        System.out.println(result);
    }
}
