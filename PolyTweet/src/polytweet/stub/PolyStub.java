package polytweet.stub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import polytweet.entity.Hashtag;
import polytweet.entity.User;
import polytweet.entity.UserInfo;
import polytweet.interfaces.PolyInterface;

/**
 *
 * @author Thomas
 */
public class PolyStub extends UnicastRemoteObject implements PolyInterface {

    private final UserInfo userInfo;

    public PolyStub(String alias, String firstname, String lastname, String password) throws RemoteException {
        userInfo = new UserInfo(alias, firstname, lastname, password);
    }

    public void post(String msg) {

    }

    @Override
    public boolean createAccount(String pseudo, String firstname, String lastname, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User login(String pseudo, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean createHashtag(String hashtag) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean followHashtag(String hastag) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Hashtag> listMyHashTag() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
