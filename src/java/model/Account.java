package model;

public class Account {
    private int id;
    private int customerId;
    private int account_number;
    private double balance;

    public Account(int customerId, int account_number, double balance) {
        this.customerId = customerId;
        this.account_number = account_number;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAccount_number() {
        return account_number;
    }

    public double getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }
}
