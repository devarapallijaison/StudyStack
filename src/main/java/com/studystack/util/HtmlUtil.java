package com.studystack.util;

/** Minimal HTML text escaping for JSP output. */
public final class HtmlUtil {
    private HtmlUtil() {
    }

    public static String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
