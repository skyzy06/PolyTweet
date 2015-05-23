package polytweet.entity;

/**
 *
 * @author Thomas
 */
public class Tweet {

    private final String author;
    private final String content;

    public Tweet(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

}
