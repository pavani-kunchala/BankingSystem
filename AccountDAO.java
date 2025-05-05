import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountDAO {

    
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234567890";


        // Hash the password using SHA-256 before storing
 
       public String hashPassword(String password) {  
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b)); // Convert to hexadecimal format
        }
        return sb.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error hashing password", e);
    }
   }


       public boolean createAccount(AccountDetails account) {
        String query = "INSERT INTO accounts (account_number, account_holder_name, balance, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getAccountHolderName());
            statement.setDouble(3, account.getBalance());
            statement.setString(4, hashPassword(account.getPassword())); // Store hashed password

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
   
    public AccountDetails getAccount(String accountNumber) {
        String query = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AccountDetails(
                            resultSet.getString("account_number"),
                            resultSet.getString("account_holder_name"),
                            resultSet.getDouble("balance"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean updateAccount(AccountDetails account) {
        String query = "UPDATE accounts SET account_holder_name=?, balance=?, password=? WHERE account_number=?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, account.getAccountHolderName());
            statement.setDouble(2, account.getBalance());
           
            statement.setString(3, hashPassword(account.getPassword())); // Hash password before storing
            statement.setString(4, account.getAccountNumber());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccount(String accountNumber) {
        String query = "DELETE FROM accounts WHERE account_number=?";

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

public double getAccountBalance(String accountNumber) {
    String query = "SELECT balance FROM accounts WHERE account_number = ?";
    try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
         PreparedStatement statement = conn.prepareStatement(query)) {

        statement.setString(1, accountNumber);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return 0; // or throw an exception if the account is not found
}



    public List<AccountDetails> getAccountsAboveThreshold(double threshold) {
        List<AccountDetails> accountsAboveThreshold = new ArrayList<>();
        String query = "SELECT * FROM accounts WHERE balance > ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setDouble(1, threshold);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AccountDetails account = new AccountDetails(
                            resultSet.getString("account_number"),
                            resultSet.getString("account_holder_name"),
                            resultSet.getDouble("balance"),
                            resultSet.getString("password")
                    );
                    accountsAboveThreshold.add(account);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return accountsAboveThreshold;
    }

public double getTotalBalance() {
        String query = "SELECT SUM(balance) AS total_balance FROM accounts";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("total_balance");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0; // or throw an exception if unable to retrieve total balance
    }

}