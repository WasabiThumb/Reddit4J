package codes.wasabi.r4j.exception;

/**
 * Thrown when the client has rejected the OAuth request
 */
public class RedditOAuthDeniedException extends RedditOAuthException {
    public RedditOAuthDeniedException() {
        super();
    }

    public RedditOAuthDeniedException(String s) {
        super(s);
    }

    public RedditOAuthDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditOAuthDeniedException(Throwable cause) {
        super(cause);
    }
}
