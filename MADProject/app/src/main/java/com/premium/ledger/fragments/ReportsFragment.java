package com.premium.ledger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.premium.ledger.databinding.FragmentReportsBinding;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCharts();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCharts();
    }

    private void setupCharts() {
        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(getContext());
        android.database.Cursor cursor = dbHelper.getExpensesByCategory();
        
        java.util.ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new java.util.ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float total = cursor.getFloat(1);
                entries.add(new com.github.mikephil.charting.data.PieEntry(total, category));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (entries.isEmpty()) {
            binding.pieChart.setNoDataText("No expense data available");
            binding.pieChart.invalidate();
            return;
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "Expenses");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);
        dataSet.setValueTextSize(12f);

        com.github.mikephil.charting.data.PieData data = new com.github.mikephil.charting.data.PieData(dataSet);
        binding.pieChart.setData(data);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setCenterText("Expenses");
        binding.pieChart.setCenterTextColor(android.graphics.Color.WHITE);
        binding.pieChart.setHoleColor(android.graphics.Color.TRANSPARENT);
        binding.pieChart.getLegend().setTextColor(android.graphics.Color.WHITE);
        binding.pieChart.animateY(1000);
        binding.pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
