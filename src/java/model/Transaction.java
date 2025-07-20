package model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int accountId;
    private String type;    // "deposit", "withdraw", "transfer"
    private double amount;
    private LocalDateTime date;
    private String details;

    public Transaction(int accountId, String type, double amount, LocalDateTime date, String details) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.details = details;
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
