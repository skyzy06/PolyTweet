package polytweet.entity;

import java.io.Serializable;

/**
 * @author Thomas
 */
public class UserInfo implements Serializable {

    private String alias;
    private String firstName;
    private String lastName;

    public UserInfo(String alias, String firstName, String lastName) {
        this.alias = alias;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
