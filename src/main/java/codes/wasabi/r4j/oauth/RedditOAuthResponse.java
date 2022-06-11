package codes.wasabi.r4j.oauth;

import codes.wasabi.r4j.exception.RedditOAuthException;

public record RedditOAuthResponse(String code, RedditOAuthException error) {
}
