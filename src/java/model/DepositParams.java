package model;

public class DepositParams {
    private int accountId;
    private double amount;
    private String details;

    public DepositParams(int accountId, double amount, String details) {
        this.accountId = accountId;
        this.amount = amount;
        this.details = details;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }
}
