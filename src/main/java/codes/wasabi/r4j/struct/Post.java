package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;

public class Post extends RedditEntity {

    public Post(JsonObject ob) {
        super(ob);
    }

    @Override
    public String getFullname() {
        return getJSON().get("name").getAsString();
    }

    public String getAuthor() {
        return getJSON().get("author").getAsString();
    }

    public String getAuthorFullname() {
        return getJSON().get("author_fullname").getAsString();
    }

    public long getCreatedTime() {
        return getJSON().get("created").getAsLong();
    }

    public String getID() {
        return getJSON().get("id").getAsString();
    }

    public String getPermalink() {
        return getJSON().get("permalink").getAsString();
    }

    public String getTitle() {
        return getJSON().get("title").getAsString();
    }

    public int getUpvotes() {
        return getJSON().get("ups").getAsInt();
    }

    public int getDownvotes() {
        return getJSON().get("downs").getAsInt();
    }

    public int getScore() {
        return getJSON().get("score").getAsInt();
    }

    public float getUpvoteRatio() {
        return getJSON().get("upvote_ratio").getAsFloat();
    }

    public String getSubreddit() {
        return getJSON().get("subreddit").getAsString();
    }

    public String getSubredditFullname() {
        return getJSON().get("subreddit_id").getAsString();
    }

    public int getCommentCount() {
        return getJSON().get("num_comments").getAsInt();
    }

}
