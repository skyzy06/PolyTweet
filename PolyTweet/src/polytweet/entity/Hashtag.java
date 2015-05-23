package polytweet.entity;

import java.io.Serializable;

/**
 *
 * @author Thomas
 */
public class Hashtag implements Serializable {

    private final String name;

    public Hashtag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
