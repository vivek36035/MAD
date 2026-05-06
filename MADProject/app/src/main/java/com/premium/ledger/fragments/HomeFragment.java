package com.premium.ledger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.premium.ledger.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private java.util.List<com.premium.ledger.models.Customer> allCustomers = new java.util.ArrayList<>();
    private com.premium.ledger.adapters.CustomerAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabAdd.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), com.premium.ledger.AddCustomerActivity.class);
            startActivity(intent);
        });
        
        binding.ivSearch.setOnClickListener(v -> {
            binding.llGreetingContainer.setVisibility(View.GONE);
            binding.llSearchContainer.setVisibility(View.VISIBLE);
            binding.etSearch.requestFocus();
            // Show keyboard
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        });

        binding.ivCloseSearch.setOnClickListener(v -> {
            binding.llSearchContainer.setVisibility(View.GONE);
            binding.llGreetingContainer.setVisibility(View.VISIBLE);
            binding.etSearch.setText("");
            filterCustomers("");
        });

        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomers(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.tvViewAll.setOnClickListener(v -> {
            binding.etSearch.setText("");
            binding.llSearchContainer.setVisibility(View.GONE);
            binding.llGreetingContainer.setVisibility(View.VISIBLE);
            filterCustomers("");
        });

        updateGreeting();
    }

    private void updateGreeting() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int timeOfDay = c.get(java.util.Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Night";
        }
        binding.tvGreeting.setText(greeting);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGreeting();
        loadCustomers();
    }

    private void loadCustomers() {
        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(getContext());
        android.database.Cursor cursor = dbHelper.getCustomersWithBalance();
        
        allCustomers.clear();
        double totalGet = 0;
        double totalGive = 0;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("current_balance"));
                
                if (balance >= 0) totalGet += balance;
                else totalGive += Math.abs(balance);

                allCustomers.add(new com.premium.ledger.models.Customer(id, name, balance, "Just now"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        binding.tvNetBalance.setText("₹ " + String.format("%.2f", totalGet - totalGive));
        binding.tvYouGet.setText("₹ " + String.format("%.2f", totalGet));
        binding.tvYouGive.setText("₹ " + String.format("%.2f", totalGive));

        filterCustomers(binding.etSearch.getText().toString());
    }

    private void filterCustomers(String query) {
        java.util.List<com.premium.ledger.models.Customer> filteredList = new java.util.ArrayList<>();
        for (com.premium.ledger.models.Customer customer : allCustomers) {
            if (customer.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(customer);
            }
        }

        adapter = new com.premium.ledger.adapters.CustomerAdapter(filteredList, this::loadCustomers);
        binding.rvCustomers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvCustomers.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
