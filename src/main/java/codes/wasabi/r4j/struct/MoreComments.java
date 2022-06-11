package codes.wasabi.r4j.struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MoreComments extends CommentNode {

    public MoreComments(JsonObject ob) {
        super(ob);
    }

    public String[] getChildren() {
        JsonArray arr = getJSON().get("children").getAsJsonArray();
        int size = arr.size();
        String[] ret = new String[size];
        for (int i=0; i < size; i++) {
            ret[i] = arr.get(i).getAsString();
        }
        return ret;
    }

    public int getCount() {
        return getJSON().get("count").getAsInt();
    }

}
