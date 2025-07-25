package services.jdbc;

import model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TransactionService {
    public static void createTransaction(Transaction transaction) throws SQLException {
        if (transaction == null) return;

        String sqlQuery = "INSERT INTO transaction (account_id, type, amount, date, details) VALUES (?, ?, ?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        ps.setInt(1, transaction.getAccountId());
        ps.setString(2, transaction.getType());
        ps.setDouble(3, transaction.getAmount());
        ps.setTimestamp(4, Timestamp.valueOf(transaction.getDate()));
        ps.setString(5, transaction.getDetails());
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    public static void createTransaction(List<Transaction> transactions) throws SQLException {
        if (transactions == null || transactions.isEmpty()) return;

        String sqlQuery = "INSERT INTO transaction (account_id, type, amount, date, details) VALUES (?, ?, ?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        connection.setAutoCommit(false);

        for (Transaction transaction: transactions) {
            ps.setInt(1, transaction.getAccountId());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setTimestamp(4, Timestamp.valueOf(transaction.getDate()));
            ps.setString(5, transaction.getDetails());
            ps.addBatch();
        }

        ps.executeBatch();
        connection.commit();

        ps.close();
        connection.close();
    }
}
