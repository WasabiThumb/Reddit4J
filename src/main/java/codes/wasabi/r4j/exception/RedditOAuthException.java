package codes.wasabi.r4j.exception;

/**
 * Superclass of all RedditOAuthExceptions
 */
public class RedditOAuthException extends RedditException {
    public RedditOAuthException() {
        super();
    }

    public RedditOAuthException(String s) {
        super(s);
    }

    public RedditOAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditOAuthException(Throwable cause) {
        super(cause);
    }
}
