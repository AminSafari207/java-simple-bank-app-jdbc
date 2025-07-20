package services;

import model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public double roundTwoDecimals(double num) {
        return Math.round(num * 100.0) / 100.0;
    }
}
