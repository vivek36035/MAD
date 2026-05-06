package com.premium.ledger;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.premium.ledger.databinding.ActivityAddCustomerBinding;

public class AddCustomerActivity extends AppCompatActivity {
    private ActivityAddCustomerBinding binding;

    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> contactPickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    android.net.Uri contactUri = result.getData().getData();
                    extractContactInfo(contactUri);
                }
            });

    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchContactPicker();
                } else {
                    android.widget.Toast.makeText(this, "Permission denied to read contacts", android.widget.Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        com.premium.ledger.database.DatabaseHelper dbHelper = new com.premium.ledger.database.DatabaseHelper(this);

        binding.tilName.setEndIconOnClickListener(v -> {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                launchContactPicker();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS);
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.tilName.getEditText().getText().toString();
            String phone = binding.tilPhone.getEditText().getText().toString();
            String amountStr = binding.tilAmount.getEditText().getText().toString();
            double amount = amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr);
            String type = binding.btgType.getCheckedButtonId() == R.id.btn_get ? "GET" : "GIVE";

            if (!name.isEmpty()) {
                dbHelper.addCustomer(name, phone, amount, type);
                finish();
            } else {
                binding.tilName.setError("Name is required");
            }
        });
    }

    private void launchContactPicker() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI);
        contactPickerLauncher.launch(intent);
    }

    private void extractContactInfo(android.net.Uri contactUri) {
        android.database.Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.ContactsContract.Contacts._ID));
            
            binding.tilName.getEditText().setText(name);

            if (cursor.getInt(cursor.getColumnIndexOrThrow(android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                android.database.Cursor pCur = getContentResolver().query(
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                if (pCur != null && pCur.moveToFirst()) {
                    String phone = pCur.getString(pCur.getColumnIndexOrThrow(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                    binding.tilPhone.getEditText().setText(phone);
                    pCur.close();
                }
            }
            cursor.close();
        }
    }
}
