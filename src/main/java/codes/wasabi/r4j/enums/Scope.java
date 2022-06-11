package codes.wasabi.r4j.enums;

import org.jetbrains.annotations.NotNull;

/**
 * A list of OAuth scopes according to <a href="https://www.reddit.com/api/v1/scopes">https://www.reddit.com/api/v1/scopes</a> as of 6/5/22
 */
public enum Scope {
    /**
     * Spend my reddit gold creddits on giving gold to other users.
     */
    CREDITS("creddits"),

    /**
     * Access mod notes for subreddits I moderate.
     */
    MOD_NOTE("modnote"),

    /**
     * Add/remove users to approved user lists and ban/unban or mute/unmute users from subreddits I moderate.
     */
    MOD_CONTRIBUTORS("modcontributors"),

    /**
     * Access and manage modmail via mod.reddit.com.
     */
    MOD_MAIL("modmail"),

    /**
     * Manage the configuration, sidebar, and CSS of subreddits I moderate.
     */
    MOD_CONFIG("modconfig"),

    /**
     * Manage my subreddit subscriptions. Manage "friends" - users whose content I follow.
     */
    SUBSCRIBE("subscribe"),

    /**
     * Edit structured styles for a subreddit I moderate.
     */
    STRUCTURED_STYLES("structuredstyles"),

    /**
     * Submit and change my votes on comments and submissions.
     */
    VOTE("vote"),

    /**
     * Edit wiki pages on my behalf
     */
    WIKI_EDIT("wikiedit"),

    /**
     * Access the list of subreddits I moderate, contribute to, and subscribe to.
     */
    MY_SUBREDDITS("mysubreddits"),

    /**
     * Submit links and comments from my account.
     */
    SUBMIT("submit"),

    /**
     * Access the moderation log in subreddits I moderate.
     */
    MOD_LOG("modlog"),

    /**
     * Approve, remove, mark nsfw, and distinguish content in subreddits I moderate.
     */
    MOD_POSTS("modposts"),

    /**
     * Manage and assign flair in subreddits I moderate.
     */
    MOD_FLAIR("modflair"),

    /**
     * Save and unsave comments and submissions.
     */
    SAVE("save"),

    /**
     * Invite or remove other moderators from subreddits I moderate.
     */
    MOD_OTHERS("modothers"),

    /**
     * Access posts and comments through my account.
     */
    READ("read"),

    /**
     * Access my inbox and send private messages to other users.
     */
    PRIVATE_MESSAGES("privatemessages"),

    /**
     * Report content for rules violations. Hide & show individual submissions.
     */
    REPORT("report"),

    /**
     * Access my reddit username and signup date.
     */
    IDENTITY("identity"),

    /**
     * Manage settings and contributors of live threads I contribute to.
     */
    LIVE_MANAGE("livemanage"),

    /**
     * Update preferences and related account information. Will not have access to your email or password.
     */
    ACCOUNT("account"),

    /**
     * Access traffic stats in subreddits I moderate.
     */
    MOD_TRAFFIC("modtraffic"),

    /**
     * Read wiki pages through my account
     */
    WIKI_READ("wikiread"),

    /**
     * Edit and delete my comments and submissions.
     */
    EDIT("edit"),

    /**
     * Change editors and visibility of wiki pages in subreddits I moderate.
     */
    MOD_WIKI("modwiki"),

    /**
     * Accept invitations to moderate a subreddit. Remove myself as a moderator or contributor of subreddits I moderate or contribute to.
     */
    MOD_SELF("modself"),

    /**
     * Access my voting history and comments or submissions I've saved or hidden.
     */
    HISTORY("history"),

    /**
     * Select my subreddit flair. Change link flair on my submissions.
     */
    FLAIR("flair");

    private final String identifier;
    Scope(String identifier) {
        this.identifier = identifier;
    }

    public final @NotNull String identifier() {
        return identifier;
    }
}
