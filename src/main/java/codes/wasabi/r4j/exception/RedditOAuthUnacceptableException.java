package codes.wasabi.r4j.exception;

/**
 * Thrown when Reddit could not accept this OAuth request
 */
public class RedditOAuthUnacceptableException extends RedditOAuthException {
    public RedditOAuthUnacceptableException() {
        super();
    }

    public RedditOAuthUnacceptableException(String s) {
        super(s);
    }

    public RedditOAuthUnacceptableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditOAuthUnacceptableException(Throwable cause) {
        super(cause);
    }
}
