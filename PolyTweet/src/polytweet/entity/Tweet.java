package polytweet.entity;

import java.util.Date;

/**
 *
 * @author Thomas
 */
public class Tweet {

    private final UserInfo author;
    private String msg;
    private final Date creationTime;

    public Tweet(UserInfo author, String msg) {
        this.author = author;
        this.msg = msg;
        this.creationTime = new Date();
    }

    public UserInfo getAuthor() {
        return author;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}
