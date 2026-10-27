package com.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Date helpers for the optional yyyy-MM-dd expiry field. */
public final class ExpiryUtil {
    public static final int SOON_DAYS = 3;
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private ExpiryUtil() { }

    /** True if the item expires within SOON_DAYS days (or has already expired). */
    public static boolean isExpiringSoon(String expiry) {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            Date d = FORMAT.parse(expiry);
            if (d == null) return false;
            long days = TimeUnit.MILLISECONDS.toDays(d.getTime() - System.currentTimeMillis());
            return days <= SOON_DAYS;
        } catch (ParseException e) {
            return false;
        }
    }
}
