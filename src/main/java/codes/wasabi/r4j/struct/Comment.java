package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;

public class Comment extends CommentNode {

    public Comment(JsonObject ob) {
        super(ob);
    }

    public String getAuthor() {
        return getJSON().get("author").getAsString();
    }

    public String getAuthorFullname() {
        return getJSON().get("author_fullname").getAsString();
    }

    public String getBody() {
        return getJSON().get("body").getAsString();
    }

    public String getBodyHTML() {
        return getJSON().get("body_html").getAsString();
    }

    public long getCreatedTime() {
        return getJSON().get("created").getAsLong();
    }

    public float getControversiality() {
        return getJSON().get("controversiality").getAsFloat();
    }

    public int getDownvotes() {
        return getJSON().get("downs").getAsInt();
    }

    public int getUpvotes() {
        return getJSON().get("ups").getAsInt();
    }

    public String getPermalink() {
        return getJSON().get("permalink").getAsString();
    }

    public Listing<CommentNode> getReplies() {
        return new Listing<>(CommentNode.class, getJSON().get("replies").getAsJsonObject());
    }

    public int getScore() {
        return getJSON().get("score").getAsInt();
    }

    public String getSubreddit() {
        return getJSON().get("subreddit").getAsString();
    }

    public String getSubredditID() {
        return getJSON().get("subreddit_id").getAsString();
    }

}
