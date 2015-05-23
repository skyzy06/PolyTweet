package polytweet.entity;

import java.io.Serializable;

/**
 * @author Thomas
 */
public class UserInfo implements Serializable {

    private final String pseudo;
    private final String firstName;
    private final String lastName;
    private final String password;

    public UserInfo(String pseudo, String firstName, String lastName, String password) {
        this.pseudo = pseudo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
