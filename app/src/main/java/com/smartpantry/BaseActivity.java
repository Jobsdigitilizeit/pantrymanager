package com.smartpantry;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/** Shares the toolbar navigation menu (Pantry / Suggested / Settings) across top-level screens. */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pantry) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            return true;
        } else if (id == R.id.action_suggested) {
            if (!(this instanceof SuggestedRecipesActivity)) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
            }
            return true;
        } else if (id == R.id.action_settings) {
            if (!(this instanceof SettingsActivity)) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
