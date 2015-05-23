package polytweet.entity;

import java.io.Serializable;

/**
 *
 * @author Thomas
 */
public class Hashtag implements Serializable {

    private String name;

    public Hashtag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
