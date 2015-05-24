package polytweet.entity;

import java.io.*;

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
