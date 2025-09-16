package org.example.dao;

import org.example.DatabaseConnection;
import org.example.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class TransactionDao {
    public void save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (amount, type, category, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, transaction.getAmount());
            stmt.setString(2, transaction.getType().name());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getDescription());
            stmt.executeUpdate();
        }

    }
    public Map<String, BigDecimal> getCategoryStats() throws SQLException {
        String sql = "SELECT category, SUM(amount) AS category_amount FROM transactions WHERE type = 'EXPENSE' GROUP BY category";

        try(Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            Map<String, BigDecimal> stats = new HashMap<>();

            while (rs.next()) {
                stats.put(rs.getString("category"), rs.getBigDecimal("category_amount"));
            }
            return stats;
        }
    }
    public List<Transaction> findAll() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Transaction transaction = new Transaction();
                transaction.setId(rs.getLong("id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
                transaction.setCategory(rs.getString("category"));
                transaction.setDescription(rs.getString("description"));
                transaction.setDate(rs.getTimestamp("date").toLocalDateTime());
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    public BigDecimal getBalance() throws SQLException {
        String sql = "SELECT SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END) as balance FROM transactions";
        try(Connection conn = DatabaseConnection.getConnection();Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            if(rs.next()){
                return rs.getBigDecimal("balance");
            }
        }
        return BigDecimal.ZERO;
    }
    public Transaction findById(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return mapRsToTransaction(rs);
            }
            return null;
        }
    }
    public void updateTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, type = ?, category = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, transaction.getAmount());
            stmt.setString(2, transaction.getType().name());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getDescription());
            stmt.setLong(5, transaction.getId());
            stmt.executeUpdate();
        }
    }
    public Transaction mapRsToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId((long) rs.getInt("id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
        transaction.setCategory(rs.getString("category"));
        transaction.setDate(rs.getTimestamp("date").toLocalDateTime());
        return transaction;
    }

    public void deleteTransaction(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public List<Transaction> getTransactionsByDate(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()){
                List<Transaction> transactions = new ArrayList<>();
                while (rs.next()){
                    Transaction transaction = new Transaction();
                    transaction.setDate(rs.getTimestamp("date").toLocalDateTime());
                    transaction.setDescription(rs.getString("description"));
                    transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setId(rs.getLong("id"));
                    transaction.setCategory(rs.getString("category"));
                    transactions.add(transaction);
                }
                return transactions;
            }
        }


    }
}

