package codes.wasabi.r4j.struct;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

public abstract class JsonObjectWrapper {

    public static <T extends JsonObjectWrapper> @NotNull T tryInstantiate(Class<T> clazz, JsonObject ob) throws IllegalStateException {
         try {
             Constructor<T> con = clazz.getConstructor(JsonObject.class);
             return con.newInstance(ob);
         } catch (Exception e) {
             throw new IllegalStateException(e);
         }
    }

    private final JsonObject ob;
    public JsonObjectWrapper(JsonObject ob) {
        this.ob = ob;
    }

    public final JsonObject getJSON() {
        return ob;
    }

}
