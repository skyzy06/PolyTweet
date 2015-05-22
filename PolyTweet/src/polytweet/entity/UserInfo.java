package polytweet.entity;

import java.io.Serializable;

/**
 * @author Thomas
 */
public class UserInfo implements Serializable {

    private final String lookupId;
    private String firstName;
    private String lastName;

    public UserInfo(String lookupId, String firstName, String lastName) {
        this.lookupId = lookupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLookupId() {
        return lookupId;
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
