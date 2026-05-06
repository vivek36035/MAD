package com.premium.ledger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.premium.ledger.R;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactions;
    private final Runnable onDataChanged;

    public TransactionAdapter(List<Transaction> transactions, Runnable onDataChanged) {
        this.transactions = transactions;
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
        Transaction transaction = transactions.get(position);
        holder.text1.setText(transaction.type + ": ₹ " + transaction.amount);
        holder.text2.setText(transaction.date + " - " + transaction.note);
        
        if (transaction.type.equals("GET")) {
            holder.text1.setTextColor(holder.itemView.getContext().getColor(R.color.get_green));
        } else {
            holder.text1.setTextColor(holder.itemView.getContext().getColor(R.color.give_red));
        }

        holder.itemView.setOnLongClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
            builder.setTitle("Transaction Option");
            builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                com.premium.ledger.database.DatabaseHelper db = new com.premium.ledger.database.DatabaseHelper(v.getContext());
                if (which == 0) {
                    // Edit logic (simplified)
                    android.widget.Toast.makeText(v.getContext(), "Edit functionality coming soon", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    db.deleteTransaction(transaction.id);
                    if (onDataChanged != null) onDataChanged.run();
                }
            });
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text1, text2;
        public ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(android.R.id.text1);
            text2 = view.findViewById(android.R.id.text2);
        }
    }

    public static class Transaction {
        int id;
        String type, amount, date, note;
        public Transaction(int id, String type, String amount, String date, String note) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.date = date;
            this.note = note;
        }
    }
}
