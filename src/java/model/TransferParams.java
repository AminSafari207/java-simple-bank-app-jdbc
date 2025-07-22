package model;

public class TransferParams {
    private Account fromAccount;
    private Account toAccount;
    private double amount;

    public TransferParams(Account fromAccount, Account toAccount, double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
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
}
