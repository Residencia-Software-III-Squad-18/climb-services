package com.climb.api.util;

/**
 * Descrições seguras para logs (sem valor de token/código).
 */
public final class LogSanitizer {

    private LogSanitizer() {
    }

    /** Ex.: {@code absent}, {@code blank}, {@code present(length=142)} */
    public static String googleAccessTokenForLog(String token) {
        if (token == null) {
            return "absent";
        }
        String t = token.trim();
        if (t.isEmpty()) {
            return "blank";
        }
        return "present(length=" + t.length() + ")";
    }

    /** Para códigos OAuth / exchange: só comprimento. */
    public static String oauthCodeForLog(String code) {
        if (code == null) {
            return "absent";
        }
        String c = code.trim();
        if (c.isEmpty()) {
            return "blank";
        }
        return "present(length=" + c.length() + ")";
    }
}
