package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Expresses a "node" in a comment tree, which can either be MoreComments (signifying a list of more comments that you may load), or a Comment
 */
public abstract class CommentNode extends RedditEntity {

    public CommentNode(JsonObject ob) {
        super(ob);
    }

    public final int getDepth() {
        return getJSON().get("depth").getAsInt();
    }

    @Override
    public String getFullname() {
        return getJSON().get("name").getAsString();
    }

    public final String getID() {
        return getJSON().get("id").getAsString();
    }

    public final String getParentFullname() {
        return getJSON().get("parent_id").getAsString();
    }

    @Contract(" -> this")
    public final @NotNull Comment asComment() throws ClassCastException {
        return (Comment) this;
    }

    public final @Nullable Comment asCommentOrNull() {
        try {
            return asComment();
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Contract(" -> this")
    public final @NotNull MoreComments asMore() throws ClassCastException {
        return (MoreComments) this;
    }

    public final @Nullable MoreComments asMoreOrNull() {
        try {
            return asMore();
        } catch (ClassCastException e) {
            return null;
        }
    }

}
