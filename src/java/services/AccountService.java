package services;

import model.Account;
import model.DepositParams;
import model.Transaction;
import model.WithdrawParams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    public Account createAccount(Account account) throws SQLException {
        String sqlQuery = "INSERT INTO account (customer_id, account_number, balance) VALUES (?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        ps.setInt(1, account.getCustomerId());
        ps.setString(2, account.getAccountNumber());
        ps.setDouble(3, roundTwoDecimals(account.getBalance()));
        ps.executeUpdate();

        Account accountWithId = null;
        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            accountWithId = new Account(
                    rs.getInt(1),
                    account.getCustomerId(),
                    account.getAccountNumber(),
                    account.getBalance()
            );
        } else {
            rs.close();
            ps.close();
            connection.close();
            throw new SQLException("Failed to retrieve generated account ID.");
        }

        rs.close();
        ps.close();
        connection.close();

        return accountWithId;
    }

    public void deleteAccount(int accountId) throws SQLException {
        String sqlQuery = "DELETE FROM account WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        ps.setInt(1, accountId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    public void deposit(DepositParams params, boolean skipTransaction) throws SQLException {
        if (params == null) return;

        String sqlQuery = "UPDATE account SET balance = balance + ? WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        int accountId = params.getAccount().getId();
        double amount = roundTwoDecimals(params.getAmount());

        ps.setDouble(1, amount);
        ps.setInt(2, accountId);
        ps.executeUpdate();

        params.getAccount().increaseBalance(amount);

        if (!skipTransaction) {
            Transaction transaction = new Transaction(accountId, "deposit", params.getAmount(), params.getDetails());
            TransactionService.createTransaction(transaction);
        }

        ps.close();
        connection.close();
    }

    public void deposit(DepositParams[] paramsBatch) throws SQLException {
        if (paramsBatch == null || paramsBatch.length == 0) return;

        String sqlQuery = "UPDATE account SET balance = balance + ? WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        connection.setAutoCommit(false);

        List<Transaction> transactions = new ArrayList<>();

        for (DepositParams params: paramsBatch) {
            if (params == null) continue;

            int accountId = params.getAccount().getId();
            double amount = roundTwoDecimals(params.getAmount());

            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.addBatch();

            transactions.add(new Transaction(accountId, "deposit", amount, params.getDetails()));
        }

        ps.executeBatch();
        connection.commit();

        for (DepositParams params : paramsBatch) {
            if (params != null) {
                params.getAccount().increaseBalance(roundTwoDecimals(params.getAmount()));
            }
        }

        TransactionService.createTransaction(transactions);

        ps.close();
        connection.close();
    }

    public void withdraw(WithdrawParams params, boolean skipTransaction) throws SQLException {
        if (params == null) return;

        String sqlQuery = "UPDATE account SET balance = balance - ? WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        int accountId = params.getAccount().getId();
        double amount = roundTwoDecimals(params.getAmount());

        ps.setDouble(1, amount);
        ps.setInt(2, accountId);
        ps.executeUpdate();

        params.getAccount().decreaseBalance(amount); // subtract

        if (!skipTransaction) {
            Transaction transaction = new Transaction(accountId, "withdraw", amount, params.getDetails());
            TransactionService.createTransaction(transaction);
        }

        ps.close();
        connection.close();
    }

    public void withdraw(WithdrawParams[] paramsBatch) throws SQLException {
        if (paramsBatch == null || paramsBatch.length == 0) return;

        String sqlQuery = "UPDATE account SET balance = balance - ? WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        connection.setAutoCommit(false);

        List<Transaction> transactions = new ArrayList<>();

        for (WithdrawParams params : paramsBatch) {
            if (params == null) continue;

            int accountId = params.getAccount().getId();
            double amount = roundTwoDecimals(params.getAmount());

            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.addBatch();

            transactions.add(new Transaction(accountId, "withdraw", amount, params.getDetails()));
        }

        ps.executeBatch();
        connection.commit();

        for (WithdrawParams params : paramsBatch) {
            if (params != null) {
                params.getAccount().increaseBalance(-roundTwoDecimals(params.getAmount()));
            }
        }

        TransactionService.createTransaction(transactions);

        ps.close();
        connection.close();
    }

    public double roundTwoDecimals(double num) {
        return Math.round(num * 100.0) / 100.0;
    }
}
