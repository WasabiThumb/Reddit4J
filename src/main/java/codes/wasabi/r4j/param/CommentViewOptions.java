package codes.wasabi.r4j.param;

import codes.wasabi.r4j.enums.SortType;
import codes.wasabi.r4j.enums.Theme;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record CommentViewOptions(int context, int depth, int limit, boolean showedits, boolean showmedia, boolean showmore, boolean showtitle, SortType sort, Theme theme) {

    public static class Builder {
        private int context = 0;
        private int depth = 4;
        private int limit = 100;
        private boolean showedits = false;
        private boolean showmedia = false;
        private boolean showmore = false;
        private boolean showtitle = false;
        private SortType sort = SortType.TOP;
        private Theme theme = Theme.DEFAULT;

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder context(int context) {
            this.context = context;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder depth(int depth) {
            this.depth = depth;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder showEdits(boolean showedits) {
            this.showedits = showedits;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder showMedia(boolean showmedia) {
            this.showmedia = showmedia;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder showMore(boolean showmore) {
            this.showmore = showmore;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder showTitle(boolean showtitle) {
            this.showtitle = showtitle;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder sort(SortType sort) {
            this.sort = sort;
            return this;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public @NotNull Builder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        @Contract(" -> !null")
        public @NotNull CommentViewOptions build() {
            return new CommentViewOptions(context, depth, limit, showedits, showmedia, showmore, showtitle, sort, theme);
        }
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

}
