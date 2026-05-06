package com.premium.ledger.models;

public class Customer {
    private int id;
    private String name;
    private double balance;
    private String lastUpdated;

    public Customer(int id, String name, double balance, String lastUpdated) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public String getLastUpdated() { return lastUpdated; }
}
