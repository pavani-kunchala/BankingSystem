import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDAO {

    private AccountDAO accountDAO;

    public TransactionDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    String getCurrentTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234567890";

    public boolean addTransaction(Transaction transaction, String accountNumber) {
        String query = "INSERT INTO transactions (account_number, transaction_type, amount, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, accountNumber);
            statement.setString(2, transaction.getTransactionType());
            statement.setDouble(3, transaction.getAmount());
            statement.setString(4, transaction.getTimestamp());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getAllTransactions(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_number = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String transactionType = resultSet.getString("transaction_type");
                double amount = resultSet.getDouble("amount");
                String timestamp = resultSet.getString("timestamp");
                Transaction transaction = new Transaction(transactionType, amount, timestamp);
                transactions.add(transaction);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return transactions;
    }

    public boolean updateTransaction(Transaction transaction, String accountNumber) {
        String query = "UPDATE transactions SET transaction_type = ?, amount = ?, timestamp = ? WHERE account_number = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, transaction.getTransactionType());
            statement.setDouble(2, transaction.getAmount());
            statement.setString(3, transaction.getTimestamp());
            statement.setString(4, accountNumber);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteTransactionsByAccount(String accountNumber) {
        String query = "DELETE FROM transactions WHERE account_number=?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, accountNumber);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


public boolean deposit(double amount, String accountNumber) {
    String query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
         PreparedStatement statement = conn.prepareStatement(query)) {

        statement.setDouble(1, amount);
        statement.setString(2, accountNumber);

        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            // Create deposit transaction record
            return addTransaction(new Transaction("Deposit", amount, getCurrentTimestamp()), accountNumber);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return false;
}

public boolean withdraw(double amount, String accountNumber) {
    // Check if the account has sufficient balance
    double currentBalance = accountDAO.getAccountBalance(accountNumber);
    if (currentBalance >= amount) {
        String query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setDouble(1, amount);
            statement.setString(2, accountNumber);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // Create withdrawal transaction record
                return addTransaction(new Transaction("Withdrawal", -amount, getCurrentTimestamp()), accountNumber);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    return false;
}


}