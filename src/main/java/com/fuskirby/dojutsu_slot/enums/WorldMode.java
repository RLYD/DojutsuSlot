package com.fuskirby.dojutsu_slot.enums;

public enum WorldMode {
    CLASSIC,
    DOJUTSU;

    /**
     * Returns the translation key used for display.
     * Use {@code new TextComponentTranslation(mode.getTranslationKey())} on the server side,
     * or {@code I18n.format(mode.getTranslationKey())} on the client side.
     */
    public String getTranslationKey() {
        return this == CLASSIC ? "dojutsu.mode.classic" : "dojutsu.mode.dojutsu";
    }
}
