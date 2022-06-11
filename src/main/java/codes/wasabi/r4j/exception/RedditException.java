package codes.wasabi.r4j.exception;

/**
 * Superclass of all RedditExceptions
 */
public class RedditException extends IllegalStateException {
    public RedditException() {
        super();
    }

    public RedditException(String s) {
        super(s);
    }

    public RedditException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditException(Throwable cause) {
        super(cause);
    }
}
