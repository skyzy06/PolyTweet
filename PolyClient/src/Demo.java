
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
import java.util.logging.Level;
import java.util.logging.Logger;

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

        System.out.println("Bienvenue sur Polytweet " + user.getPseudo() + "!");

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
            String hashtagName;
            switch (command) {
                case 0:
                    System.out.println("Au revoir");
                    System.exit(0);
                    break;
                case 1:
                    //creer un hashtag
                    hashtagName = sc.nextLine();
                    System.out.println("Donnez un nom d'hashtag");
                    hashtagName = sc.nextLine();

                    try {
                        inferace.createHashtag(hashtagName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    //suivre un hashtag
                    hashtagName = sc.nextLine();
                    System.out.println("Donnez un nom d'hashtag :");
                    hashtagName = sc.nextLine();
                    try {
                        inferace.followHashtag(hashtagName, user.getPseudo());
                        consumer = session.createConsumer((Topic) context.lookup("dynamicTopics/" + hashtagName));
                        consumer.setMessageListener(user);
                    } catch (JMSException | NamingException | RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    //lister les hashtags perso
                    try {
                        System.out.println("Les hashtags que vous suivez :");
                        List<Hashtag> result;
                        result = inferace.listMyHashTag(user.getPseudo());
                        for (Hashtag h : result) {
                            System.out.println(h.getName());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    //poster un tweet
                    String tweet;
                    hashtagName = sc.nextLine();
                    System.out.println("Donnez un nom d'hashtag :");
                    hashtagName = sc.nextLine();

                    System.out.println("Ecrivez votre tweet :");
                    tweet = sc.nextLine();
                    postTweet(user.getPseudo(), tweet, hashtagName);
                    break;
                default:
                    System.out.println("Commande inconnue\n\n");
            }
        }
    }

    private void postTweet(String pseudo, String tweetMsg, String hashtag) {
        MapMessage tweet = null;
        MessageProducer producer = null;

        boolean isFollowTheHashtag = false;
        try {
            for (Hashtag tag : inferace.listMyHashTag(pseudo)) {
                if (tag.getName().equals(hashtag)) {
                    isFollowTheHashtag = true;
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Demo.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!isFollowTheHashtag) {
            System.err.println("Vous ne suivez pas ce hashtag !");
            return;
        }

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
                + "3 . list my hashtag (login required)\n"
                + "4 . post a tweet (login required)\n"
                + "*********************\n";
        System.out.println(result);
    }
}
