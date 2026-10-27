package com.smartpantry;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Binds pantry rows from the database to item_pantry.xml. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed list (called after every database read). */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.qty.setText(Units.format(item.getQuantity()) + " " + item.getUnit());

        String expiry = item.getExpiry();
        if (expiry == null || expiry.isEmpty()) {
            h.expiry.setText("No expiry date");
            h.expiry.setTextColor(Color.GRAY);
        } else {
            boolean soon = ExpiryUtil.isExpiringSoon(expiry);
            h.expiry.setText((soon ? "Expiring soon: " : "Expires: ") + expiry);
            h.expiry.setTextColor(soon ? Color.parseColor("#C62828") : Color.GRAY);
        }
        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, qty, expiry;
        final ImageButton delete;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.textName);
            qty = v.findViewById(R.id.textQty);
            expiry = v.findViewById(R.id.textExpiry);
            delete = v.findViewById(R.id.buttonDelete);
        }
    }
}
