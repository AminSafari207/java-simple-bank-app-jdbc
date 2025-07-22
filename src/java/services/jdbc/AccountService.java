package services.jdbc;

import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    public Account createAccount(Account account) throws SQLException {
        String sqlQuery = "INSERT INTO account (customer_id, account_number, balance) VALUES (?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

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

    public void deposit(List<DepositParams> paramsBatch) throws SQLException {
        if (paramsBatch == null || paramsBatch.isEmpty()) return;

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

    public void withdraw(List<WithdrawParams> paramsBatch) throws SQLException {
        if (paramsBatch == null || paramsBatch.isEmpty()) return;

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

    public void transfer(TransferParams params) throws SQLException {
        if (params == null) return;

        Account fromAccount = params.getFromAccount();
        Account toAccount = params.getToAccount();
        double amount = roundTwoDecimals(params.getAmount());

        withdraw(new WithdrawParams(fromAccount, amount, null), true);
        deposit(new DepositParams(toAccount, amount, null), true);

        List<Transaction> transactions = List.of(
                new Transaction(
                        fromAccount.getId(),
                        "transfer",
                        amount,
                        "Transfer to account number: " + fromAccount.getAccountNumber()
                ),
                new Transaction(
                        toAccount.getId(),
                        "transfer",
                        amount,
                        "Transfer from account number: " + toAccount.getAccountNumber()
                )
        );

        TransactionService.createTransaction(transactions);
    }

    public void transfer(List<TransferParams> paramsBatch) throws SQLException {
        if (paramsBatch == null || paramsBatch.isEmpty()) return;

        Connection connection = DBConnection.getConnection();
        connection.setAutoCommit(false);

        String withdrawQuery = "UPDATE account SET balance = balance + ? WHERE id = ?";
        String depositQuery = "UPDATE account SET balance = balance - ? WHERE id = ?";
        String transactionQuery = "INSERT INTO transaction (account_id, type, amount, date, details) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement withdrawPs = connection.prepareStatement(withdrawQuery);
        PreparedStatement depositPs = connection.prepareStatement(depositQuery);
        PreparedStatement transactionPs = connection.prepareStatement(transactionQuery);

        for (TransferParams params: paramsBatch) {
            if (params == null) continue;

            Account fromAccount = params.getFromAccount();
            Account toAccount = params.getToAccount();
            double amount = roundTwoDecimals(params.getAmount());
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            String transactionType = "transfer";
            String fromDetails = "Transfer to account number: " + toAccount.getAccountNumber();
            String toDetails = "Transfer from account number: " + fromAccount.getAccountNumber();

            withdrawPs.setDouble(1, amount);
            withdrawPs.setInt(2, fromAccount.getId());
            withdrawPs.addBatch();

            depositPs.setDouble(1, amount);
            depositPs.setInt(2, toAccount.getId());
            depositPs.addBatch();

            transactionPs.setInt(1, fromAccount.getId());
            transactionPs.setString(2, transactionType);
            transactionPs.setDouble(3, amount);
            transactionPs.setTimestamp(4, timestamp);
            transactionPs.setString(5, fromDetails);
            transactionPs.addBatch();

            transactionPs.setInt(1, toAccount.getId());
            transactionPs.setString(2, transactionType);
            transactionPs.setDouble(3, amount);
            transactionPs.setTimestamp(4, timestamp);
            transactionPs.setString(5, toDetails);
            transactionPs.addBatch();
        }

        withdrawPs.executeBatch();
        depositPs.executeBatch();
        transactionPs.executeBatch();
        connection.commit();

        for (TransferParams params : paramsBatch) {
            if (params != null) {
                double amount = roundTwoDecimals(params.getAmount());
                params.getFromAccount().decreaseBalance(amount);
                params.getToAccount().increaseBalance(amount);
            }
        }

        withdrawPs.close();
        depositPs.close();
        transactionPs.close();
        connection.close();
    }

    public double roundTwoDecimals(double num) {
        return Math.round(num * 100.0) / 100.0;
    }
}
