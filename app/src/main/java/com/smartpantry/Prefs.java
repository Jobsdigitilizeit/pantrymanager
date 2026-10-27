package com.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;

/** Thin wrapper over SharedPreferences for the two settings toggles. */
public final class Prefs {
    private static final String FILE = "settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_ALMOST_THERE = "almost_there";

    private Prefs() { }

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlerts(Context c) { return sp(c).getBoolean(KEY_EXPIRY_ALERTS, true); }
    public static void setExpiryAlerts(Context c, boolean v) { sp(c).edit().putBoolean(KEY_EXPIRY_ALERTS, v).apply(); }

    public static boolean almostThere(Context c) { return sp(c).getBoolean(KEY_ALMOST_THERE, false); }
    public static void setAlmostThere(Context c, boolean v) { sp(c).edit().putBoolean(KEY_ALMOST_THERE, v).apply(); }
}
