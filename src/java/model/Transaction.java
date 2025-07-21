package model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int accountId;
    private String type;    // "deposit", "withdraw", "transfer"
    private double amount;
    private LocalDateTime date;
    private String details;

    public Transaction(int accountId, String type, double amount, String details) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.details = details;
    }

    public Transaction(int id, int accountId, String type, double amount, String details) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }
}
