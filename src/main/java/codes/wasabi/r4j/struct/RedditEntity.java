package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;

public abstract class RedditEntity extends JsonObjectWrapper implements Fullnamed {
    public RedditEntity(JsonObject ob) {
        super(ob);
    }
}
