package polytweet.stub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import polytweet.entity.UserInfo;
import polytweet.interfaces.PolyInterface;

/**
 *
 * @author Thomas
 */
public class PolyStub extends UnicastRemoteObject implements PolyInterface {

    private final UserInfo userInfo;

    public PolyStub(String alias, String firstname, String lastname) throws RemoteException {
        userInfo = new UserInfo(alias, firstname, lastname);
    }

    public void post(String msg) {

    }
}
