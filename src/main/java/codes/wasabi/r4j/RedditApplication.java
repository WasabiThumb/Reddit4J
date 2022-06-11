package codes.wasabi.r4j;

import codes.wasabi.r4j.enums.Scope;
import codes.wasabi.r4j.exception.RedditOAuthCodeRedemptionException;
import codes.wasabi.r4j.exception.RedditOAuthException;
import codes.wasabi.r4j.oauth.RedditOAuthResponse;
import codes.wasabi.r4j.oauth.RedditOAuthServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Defines a <a href="https://www.reddit.com/prefs/apps">Reddit Application</a>
 */
public class RedditApplication {

    private final String clientID;
    private final String clientSecret;
    private final boolean hasClientSecret;

    /**
     * Creates a new <a href="https://www.reddit.com/prefs/apps">Reddit Application</a> instance
     * @param clientID The client ID of this application
     */
    public RedditApplication(@NotNull String clientID) {
        this.clientID = clientID;
        this.clientSecret = "";
        hasClientSecret = false;
    }

    /**
     * Creates a new <a href="https://www.reddit.com/prefs/apps">Reddit Application</a> instance
     * @param clientID The client ID of this application
     * @param clientSecret The client secret of this application
     */
    public RedditApplication(@NotNull String clientID, @NotNull String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        hasClientSecret = true;
    }

    /**
     * Gets the client ID of this <a href="https://www.reddit.com/prefs/apps">Reddit Application</a> instance
     * @return The client ID
     */
    public final @NotNull String getClientID() {
        return clientID;
    }

    /**
     * Gets the client secret of this <a href="https://www.reddit.com/prefs/apps">Reddit Application</a> instance
     * @return The client secret
     */
    public final @NotNull String getClientSecret() {
        return clientSecret;
    }

    /**
     * Gets whether the client secret of this application has been supplied
     * @return True if the client secret should be considered, false if client secret is an empty string
     */
    public final boolean hasClientSecret() {
        return hasClientSecret;
    }

    /**
     * Creates a client from an existing session with the given bearer token
     * @param bearerToken The bearer token
     * @return A new client
     */
    @Contract("_ -> new")
    public final @NotNull RedditClient createClient(@NotNull String bearerToken) {
        return new RedditClient(this, bearerToken);
    }

    /**
     * Creates a client from an existing session with the given bearer and refresh tokeb
     * @param bearerToken The bearer token
     * @param refreshToken The refresh token
     * @return A new client
     */
    @Contract("_, _ -> new")
    public final @NotNull RedditClient createClient(@NotNull String bearerToken, @NotNull String refreshToken) {
        return new RedditClient(this, bearerToken, refreshToken);
    }

    /**
     * Creates a client with a new session authenticated through an OAuth2 URL
     * @param urlConsumer A consumer that accepts the OAuth2 URL and passes it along to the client
     * @param permanent Whether or not the session should be "permanent"
     * @param scopes A list of OAuth2 scopes to grant to the application
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     * @throws IllegalArgumentException If no scopes are specified
     */
    public final @NotNull CompletableFuture<RedditClient> createClient(@NotNull Consumer<String> urlConsumer, boolean permanent, @NotNull EnumSet<Scope> scopes) throws IllegalArgumentException {
        if (scopes.size() < 1) throw new IllegalArgumentException("Must specify at least 1 scope!");
        RedditOAuthServer oAuthServer = Reddit4J.getOAuthServer();
        String state = UUID.randomUUID().toString();
        StringBuilder scopeString = new StringBuilder();
        for (Scope s : scopes) {
            if (!scopeString.isEmpty()) scopeString.append(" ");
            scopeString.append(s.identifier());
        }
        String port = "8181";
        try {
            port = Objects.requireNonNull(System.getProperty("r4j.oauth.port"));
        } catch (Exception ignored) { };
        String redirectURI = URLEncoder.encode("http://127.0.0.1:" + port + "/", StandardCharsets.UTF_8);
        String url = "https://www.reddit.com/api/v1/authorize?" +
                "client_id=" + URLEncoder.encode(clientID, StandardCharsets.UTF_8) + "&" +
                "response_type=code&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8) + "&" +
                "redirect_uri=" + redirectURI + "&" +
                "duration=" + (permanent ? "permanent" : "temporary") + "&" +
                "scope=" + URLEncoder.encode(scopeString.toString(), StandardCharsets.UTF_8);
        CompletableFuture<RedditClient> ret = new CompletableFuture<>();
        oAuthServer.awaitResponse(state, (RedditOAuthResponse response) -> {
            RedditOAuthException err = response.error();
            if (err != null) {
                ret.completeExceptionally(err);
            } else {
                String code = response.code();
                OutputStream os = null;
                InputStream is = null;
                try {
                    URL u = new URL("https://www.reddit.com/api/v1/access_token/");
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(true);
                    conn.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode((clientID + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("User-Agent", Reddit4J.getUserAgent());
                    conn.connect();
                    String payload = "grant_type=authorization_code&" +
                            "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) + "&" +
                            "redirect_uri=" + redirectURI;
                    os = conn.getOutputStream();
                    os.write(payload.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    is = conn.getInputStream();
                    byte[] bytes = is.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    JsonObject ob = gson.fromJson(s, JsonObject.class);
                    if (ob.has("error")) {
                        ret.completeExceptionally(new RedditOAuthCodeRedemptionException("Endpoint gave error code \"" + ob.get("error").getAsString() + "\""));
                    } else {
                        String accessToken = ob.get("access_token").getAsString();
                        String refreshToken = null;
                        if (ob.has("refresh_token")) {
                            refreshToken = ob.get("refresh_token").getAsString();
                        }
                        ret.complete(new RedditClient(this, accessToken, refreshToken));
                    }
                } catch (Exception e) {
                    ret.completeExceptionally(new RedditOAuthCodeRedemptionException(e));
                } finally {
                    try {
                        if (os != null) os.close();
                        if (is != null) is.close();
                    } catch (IOException ignored) { }
                }
            }
        });
        urlConsumer.accept(url);
        return ret;
    }

    /**
     * Creates a client with a new session authenticated through an OAuth2 URL. All scopes will be granted to this session.
     * @param urlConsumer A consumer that accepts the OAuth2 URL and passes it along to the client
     * @param permanent Whether or not the session should be "permanent"
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     */
    public final @NotNull CompletableFuture<RedditClient> createClient(@NotNull Consumer<String> urlConsumer, boolean permanent) {
        return createClient(urlConsumer, permanent, EnumSet.allOf(Scope.class));
    }

    /**
     * Creates a client with a new permanent session authenticated through an OAuth2 URL. All scopes will be granted to this session.
     * @param urlConsumer A consumer that accepts the OAuth2 URL and passes it along to the client
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     */
    public final @NotNull CompletableFuture<RedditClient> createClient(@NotNull Consumer<String> urlConsumer) {
        return createClient(urlConsumer, true, EnumSet.allOf(Scope.class));
    }

    /**
     * Creates a client with a new session authenticated through an OAuth2 URL. This URL will be printed to console and attempted to open in the local default browser
     * @param permanent Whether or not the session should be "permanent"
     * @param scopes A list of OAuth2 scopes to grant to the application
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     * @throws IllegalArgumentException If no scopes are specified
     */
    public final @NotNull CompletableFuture<RedditClient> createClient(boolean permanent, @NotNull EnumSet<Scope> scopes) throws IllegalArgumentException {
        return createClient((String url) -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ignored) { }
            }
            System.out.println("Open " + url + " in browser to continue with OAuth");
        }, permanent, scopes);
    }

    /**
     * Creates a client with a new session authenticated through an OAuth2 URL, granting all scopes to the session. This URL will be printed to console and attempted to open in the local default browser
     * @param permanent Whether or not the session should be "permanent"
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     * @throws IllegalArgumentException If no scopes are specified
     */
    public final @NotNull CompletableFuture<RedditClient> createClient(boolean permanent) {
        return createClient(permanent, EnumSet.allOf(Scope.class));
    }

    /**
     * Creates a client with a new permanent session authenticated through an OAuth2 URL, granting all scopes to the session. This URL will be printed to console and attempted to open in the local default browser
     * @return A future that resolves when the client has been created, or completes exceptionally when an error has occurred
     */
    public final @NotNull CompletableFuture<RedditClient> createClient() {
        return createClient(true, EnumSet.allOf(Scope.class));
    }

}
