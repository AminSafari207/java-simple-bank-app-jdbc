package model;

public class TransferParams {
    private Account fromAccount;
    private Account toAccount;
    private double amount;
    private String details;

    public TransferParams(Account fromAccount, Account toAccount, double amount, String details) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.details = details == null ? "" : details;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }
}
