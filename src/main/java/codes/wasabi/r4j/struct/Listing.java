package codes.wasabi.r4j.struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class Listing<T extends RedditEntity> extends JsonObjectWrapper implements List<T> {

    private final String before;
    private final String after;
    private final int distance;
    private final List<T> conts;
    private final int length;
    public Listing(Class<T> clazz, JsonObject ob) {
        super(ob);
        JsonObject data = ob.get("data").getAsJsonObject();
        String s;
        try {
            s = ob.get("after").getAsString();
        } catch (Exception e) {
            s = null;
        }
        after = s;
        try {
            s = ob.get("before").getAsString();
        } catch (Exception e) {
            s = null;
        }
        before = s;
        int dist;
        try {
            dist = data.get("dist").getAsInt();
        } catch (Exception e) {
            dist = 0;
        }
        distance = dist;
        JsonArray children = data.get("children").getAsJsonArray();
        List<T> list = new ArrayList<>();
        for (JsonElement el : children) {
            JsonObject child = el.getAsJsonObject();
            if (clazz.equals(CommentNode.class)) {
                if (child.get("kind").getAsString().equalsIgnoreCase("more")) {
                    list.add(clazz.cast(new MoreComments(child.get("data").getAsJsonObject())));
                } else {
                    list.add(clazz.cast(new Comment(child.get("data").getAsJsonObject())));
                }
            } else {
                list.add(JsonObjectWrapper.tryInstantiate(clazz, child.get("data").getAsJsonObject()));
            }
        }
        conts = Collections.unmodifiableList(list);
        length = conts.size();
    }

    public @UnmodifiableView @NotNull List<T> getContent() {
        return conts;
    }

    public int getLength() {
        return length;
    }

    public int getDistance() {
        return distance;
    }

    public @Nullable String getBefore() {
        return before;
    }

    public @Nullable String getAfter() {
        return after;
    }

    // List Implementation

    @Override
    public int size() {
        return conts.size();
    }

    @Override
    public boolean isEmpty() {
        return conts.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return conts.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return conts.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return conts.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] t1s) {
        return conts.toArray(t1s);
    }

    @Override
    public boolean add(T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot add elements!");
    }

    @Override
    public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot remove elements!");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return conts.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot add elements!");
    }

    @Override
    public boolean addAll(int i, @NotNull Collection<? extends T> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot add elements!");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot remove elements!");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot remove elements!");
    }

    @Override
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot remove elements!");
    }

    @Override
    public T get(int i) {
        return conts.get(i);
    }

    @Override
    public T set(int i, T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot set elements!");
    }

    @Override
    public void add(int i, T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot add elements!");
    }

    @Override
    public T remove(int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Listing is read-only, cannot remove elements!");
    }

    @Override
    public int indexOf(Object o) {
        return conts.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return conts.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return conts.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int i) {
        return conts.listIterator(i);
    }

    @NotNull
    @Override
    public List<T> subList(int i, int i1) {
        return conts.subList(i, i1);
    }
}
