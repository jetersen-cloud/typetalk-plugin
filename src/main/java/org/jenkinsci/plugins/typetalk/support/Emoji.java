package org.jenkinsci.plugins.typetalk.support;

public enum Emoji {
    LOUDSPEAKER(":loudspeaker:"),
    MEGA(":mega:"),
    ASTONISHED(":astonished:"),
    RAGE(":rage:"),
    CRY(":cry:"),
    SMILEY(":smiley:"),
    MASK(":mask:"),
    BOOK(":book:"),
    PAGE_FACING_UP(":page_facing_up:"),
    QUESTION(":question: ");

    private final String symbol;

    public String getSymbol() {
        return symbol;
    }

    Emoji(String symbol) {
        this.symbol = symbol;
    }
}
