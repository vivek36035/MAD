package com.premium.ledger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.premium.ledger.R;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final List<ExpenseItem> expenses;
    private final Runnable onDataChanged;

    public ExpenseAdapter(List<ExpenseItem> expenses, Runnable onDataChanged) {
        this.expenses = expenses;
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseItem item = expenses.get(position);
        holder.text1.setText(item.category + ": ₹ " + item.amount);
        holder.text2.setText(item.date + " - " + item.note);
        holder.text1.setTextColor(holder.itemView.getContext().getColor(R.color.white));

        holder.itemView.setOnLongClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
            builder.setTitle("Expense Option");
            builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                com.premium.ledger.database.DatabaseHelper db = new com.premium.ledger.database.DatabaseHelper(v.getContext());
                if (which == 0) {
                    // Edit logic
                    android.content.Intent intent = new android.content.Intent(v.getContext(), com.premium.ledger.AddExpenseActivity.class);
                    intent.putExtra("expense_id", item.id);
                    intent.putExtra("amount", item.amount);
                    intent.putExtra("note", item.note);
                    intent.putExtra("category", item.category);
                    v.getContext().startActivity(intent);
                } else {
                    db.deleteExpense(item.id);
                    if (onDataChanged != null) onDataChanged.run();
                }
            });
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text1, text2;
        public ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(android.R.id.text1);
            text2 = view.findViewById(android.R.id.text2);
        }
    }

    public static class ExpenseItem {
        int id;
        String amount, note, category, date;
        public ExpenseItem(int id, String amount, String note, String category, String date) {
            this.id = id;
            this.amount = amount;
            this.note = note;
            this.category = category;
            this.date = date;
        }
    }
}
