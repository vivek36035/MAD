package com.premium.ledger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.premium.ledger.databinding.FragmentExpensesBinding;

public class ExpensesFragment extends Fragment {

    private FragmentExpensesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.fabAddExpense.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), com.premium.ledger.AddExpenseActivity.class);
            startActivity(intent);
        });

        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                com.google.android.material.chip.Chip chip = view.findViewById(checkedIds.get(0));
                String category = chip.getText().toString();
                loadExpenses(category);
            } else {
                loadExpenses("All");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses("All");
    }

    private void loadExpenses(String categoryFilter) {
        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(getContext());
        android.database.Cursor cursor = dbHelper.getAllExpenses(categoryFilter);
        
        java.util.List<com.premium.ledger.adapters.ExpenseAdapter.ExpenseItem> expenseList = new java.util.ArrayList<>();
        double total = 0;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                
                total += amount;
                expenseList.add(new com.premium.ledger.adapters.ExpenseAdapter.ExpenseItem(id, String.format("%.2f", amount), note, category, date));
            } while (cursor.moveToNext());
        }
        cursor.close();

        binding.tvTotalExpense.setText("₹ " + String.format("%.2f", total));
        
        binding.rvExpenses.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvExpenses.setAdapter(new com.premium.ledger.adapters.ExpenseAdapter(expenseList, () -> loadExpenses(categoryFilter)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
