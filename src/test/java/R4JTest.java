import codes.wasabi.r4j.Reddit4J;
import codes.wasabi.r4j.RedditApplication;
import codes.wasabi.r4j.RedditClient;
import codes.wasabi.r4j.enums.Region;
import codes.wasabi.r4j.enums.Scope;
import codes.wasabi.r4j.param.CommentViewOptions;
import codes.wasabi.r4j.param.ListingOptions;
import codes.wasabi.r4j.struct.*;

import java.util.EnumSet;

public class R4JTest {

    public static void main(String[] args) {
        System.out.println("Reddit4J version " + Reddit4J.getVersion());
        System.out.println("Initializing app");
        RedditApplication app = Reddit4J.getApplication();
        System.out.println("Creating client");
        app.createClient(false, EnumSet.of(Scope.IDENTITY, Scope.READ)).whenComplete((RedditClient rc, Throwable t) -> {
            if (t != null) {
                System.out.println("Failed");
                t.printStackTrace();
            } else {
                System.out.println("Created client");
                System.out.println("Bearer token: " + rc.getBearerToken());
                testClient(rc);
            }
            Reddit4J.getOAuthServer().close();
        });
    }

    private static void testClient(RedditClient rc) {
        try {
            System.out.println("Getting identity of client");
            Identity identity = rc.getIdentity();
            // Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println("Fullname: " + identity.getFullname());
            System.out.println("Username: " + identity.getUsername());
            System.out.println("Listing 5 hot posts in r/pics");
            Listing<Post> list = rc.getHot("pics", Region.GLOBAL, ListingOptions.builder().limit(5).build());
            System.out.println("===========================");
            for (int i=0; i < Math.min(list.size(), 5); i++) {
                Post p = list.get(i);
                System.out.println(p.getTitle() + " (by u/" + p.getAuthor() + ")");
                System.out.println(p.getUpvotes() + " upvotes");
                System.out.println("===========================");
            }
            System.out.println("Listing comments for first post");
            Post first = list.get(0);
            Listing<CommentNode> comments = rc.getComments(first, CommentViewOptions.builder().build());
            System.out.println("===========================");
            for (CommentNode cn : comments) {
                if (cn instanceof Comment comment) {
                    System.out.println(comment.getAuthor() + " says");
                    System.out.println(comment.getBody());
                    System.out.println(comment.getUpvotes() + " upvotes");
                } else if (cn instanceof MoreComments more) {
                    System.out.println(more.getCount() + " more");
                }
                System.out.println("===========================");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
