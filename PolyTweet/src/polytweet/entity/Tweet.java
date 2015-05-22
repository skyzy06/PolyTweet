package polytweet.entity;

import java.util.Date;

/**
 *
 * @author Thomas
 */
public class Tweet {

    private final UserInfo author;
    private String tweet;
    private final Date creationTime;

    public Tweet(UserInfo author, String tweet) {
        this.author = author;
        this.tweet = tweet;
        this.creationTime = new Date();
    }

    public UserInfo getAuthor() {
        return author;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String msg) {
        this.tweet = msg;
    }

    public Date getCreationTime() {
        return creationTime;
    }

}
