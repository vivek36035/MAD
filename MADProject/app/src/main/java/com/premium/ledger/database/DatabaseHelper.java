package com.premium.ledger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PremiumLedger.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Customers Table
        db.execSQL("CREATE TABLE customers (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, initial_amount REAL, type TEXT)");
        
        // Transactions Table (for specific customers)
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER, amount REAL, type TEXT, note TEXT, date DATETIME DEFAULT CURRENT_TIMESTAMP)");
        
        // General Expenses Table
        db.execSQL("CREATE TABLE expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, note TEXT, category TEXT, date DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS customers");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }

    // CRUD for Customers
    public long addCustomer(String name, String phone, double amount, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("initial_amount", amount);
        values.put("type", type);
        return db.insert("customers", null, values);
    }

    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM customers", null);
    }

    public Cursor getCustomersWithBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.*, " +
                "((CASE WHEN c.type = 'GIVE' THEN c.initial_amount ELSE -c.initial_amount END) + " +
                "COALESCE((SELECT SUM(CASE WHEN t.type = 'GIVE' THEN t.amount ELSE -t.amount END) " +
                "FROM transactions t WHERE t.customer_id = c.id), 0)) as current_balance " +
                "FROM customers c";
        return db.rawQuery(query, null);
    }

    // CRUD for Expenses
    public long addExpense(double amount, String note, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        if (date != null) values.put("date", date);
        return db.insert("expenses", null, values);
    }

    public void updateExpense(int id, double amount, String note, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        values.put("date", date);
        db.update("expenses", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllExpenses(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (category == null || category.equals("All")) {
            return db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null);
        } else {
            return db.rawQuery("SELECT * FROM expenses WHERE category = ? ORDER BY date DESC", new String[]{category});
        }
    }

    public Cursor getTransactionsByCustomerId(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE customer_id = ?", new String[]{String.valueOf(customerId)});
    }

    public long addTransaction(int customerId, double amount, String type, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_id", customerId);
        values.put("amount", amount);
        values.put("type", type);
        values.put("note", note);
        return db.insert("transactions", null, values);
    }

    // UPDATE and DELETE
    public void deleteCustomer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("customers", "id = ?", new String[]{String.valueOf(id)});
        db.delete("transactions", "customer_id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public void updateCustomer(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        db.update("customers", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getExpensesByCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT category, SUM(amount) as total FROM expenses GROUP BY category", null);
    }
}
