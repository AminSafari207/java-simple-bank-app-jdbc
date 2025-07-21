package model;

public class WithdrawParams {
    private Account account;
    private double amount;
    private String details;

    public WithdrawParams(Account account, double amount, String details) {
        this.account = account;
        this.amount = amount;
        this.details = details;
    }

    public Account getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }
}
