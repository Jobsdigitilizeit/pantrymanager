package com.smartpantry;

import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;

/** Settings screen with two persisted toggles. */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.menu_settings);

        SwitchCompat expiry = findViewById(R.id.switchExpiry);
        SwitchCompat almost = findViewById(R.id.switchAlmost);

        expiry.setChecked(Prefs.expiryAlerts(this));
        almost.setChecked(Prefs.almostThere(this));

        expiry.setOnCheckedChangeListener((CompoundButton b, boolean on) -> Prefs.setExpiryAlerts(this, on));
        almost.setOnCheckedChangeListener((CompoundButton b, boolean on) -> Prefs.setAlmostThere(this, on));
    }
}
