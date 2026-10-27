package com.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/** Add / Edit ingredient form. Edit mode is triggered by an item_id extra. */
public class AddEditItemActivity extends AppCompatActivity {
    private PantryRepository repo;
    private EditText nameInput, qtyInput, expiryInput;
    private Spinner unitSpinner;
    private String itemId; // null means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repo = new PantryRepository();
        nameInput = findViewById(R.id.inputName);
        qtyInput = findViewById(R.id.inputQuantity);
        expiryInput = findViewById(R.id.inputExpiry);
        unitSpinner = findViewById(R.id.spinnerUnit);
        Button save = findViewById(R.id.buttonSave);
        Button clearDate = findViewById(R.id.buttonClearDate);

        unitSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Units.ALL));

        // Date picker instead of free-text typing, so the format is always valid.
        expiryInput.setOnClickListener(v -> showDatePicker());
        clearDate.setOnClickListener(v -> expiryInput.setText(""));

        itemId = getIntent().getStringExtra(MainActivity.EXTRA_ITEM_ID);
        if (itemId != null) {
            setTitle("Edit ingredient");
            save.setEnabled(false); // until the existing values have loaded
            repo.getItem(itemId, item -> {
                if (item == null) { // deleted elsewhere
                    finish();
                    return;
                }
                nameInput.setText(item.getName());
                qtyInput.setText(Units.format(item.getQuantity()));
                expiryInput.setText(item.getExpiry());
                for (int i = 0; i < Units.ALL.length; i++) {
                    if (Units.ALL[i].equals(item.getUnit())) unitSpinner.setSelection(i);
                }
                save.setEnabled(true);
            });
        } else {
            setTitle("Add ingredient");
        }

        save.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        String current = expiryInput.getText().toString();
        if (!current.isEmpty()) {
            try {
                Date d = ExpiryUtil.FORMAT.parse(current);
                if (d != null) cal.setTime(d);
            } catch (ParseException ignored) { }
        }
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(y, m, d);
            expiryInput.setText(ExpiryUtil.FORMAT.format(picked.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form, then inserts or updates. */
    private void save() {
        String name = nameInput.getText().toString().trim();
        String qtyText = qtyInput.getText().toString().trim();
        boolean ok = true;

        nameInput.setError(null);
        qtyInput.setError(null);

        if (name.isEmpty()) {
            nameInput.setError("Ingredient name is required");
            ok = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            nameInput.setError("Name must contain letters");
            ok = false;
        }

        double qty = 0;
        if (qtyText.isEmpty()) {
            qtyInput.setError("Quantity is required");
            ok = false;
        } else {
            try {
                qty = Double.parseDouble(qtyText);
                if (qty <= 0) {
                    qtyInput.setError("Quantity must be greater than 0");
                    ok = false;
                }
            } catch (NumberFormatException e) {
                qtyInput.setError("Enter a valid number");
                ok = false;
            }
        }
        if (!ok) return;

        String unit = (String) unitSpinner.getSelectedItem();
        String expiry = expiryInput.getText().toString().trim();
        if (expiry.isEmpty()) expiry = null;

        if (itemId == null) {
            repo.addItem(name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            repo.updateItem(itemId, name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
