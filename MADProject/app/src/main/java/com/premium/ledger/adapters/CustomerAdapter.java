package com.premium.ledger.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.premium.ledger.databinding.ItemCustomerBinding;
import com.premium.ledger.models.Customer;
import com.premium.ledger.R;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private final List<Customer> customers;
    private final Runnable onDataChanged;

    public CustomerAdapter(List<Customer> customers, Runnable onDataChanged) {
        this.customers = customers;
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerBinding binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.premium.ledger.models.Customer customer = customers.get(position);
        holder.binding.tvName.setText(customer.getName());
        holder.binding.tvBalance.setText("₹ " + String.format("%.2f", Math.abs(customer.getBalance())));
        holder.binding.tvLastUpdated.setText(customer.getLastUpdated());

        if (customer.getBalance() >= 0) {
            holder.binding.tvBalance.setTextColor(holder.binding.getRoot().getContext().getColor(R.color.get_green));
            holder.binding.tvBalanceLabel.setText("You will get");
        } else {
            holder.binding.tvBalance.setTextColor(holder.binding.getRoot().getContext().getColor(R.color.give_red));
            holder.binding.tvBalanceLabel.setText("You will give");
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), com.premium.ledger.CustomerDetailsActivity.class);
            intent.putExtra("customer_id", customer.getId());
            intent.putExtra("customer_name", customer.getName());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            // Show options dialog (Edit/Delete)
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
            builder.setTitle(customer.getName());
            builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                if (which == 0) {
                    // Edit logic
                    android.app.AlertDialog.Builder editBuilder = new android.app.AlertDialog.Builder(v.getContext());
                    editBuilder.setTitle("Edit Customer");
                    
                    android.widget.LinearLayout layout = new android.widget.LinearLayout(v.getContext());
                    layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                    layout.setPadding(32, 32, 32, 32);

                    android.widget.EditText nameInput = new android.widget.EditText(v.getContext());
                    nameInput.setText(customer.getName());
                    layout.addView(nameInput);

                    editBuilder.setView(layout);
                    editBuilder.setPositiveButton("Update", (d, w) -> {
                        com.premium.ledger.database.DatabaseHelper db = new com.premium.ledger.database.DatabaseHelper(v.getContext());
                        db.updateCustomer(customer.getId(), nameInput.getText().toString(), "");
                        if (onDataChanged != null) onDataChanged.run();
                    });
                    editBuilder.show();
                } else {
                    // Delete logic
                    com.premium.ledger.database.DatabaseHelper db = new com.premium.ledger.database.DatabaseHelper(v.getContext());
                    db.deleteCustomer(customer.getId());
                    if (onDataChanged != null) onDataChanged.run();
                }
            });
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCustomerBinding binding;
        public ViewHolder(ItemCustomerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
