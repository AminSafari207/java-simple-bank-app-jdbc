package services.jdbc;

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

    public void updateCustomer(Customer oldCustomer, Map<String, String> updates) throws SQLException {
        if (updates.isEmpty()) return;

        String sqlQuery = "UPDATE customer SET ";
        int updateCount = 0;

        for (String key: updates.keySet()) {
            sqlQuery += key + " = ?";
            updateCount++;

            if (updateCount < updates.size()) sqlQuery += ", ";
        }

        sqlQuery += " WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        int idIndex = 1;

        for (String key: updates.keySet()) {
            String value = updates.get(key);

            ps.setObject(idIndex++, value);

            if (key.equals("name")) oldCustomer.setName(value);
            if (key.equals("email")) oldCustomer.setEmail(value);
            if (key.equals("phoneNumber")) oldCustomer.setPhoneNumber(value);
        }

        ps.setInt(idIndex, oldCustomer.getId());
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sqlQuery = "DELETE FROM customer WHERE id = ?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sqlQuery);

        ps.setInt(1, customerId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }
}
