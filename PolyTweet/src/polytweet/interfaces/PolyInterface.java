/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polytweet.interfaces;

import java.io.Serializable;
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

    public boolean createAccount(String pseudo, String firstname, String lastname, String password) throws RemoteException;

    public User login(String pseudo, String password) throws RemoteException;

    public boolean createHashtag(String hashtag) throws RemoteException;

    public boolean followHashtag(String hastag) throws RemoteException;

    public List<Hashtag> listMyHashTag() throws RemoteException;
}
