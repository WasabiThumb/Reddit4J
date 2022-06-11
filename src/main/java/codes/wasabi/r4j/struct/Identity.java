package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;

public class Identity extends RedditEntity {

    public Identity(JsonObject json) {
        super(json);
    }

    public final String getUsername() {
        return getJSON().get("name").toString();
    }

    public final double getCreatedTime() {
        return getJSON().get("created").getAsDouble();
    }

    public final String getID() {
        return getJSON().get("id").getAsString();
    }

    @Override
    public final String getFullname() {
        return "t2_" + getID();
    }

    public final boolean isOver18() {
        return getJSON().get("over_18").getAsBoolean();
    }

}
