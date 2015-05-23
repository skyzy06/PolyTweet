package polytweet.stub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import polytweet.entity.Hashtag;
import polytweet.entity.User;
import polytweet.interfaces.PolyInterface;

/**
 *
 * @author Thomas
 */
public class PolyStub extends UnicastRemoteObject implements PolyInterface {

    private List<Hashtag> allhashtags;
    private List<User> allUsers;
    private Context context;
    private Connection connection;
    private Session session;

    public PolyStub() throws RemoteException {
        allhashtags = new ArrayList<Hashtag>();
        allUsers = new ArrayList<User>();
        try {
            context = new InitialContext();
            context.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            context.addToEnvironment(Context.PROVIDER_URL, "tcp://localhost:61616");

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createAccount(String pseudo, String password) throws RemoteException {
        if (!userAlreadyExist(pseudo)) {
            allUsers.add(new User(pseudo, password));
            return true;
        }
        return false;
    }

    @Override
    public User login(String pseudo, String password) throws RemoteException {
        for (User user : allUsers) {
            if (user.getPseudo().equals(pseudo) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean createHashtag(String hashtag) throws RemoteException {
        if (!hashtagAlreadyExist(hashtag)) {
            allhashtags.add(new Hashtag(hashtag));
            return true;
        }
        return false;
    }

    @Override
    public boolean followHashtag(String hastag, String pseudo) throws RemoteException {
        if (hashtagAlreadyExist(hastag) && userAlreadyExist(pseudo)) {
            Hashtag tag = findHashtagByName(hastag);
            User user = findUserByPseudo(pseudo);
            user.getListHashtag().add(tag);
            return true;
        }
        return false;
    }

    @Override
    public List<Hashtag> listMyHashTag(String pseudo) throws RemoteException {
        if (userAlreadyExist(pseudo)) {
            User user = findUserByPseudo(pseudo);
            return user.getListHashtag();
        }
        return null;
    }

    private boolean userAlreadyExist(String pseudo) {
        return findUserByPseudo(pseudo) != null;
    }

    private User findUserByPseudo(String pseudo) {
        User user = null;
        for (User tmp : allUsers) {
            if (tmp.getPseudo().equals(pseudo)) {
                user = tmp;
            }
        }
        return user;
    }

    private boolean hashtagAlreadyExist(String tag) {
        return findHashtagByName(tag) != null;
    }

    private Hashtag findHashtagByName(String tag) {
        Hashtag hash = null;
        for (Hashtag tmp : allhashtags) {
            if (tmp.getName().equals(tag)) {
                hash = tmp;
            }
        }
        return hash;
    }
}
