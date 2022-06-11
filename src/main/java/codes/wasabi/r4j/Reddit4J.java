package codes.wasabi.r4j;

import codes.wasabi.r4j.oauth.RedditOAuthServer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Reddit4J {

    private static RedditApplication app = null;
    public static @NotNull RedditApplication getApplication() {
        if (app == null) {
            app = new RedditApplication("Hs2_HHqM0LKizjXjBNMBWQ");
        }
        return app;
    }

    private static void createOAuthServer() {
        int port = 8181;
        try {
            String st = Objects.requireNonNull(System.getProperty("r4j.oauth.port"));
            port = Integer.parseInt(st);
        } catch (Exception ignored) { }
        try {
            server = new RedditOAuthServer(port);
        } catch (Exception e) {
            System.out.println("[R4J] WARN: Could not start R4J OAuth server!");
            e.printStackTrace();
        }
    }

    private static RedditOAuthServer server = null;
    public static @NotNull RedditOAuthServer getOAuthServer() {
        if (server == null) {
            createOAuthServer();
        } else {
            if (!server.isRunning()) {
                server.close();
                createOAuthServer();
            }
        }
        return server;
    }

    public static @NotNull String getVersion() {
        return Objects.requireNonNullElse(Reddit4J.class.getPackage().getImplementationVersion(), "1.0.0");
    }

    public static @NotNull String getUserAgent() {
        return "java:codes.wasabi.r4j:v" + getVersion() + " (by /u/Lavacoal123)";
    }

}
