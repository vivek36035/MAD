package com.premium.ledger;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.premium.ledger.databinding.ActivityAddExpenseBinding;

public class AddExpenseActivity extends AppCompatActivity {
    private ActivityAddExpenseBinding binding;

    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    private int expenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(this);

        // Check if editing
        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId != -1) {
            binding.tvTitle.setText("Edit Expense");
            binding.btnSave.setText("Update Expense");
            binding.tilAmount.getEditText().setText(getIntent().getStringExtra("amount"));
            binding.tilNote.getEditText().setText(getIntent().getStringExtra("note"));
            String category = getIntent().getStringExtra("category");
            for (int i = 0; i < binding.cgCategories.getChildCount(); i++) {
                com.google.android.material.chip.Chip chip = (com.google.android.material.chip.Chip) binding.cgCategories.getChildAt(i);
                if (chip.getText().toString().equals(category)) {
                    chip.setChecked(true);
                    break;
                }
            }
            // Parse date if needed, for now just use current or from intent
        }

        updateDateLabel();
        updateTimeLabel();

        binding.tilDate.getEditText().setOnClickListener(v -> showDatePicker());
        binding.tilTime.getEditText().setOnClickListener(v -> showTimePicker());

        binding.btnSave.setOnClickListener(v -> {
            String amountStr = binding.tilAmount.getEditText().getText().toString();
            String note = binding.tilNote.getEditText().getText().toString();
            
            int checkedId = binding.cgCategories.getCheckedChipId();
            String category = "General";
            if (checkedId != -1) {
                com.google.android.material.chip.Chip chip = findViewById(checkedId);
                category = chip.getText().toString();
            }

            if (!amountStr.isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                String dateTime = sdf.format(calendar.getTime());

                if (expenseId == -1) {
                    dbHelper.addExpense(Double.parseDouble(amountStr), note, category, dateTime);
                } else {
                    dbHelper.updateExpense(expenseId, Double.parseDouble(amountStr), note, category, dateTime);
                }
                finish();
            } else {
                binding.tilAmount.setError("Amount is required");
            }
        });
    }

    private void showDatePicker() {
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(java.util.Calendar.MINUTE, minute);
            updateTimeLabel();
        }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true).show();
    }

    private void updateDateLabel() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        binding.tilDate.getEditText().setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        binding.tilTime.getEditText().setText(sdf.format(calendar.getTime()));
    }
}
