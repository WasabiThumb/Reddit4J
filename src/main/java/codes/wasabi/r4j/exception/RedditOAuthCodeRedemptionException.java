package codes.wasabi.r4j.exception;

/**
 * Thrown when the OAuth code could not be redeemed for an unknown reason
 */
public class RedditOAuthCodeRedemptionException extends RedditOAuthException {
    public RedditOAuthCodeRedemptionException() {
        super();
    }

    public RedditOAuthCodeRedemptionException(String s) {
        super(s);
    }

    public RedditOAuthCodeRedemptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditOAuthCodeRedemptionException(Throwable cause) {
        super(cause);
    }
}
