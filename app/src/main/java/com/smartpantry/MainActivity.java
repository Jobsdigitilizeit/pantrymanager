package com.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/** Pantry list screen: shows every ingredient, with add / edit / delete. */
public class MainActivity extends BaseActivity implements PantryAdapter.Listener {
    public static final String EXTRA_ITEM_ID = "item_id";

    private PantryRepository repo;
    private PantryAdapter adapter;
    private TextView emptyView, expiryBanner;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.menu_pantry);

        repo = new PantryRepository();
        repo.seedRecipesIfEmpty(); // first run only
        emptyView = findViewById(R.id.textEmpty);
        expiryBanner = findViewById(R.id.textExpiryBanner);

        RecyclerView list = findViewById(R.id.recyclerPantry);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        list.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditItemActivity.class)));
    }

    /** Listen to Firestore while visible; the list refreshes itself on any change. */
    @Override
    protected void onStart() {
        super.onStart();
        listener = repo.listenItems(this::show);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) listener.remove();
    }

    private void show(List<PantryItem> items) {
        adapter.setItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

        int soon = 0;
        for (PantryItem item : items) if (ExpiryUtil.isExpiringSoon(item.getExpiry())) soon++;
        if (Prefs.expiryAlerts(this) && soon > 0) {
            expiryBanner.setText(soon + (soon == 1 ? " item is" : " items are")
                    + " expiring within " + ExpiryUtil.SOON_DAYS + " days - cook with "
                    + (soon == 1 ? "it" : "them") + " soon!");
            expiryBanner.setVisibility(View.VISIBLE);
        } else {
            expiryBanner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(this, AddEditItemActivity.class);
        i.putExtra(EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    repo.deleteItem(item.getId());
                    Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
