package services;

import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CustomerService {
    public Customer createCustomer(Customer customer) throws SQLException {
        String sqlQuery = "INSERT INTO customer (name, email, phone) VALUES (?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getEmail());
        ps.setString(3, customer.getPhoneNumber());
        ps.executeUpdate();

        Customer customerWithId = null;
        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            customerWithId = new Customer(
                    rs.getInt(1),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getEmail()
            );
        } else {
            rs.close();
            ps.close();
            connection.close();
            throw new SQLException("Failed to retrieve generated customer ID.");
        }

        rs.close();
        ps.close();
        connection.close();

        return customerWithId;
    }
}
