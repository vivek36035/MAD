package com.premium.ledger;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.premium.ledger.databinding.ActivityCustomerDetailsBinding;

public class CustomerDetailsActivity extends AppCompatActivity {
    private ActivityCustomerDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int customerId = getIntent().getIntExtra("customer_id", -1);
        String name = getIntent().getStringExtra("customer_name");
        binding.tvCustomerName.setText(name != null ? name : "Customer");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnGive.setOnClickListener(v -> showAddTransactionDialog(customerId, "GIVE"));
        binding.btnGet.setOnClickListener(v -> showAddTransactionDialog(customerId, "GET"));

        binding.btnSendReminder.setOnClickListener(v -> sendSmsReminder(customerId));

        loadCustomerData(customerId);
    }

    private void sendSmsReminder(int customerId) {
        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(this);
        android.database.Cursor customerCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT name, phone, initial_amount, type FROM customers WHERE id = ?", 
                new String[]{String.valueOf(customerId)});
        
        if (customerCursor.moveToFirst()) {
            String name = customerCursor.getString(0);
            String phone = customerCursor.getString(1);
            double initialAmount = customerCursor.getDouble(2);
            String initialType = customerCursor.getString(3);
            
            double balance = initialType.equals("GIVE") ? initialAmount : -initialAmount;
            
            android.database.Cursor transCursor = dbHelper.getTransactionsByCustomerId(customerId);
            if (transCursor.moveToFirst()) {
                do {
                    double amount = transCursor.getDouble(transCursor.getColumnIndexOrThrow("amount"));
                    String type = transCursor.getString(transCursor.getColumnIndexOrThrow("type"));
                    balance += type.equals("GIVE") ? amount : -amount;
                } while (transCursor.moveToNext());
            }
            transCursor.close();

            String status = balance >= 0 ? "You owe me" : "I owe you";
            String message = "Hello " + name + ", your net balance is ₹ " + 
                             String.format("%.2f", Math.abs(balance)) + " (" + status + "). Please check the app for details.";
            
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("sms:" + phone));
            intent.putExtra("sms_body", message);
            startActivity(intent);
        }
        customerCursor.close();
    }

    private void loadCustomerData(int customerId) {
        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(this);
        
        // 1. Get Initial Balance
        android.database.Cursor customerCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT initial_amount, type FROM customers WHERE id = ?", 
                new String[]{String.valueOf(customerId)});
        
        double currentBalance = 0;
        if (customerCursor.moveToFirst()) {
            double initialAmount = customerCursor.getDouble(0);
            String type = customerCursor.getString(1);
            currentBalance = type.equals("GIVE") ? initialAmount : -initialAmount;
        }
        customerCursor.close();

        // 2. Add Transactions
        java.util.List<com.premium.ledger.adapters.TransactionAdapter.Transaction> transactionList = new java.util.ArrayList<>();
        android.database.Cursor transCursor = dbHelper.getTransactionsByCustomerId(customerId);
        if (transCursor.moveToFirst()) {
            do {
                int id = transCursor.getInt(transCursor.getColumnIndexOrThrow("id"));
                double amount = transCursor.getDouble(transCursor.getColumnIndexOrThrow("amount"));
                String type = transCursor.getString(transCursor.getColumnIndexOrThrow("type"));
                String date = transCursor.getString(transCursor.getColumnIndexOrThrow("date"));
                String note = transCursor.getString(transCursor.getColumnIndexOrThrow("note"));
                
                currentBalance += type.equals("GIVE") ? amount : -amount;
                transactionList.add(new com.premium.ledger.adapters.TransactionAdapter.Transaction(id, type, String.format("%.2f", amount), date, note));
            } while (transCursor.moveToNext());
        }
        transCursor.close();

        binding.rvTransactions.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.rvTransactions.setAdapter(new com.premium.ledger.adapters.TransactionAdapter(transactionList, () -> loadCustomerData(customerId)));

        // 3. Update UI
        binding.tvTotalBalance.setText("₹ " + String.format("%.2f", Math.abs(currentBalance)));
        if (currentBalance >= 0) {
            binding.tvTotalBalance.setTextColor(getColor(R.color.get_green));
            binding.tvBalanceLabel.setText("You will get");
        } else {
            binding.tvTotalBalance.setTextColor(getColor(R.color.give_red));
            binding.tvBalanceLabel.setText("You will give");
        }
    }

    private void showAddTransactionDialog(int customerId, String type) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(type.equals("GIVE") ? "You Gave" : "You Got");
        
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Amount");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(this);
                dbHelper.addTransaction(customerId, Double.parseDouble(amountStr), type, "Transaction");
                loadCustomerData(customerId); // Refresh UI
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
