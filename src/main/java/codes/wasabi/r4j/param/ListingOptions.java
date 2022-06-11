package codes.wasabi.r4j.param;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

public record ListingOptions(@Nullable String after, @Nullable String before, @Range(from=0L, to=Integer.MAX_VALUE) int count, @Range(from=1L, to=100L) int limit, boolean showAll) {

    public static class Builder {
        private String after = null;
        private String before = null;
        private int count = 0;
        private int limit = 100;
        private boolean showAll = false;

        @Contract(value = "_ -> this", mutates = "this")
        public Builder after(@Nullable String after) {
            this.after = after;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public Builder before(@Nullable String before) {
            this.before = before;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public Builder count(@Range(from=0L, to=Integer.MAX_VALUE) int count) {
            this.count = count;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public Builder limit(@Range(from=1L, to=100L) int limit) {
            this.limit = limit;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public Builder showAll(boolean showAll) {
            this.showAll = showAll;
            return this;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull ListingOptions build() {
            return new ListingOptions(after, before, count, limit, showAll);
        }
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Contract(" -> new")
    public @NotNull Map<String, String> toHashMap() {
        Map<String, String> ret = new HashMap<>();
        if (after != null) ret.put("after", after);
        if (before != null) ret.put("before", before);
        ret.put("count", String.valueOf(count));
        ret.put("limit", String.valueOf(limit));
        if (showAll) ret.put("show", "all");
        return ret;
    }

}
