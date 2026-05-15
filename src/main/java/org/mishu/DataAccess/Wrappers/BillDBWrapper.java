package org.mishu.DataAccess.Wrappers;

import org.mishu.DataAccess.ConnectionFactory;
import org.mishu.Model.Bill;
import org.mishu.Utils.QuerySettings;

import java.sql.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDBWrapper extends DBWrapper<Bill> {

    private static final Logger LOGGER = Logger.getLogger(BillDBWrapper.class.getName());

    public BillDBWrapper(Class<Bill> type) {
        super(type);
    }

    public int insertBill(int orderId, int clientId, int productId,
                          int qty, double total) {
        String query = "INSERT INTO Log (orderId, clientId, productId, quantity, total)" +
                " VALUES (?,?,?,?,?)";
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, orderId);
            statement.setInt(2, clientId);
            statement.setInt(3, productId);
            statement.setInt(4, qty);
            statement.setDouble(5, total);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDBWrapper: insertBill", e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return -1;
    }

    public Bill findByOrderId(int orderId) {
        String query = "SELECT * FROM Log WHERE orderId = ?";
        List<Bill> bills = get(
                orderId,
                "OrderId",
                query,
                new QuerySettings(false, false));

        if (bills == null || bills.isEmpty()) {
            throw new NoSuchElementException("Order with id=" + orderId + " does not " +
                    "exist for a bill to be fetched");
        }

        return bills.getFirst();
    }

    public void deleteByOrderId(int orderId) {
        String query = "DELETE FROM Log WHERE orderId = ?";
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDBWrapper: deleteByOrderId", e);
        } finally {
            ConnectionFactory.close(stmt);
            ConnectionFactory.close(conn);
        }
    }

}

