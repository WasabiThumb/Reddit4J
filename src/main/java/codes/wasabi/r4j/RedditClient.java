package codes.wasabi.r4j;

import codes.wasabi.r4j.enums.Region;
import codes.wasabi.r4j.enums.SortType;
import codes.wasabi.r4j.enums.Theme;
import codes.wasabi.r4j.enums.TimePeriod;
import codes.wasabi.r4j.param.CommentViewOptions;
import codes.wasabi.r4j.param.ListingOptions;
import codes.wasabi.r4j.struct.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RedditClient {

    private final RedditApplication app;
    private String bearerToken;
    private final String refreshToken;
    private final boolean hasRefreshToken;
    private long refreshTime;
    protected RedditClient(RedditApplication app, String bearerToken, String refreshToken) {
        this.app = app;
        this.bearerToken = bearerToken;
        this.refreshToken = refreshToken;
        this.hasRefreshToken = refreshToken != null;
        this.refreshTime = System.currentTimeMillis() + 3300000L;
    }

    protected RedditClient(RedditApplication app, String bearerToken) {
        this(app, bearerToken, null);
    }

    /**
     * Get the application that spawned this client
     * @return The application
     */
    public @NotNull RedditApplication getApplication() {
        return app;
    }

    /**
     * Gets the one-time bearer token for this client's session
     * @return The bearer token
     */
    public @NotNull String getBearerToken() {
        return bearerToken;
    }

    /**
     * Gets the refresh token for this client's session, or null if none exists (the client session is not permanent)
     * @return The refresh token or null
     */
    public @Nullable String getRefreshToken() {
        return bearerToken;
    }

    /**
     * Returns true if the refresh token is present
     * @return True if the refresh token is present
     */
    public boolean hasRefreshToken() {
        return hasRefreshToken;
    }

    /**
     * Alias for hasRefreshToken()
     * @see #hasRefreshToken()
     * @return True if the refresh token is present (the client session is permanent)
     */
    public boolean isPermanent() {
        return hasRefreshToken;
    }

    private WeakReference<Identity> identityWeakReference = new WeakReference<>(null);
    /**
     * Gets the identity of the client. Requires the identity scope
     * @return The identity of the client
     */
    public Identity getIdentity() throws IOException {
        Identity ret = identityWeakReference.get();
        if (ret == null) {
            JsonObject ob = requestJSON("GET", "/api/v1/me", JsonObject.class);
            ret = new Identity(ob);
            identityWeakReference = new WeakReference<>(ret);
        }
        return ret;
    }

    /**
     * Lists the hot posts for a subreddit
     * @param subreddit Subreddit name
     * @param region The region to search within
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getHot(String subreddit, Region region, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        params.put("g", region.name());
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/hot", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    /**
     * Lists the hot posts for a subreddit
     * @param subreddit Subreddit name
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getHot(String subreddit, ListingOptions options) throws IOException {
        return getHot(subreddit, Region.GLOBAL, options);
    }

    /**
     * Lists the best posts for a subreddit
     * @param subreddit Subreddit name
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getBest(String subreddit, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/best", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    /**
     * Lists the top posts for a subreddit
     * @param subreddit Subreddit name
     * @param period The time period to search over
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getTop(String subreddit, TimePeriod period, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        params.put("t", period.name().toLowerCase());
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/top", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    /**
     * Lists the controversial posts for a subreddit
     * @param subreddit Subreddit name
     * @param period The time period to search over
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getControversial(String subreddit, TimePeriod period, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        params.put("t", period.name().toLowerCase());
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/controversial", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    /**
     * Lists the new posts for a subreddit
     * @param subreddit Subreddit name
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getNew(String subreddit, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/new", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    /**
     * Lists the rising posts for a subreddit
     * @param subreddit Subreddit name
     * @param options The options for this listing
     * @return The listing
     */
    public Listing<Post> getRising(String subreddit, ListingOptions options) throws IOException {
        Map<String, String> params = options.toHashMap();
        JsonObject ob = requestJSON("GET", "/r/" + subreddit + "/rising", params, JsonObject.class);
        return new Listing<>(Post.class, ob);
    }

    public Listing<CommentNode> getComments(String subreddit, String postID, @Nullable String parentCommentID, int context, int depth, int limit, boolean showedits, boolean showmedia, boolean showmore, boolean showtitle, SortType sort, Theme theme) throws IOException {
        Map<String, String> params = new HashMap<>(Map.of(
                "context", String.valueOf(context),
                "depth", String.valueOf(depth),
                "limit", String.valueOf(limit),
                "showedits", String.valueOf(showedits),
                "showmedia", String.valueOf(showmedia),
                "showmore", String.valueOf(showmore),
                "showtitle", String.valueOf(showtitle),
                "sort", sort.name().toLowerCase(),
                "theme", theme.name().toLowerCase()
        ));
        if (parentCommentID != null) params.put("comment", parentCommentID);
        JsonArray arr = requestJSON("GET", "/r/" + subreddit + "/comments/" + postID, params, JsonArray.class);
        return new Listing<>(CommentNode.class, arr.get(1).getAsJsonObject());
    }

    public Listing<CommentNode> getComments(String subreddit, String postID, int context, int depth, int limit, boolean showedits, boolean showmedia, boolean showmore, boolean showtitle, SortType sort, Theme theme) throws IOException {
        return getComments(subreddit, postID, null, context, depth, limit, showedits, showmedia, showmore, showtitle, sort, theme);
    }

    public Listing<CommentNode> getComments(String subreddit, String postID, @Nullable String parentCommentID, CommentViewOptions opts) throws IOException {
        return getComments(
                subreddit,
                postID,
                parentCommentID,
                opts.context(),
                opts.depth(),
                opts.limit(),
                opts.showedits(),
                opts.showmedia(),
                opts.showmore(),
                opts.showtitle(),
                opts.sort(),
                opts.theme()
        );
    }

    public Listing<CommentNode> getComments(String subreddit, String postID, CommentViewOptions opts) throws IOException {
        return getComments(subreddit, postID, null, opts);
    }

    public Listing<CommentNode> getComments(Post post, @Nullable String parentCommentID, CommentViewOptions opts) throws IOException {
        return getComments(
                post.getSubreddit(),
                post.getID(),
                parentCommentID,
                opts.context(),
                opts.depth(),
                opts.limit(),
                opts.showedits(),
                opts.showmedia(),
                opts.showmore(),
                opts.showtitle(),
                opts.sort(),
                opts.theme()
        );
    }

    public Listing<CommentNode> getComments(Post post, CommentViewOptions opts) throws IOException {
        return getComments(post, null, opts);
    }

    /**
     * Refreshes the client's bearer token. This is necessary because bearer tokens only last about 1 hour. This requires this session to have a refresh token, which it should if
     * it was created as a "permanent" session. This is also called by default when necessary, however it should also be called manually when loading a previously suspended session.
     * @throws IOException Failed to refresh
     * @throws IllegalStateException Cannot refresh, no refresh token
     * @see RedditClient#hasRefreshToken()
     * @see RedditClient#isPermanent()
     */
    public void refresh() throws IOException, IllegalStateException {
        if (!hasRefreshToken()) throw new IllegalStateException("Cannot refresh without a refresh token!");
        InputStream is = null;
        OutputStream os = null;
        try {
            String clientSecret = (app.hasClientSecret() ? app.getClientSecret() : "");
            String basicAuth = new String(Base64.getEncoder().encode((app.getClientID() + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            URL url = new URL("https://www.reddit.com/api/v1/access_token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("User-Agent", Reddit4J.getUserAgent());
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            conn.connect();
            String payload = "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
            os = conn.getOutputStream();
            os.write(payload.getBytes(StandardCharsets.UTF_8));
            os.flush();
            is = conn.getInputStream();
            byte[] resp = is.readAllBytes();
            String respString = new String(resp, StandardCharsets.UTF_8);
            if (gson == null) gson = new Gson();
            JsonObject ob = gson.fromJson(respString, JsonObject.class);
            String newAccessToken = ob.get("access_token").getAsString();
            long expireTime = ob.get("expires_in").getAsLong();
            this.bearerToken = newAccessToken;
            this.refreshTime = System.currentTimeMillis() + (expireTime * 1000L);
        } catch (IOException e) {
            if (is != null) is.close();
            if (os != null) os.close();
            throw e;
        }
    }

    protected byte[] request(String method, String endpoint, Map<String, String> params) throws IOException {
        if (hasRefreshToken()) {
            if (System.currentTimeMillis() >= refreshTime) {
                refresh();
            }
        }
        StringBuilder payload = new StringBuilder("raw_json=1");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            payload.append("&");
            payload.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            payload.append("=");
            payload.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
        }
        InputStream is = null;
        byte[] ret;
        try {
            URL url = new URL("https://oauth.reddit.com" + endpoint + "?" + payload);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("User-Agent", Reddit4J.getUserAgent());
            conn.connect();
            is = conn.getInputStream();
            ret = is.readAllBytes();
        } catch (IOException e) {
            if (is != null) is.close();
            throw e;
        }
        return ret;
    }

    protected byte[] request(String method, String endpoint) throws IOException {
        return request(method, endpoint, Collections.emptyMap());
    }

    protected String requestString(String method, String endpoint, Map<String, String> params) throws IOException {
        byte[] bytes = request(method, endpoint, params);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected String requestString(String method, String endpoint) throws IOException {
        return requestString(method, endpoint, Collections.emptyMap());
    }

    private Gson gson = null;
    protected JsonElement requestJSON(String method, String endpoint, Map<String, String> params) throws IOException {
        String string = requestString(method, endpoint, params);
        if (gson == null) gson = new Gson();
        return gson.fromJson(string, JsonElement.class);
    }

    protected JsonElement requestJSON(String method, String endpoint) throws IOException {
        return requestJSON(method, endpoint, Collections.emptyMap());
    }

    protected <T> T requestJSON(String method, String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        String string = requestString(method, endpoint, params);
        if (gson == null) gson = new Gson();
        return gson.fromJson(string, clazz);
    }

    protected <T> T requestJSON(String method, String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON(method, endpoint, Collections.emptyMap(), clazz);
    }

    // Expose low-level methods

    /**
     * Sends a GET request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T get(String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        return requestJSON("GET", endpoint, params, clazz);
    }

    /**
     * Sends a GET request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement get(String endpoint, Map<String, String> params) throws IOException {
        return requestJSON("GET", endpoint, params, JsonElement.class);
    }

    /**
     * Sends a GET request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement get(String endpoint) throws IOException {
        return requestJSON("GET", endpoint, Collections.emptyMap(), JsonElement.class);
    }

    /**
     * Sends a GET request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T get(String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON("GET", endpoint, Collections.emptyMap(), clazz);
    }

    /**
     * Sends a POST request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T post(String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        return requestJSON("POST", endpoint, params, clazz);
    }

    /**
     * Sends a POST request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement post(String endpoint, Map<String, String> params) throws IOException {
        return requestJSON("POST", endpoint, params, JsonElement.class);
    }

    /**
     * Sends a POST request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement post(String endpoint) throws IOException {
        return requestJSON("POST", endpoint, Collections.emptyMap(), JsonElement.class);
    }

    /**
     * Sends a POST request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T post(String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON("POST", endpoint, Collections.emptyMap(), clazz);
    }

    /**
     * Sends a PATCH request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T patch(String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        return requestJSON("PATCH", endpoint, params, clazz);
    }

    /**
     * Sends a PATCH request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement patch(String endpoint, Map<String, String> params) throws IOException {
        return requestJSON("PATCH", endpoint, params, JsonElement.class);
    }

    /**
     * Sends a PATCH request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement patch(String endpoint) throws IOException {
        return requestJSON("PATCH", endpoint, Collections.emptyMap(), JsonElement.class);
    }

    /**
     * Sends a PATCH request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T patch(String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON("PATCH", endpoint, Collections.emptyMap(), clazz);
    }

    /**
     * Sends a PUT request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T put(String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        return requestJSON("PUT", endpoint, params, clazz);
    }

    /**
     * Sends a PUT request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement put(String endpoint, Map<String, String> params) throws IOException {
        return requestJSON("PUT", endpoint, params, JsonElement.class);
    }

    /**
     * Sends a PUT request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement put(String endpoint) throws IOException {
        return requestJSON("PUT", endpoint, Collections.emptyMap(), JsonElement.class);
    }

    /**
     * Sends a PUT request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T put(String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON("PUT", endpoint, Collections.emptyMap(), clazz);
    }

    /**
     * Sends a DELETE request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T delete(String endpoint, Map<String, String> params, Class<? extends T> clazz) throws IOException {
        return requestJSON("DELETE", endpoint, params, clazz);
    }

    /**
     * Sends a DELETE request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param params A map of URI-encoded parameters to send.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement delete(String endpoint, Map<String, String> params) throws IOException {
        return requestJSON("DELETE", endpoint, params, JsonElement.class);
    }

    /**
     * Sends a DELETE request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public @NotNull JsonElement delete(String endpoint) throws IOException {
        return requestJSON("DELETE", endpoint, Collections.emptyMap(), JsonElement.class);
    }

    /**
     * Sends a DELETE request to the specified endpoint and returns the result
     * @param endpoint The endpoint (e.g. /api/v1/me). See all endpoints <a href="https://www.reddit.com/dev/api/">here</a>.
     * @param clazz The class of the data structure to parse the body with (e.g. JsonObject.class).
     * @return The JSON body
     * @throws IOException Could not complete this request
     */
    public <T> @NotNull T delete(String endpoint, Class<? extends T> clazz) throws IOException {
        return requestJSON("DELETE", endpoint, Collections.emptyMap(), clazz);
    }

}
