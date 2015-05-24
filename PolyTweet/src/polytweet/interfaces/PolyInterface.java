package polytweet.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import polytweet.entity.Hashtag;
import polytweet.entity.User;

/**
 *
 * @author Thomas
 */
public interface PolyInterface extends Remote {

    public void createAccount(String pseudo, String password) throws RemoteException;

    public User login(String pseudo, String password) throws RemoteException;

    public void createHashtag(String hashtag) throws RemoteException;

    public void followHashtag(String hastag, String pseudo) throws RemoteException;

    public List<Hashtag> listMyHashTag(String pseudo) throws RemoteException;
}
