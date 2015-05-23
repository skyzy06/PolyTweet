/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polytweet.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Thomas
 */
public class User implements Serializable, MessageListener {

    private UserInfo userInfo = null;

    private List<Hashtag> listHashtag = null;

    public User(String pseudo, String firstname, String lastname, String password) {
        this.userInfo = new UserInfo(pseudo, firstname, lastname, password);
        this.listHashtag = new ArrayList<>();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public List<Hashtag> getListHashtag() {
        return listHashtag;
    }

    public boolean isFollowing(String hashtag) {
        boolean result = false;
        for (Hashtag h : listHashtag) {
            if (h.getName().equals(hashtag)) {
                result = true;
            }
        }
        return result;
    }

    public void followNewHashtag(Hashtag hashtag) {
        if (isFollowing(hashtag.getName())) {
            return;
        }
        listHashtag.add(hashtag);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof Tweet) {
            Tweet tweet = (Tweet) message;
            UserInfo author = tweet.getAuthor();
            System.out.print("Nouveau tweet de ");
            System.out.print(author.getFirstName() + " " + author.getLastName() + " (@" + author.getPseudo() + ") : ");
            System.out.println(tweet.getTweet());
        } else {
            System.err.println("Ceci n'est pas un tweet !");
        }
    }

}
