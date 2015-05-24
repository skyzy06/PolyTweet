/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polytweet.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Thomas
 */
public class User implements Serializable, MessageListener {

    private String pseudo = "";

    private String password = "";

    private List<Hashtag> listHashtag = null;

    public User(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
        this.listHashtag = new ArrayList<>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }

    public List<Hashtag> getListHashtag() {
        return listHashtag;
    }

    public boolean isFollowing(String hashtag) {
        return listHashtag.stream().anyMatch((h) -> (h.getName().equals(hashtag)));
    }

    public void followNewHashtag(Hashtag hashtag) {
        if (isFollowing(hashtag.getName())) {
            return;
        }
        listHashtag.add(hashtag);
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
