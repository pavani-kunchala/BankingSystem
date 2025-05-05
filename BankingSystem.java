import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
 


class Transaction {
    private String transactionType;
    private double amount;
    private String timestamp;

    public Transaction(String transactionType, double amount, String timestamp) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

class AccountDetails {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private String password;

    public AccountDetails(String accountNumber, String accountHolderName, double balance, String password) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.password = password;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method to validate password
    public boolean validatePassword(String inputPassword) {
        return password.equals(inputPassword);
    }
}

public class BankingSystem extends JFrame {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO(accountDAO); // Pass accountDAO to TransactionDAO
    private AccountDetails loggedInAccount;

    // Components for main menu
    private JLabel titleLabel;
    private JButton createAccountButton;
    private JButton loginButton;
    private JButton deleteAccountButton;
    private JButton exitButton;

    // Components for banking operations menu
    private JLabel bankingTitleLabel;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton balanceInquiryButton;
    private JButton transactionHistoryButton;
    private JButton logoutButton;

    // Main menu panel
    private JPanel mainMenuPanel;

   
    public BankingSystem() {
        initializeUI();
 
    }

    private void initializeUI() {
        try {
            // Apply Nimbus look and feel for modern appearance
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

 

        setTitle("Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null); // Center the frame on the screen
        setLayout(new BorderLayout());

            // Disable frame resizing
        setResizable(false);

        // Initialize main menu components
        mainMenuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        titleLabel = new JLabel("Banking System Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));  
        titleLabel.setForeground(Color.BLUE);  
        createAccountButton = new JButton("Create Account");
        loginButton = new JButton("Login");
        deleteAccountButton = new JButton("Delete Account");
        exitButton = new JButton("Exit");

        mainMenuPanel.add(titleLabel);
        mainMenuPanel.add(createAccountButton);
        mainMenuPanel.add(loginButton);
        mainMenuPanel.add(deleteAccountButton);
        mainMenuPanel.add(exitButton);

        // Action listeners for main menu buttons
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAccount();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });

        // Add main menu panel to frame
        add(mainMenuPanel, BorderLayout.CENTER);
        setVisible(true);
    }

 
 public void createAccount() {
    while (true) {
        JTextField accountNumberField = new JTextField();
        JTextField accountHolderField = new JTextField();
        JTextField balanceField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Account Number:", accountNumberField,
                "Account Holder Name:", accountHolderField,
                "Initial Balance:", balanceField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String accountNumber = accountNumberField.getText().trim();
                String accountHolderName = accountHolderField.getText().trim();
                String balanceStr = balanceField.getText().trim();
                double initialBalance = Double.parseDouble(balanceStr);
                String password = new String(passwordField.getPassword());

                // Validate account number
                if (accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Account number cannot be empty.");
                    continue; // Show dialog again
                }

                // Validate account holder name
                if (accountHolderName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Account holder name cannot be empty.");
                    continue; // Show dialog again
                }

                // Validate initial balance
                if (balanceStr.isEmpty() || initialBalance < 0) {
                    JOptionPane.showMessageDialog(this, "Initial balance must be a valid positive number.");
                    continue; // Show dialog again
                }

                // Password validation
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                    continue; // Show dialog again
                }

                // Create new account object
                AccountDetails newAccount = new AccountDetails(accountNumber, accountHolderName, initialBalance, password);
                boolean success = accountDAO.createAccount(newAccount);
                if (success) {
                    // Add initial deposit transaction
                    boolean transactionSuccess = transactionDAO.addTransaction(new Transaction("Deposit", initialBalance, transactionDAO.getCurrentTimestamp()),                       accountNumber);
                    if (transactionSuccess) {
                        JOptionPane.showMessageDialog(this, "Account created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add initial deposit transaction.");
                    }
                    break; // Exit the loop on successful account creation
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create account.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input format. Please enter valid input values.");
            }
        } else {
            // User clicked Cancel or closed the dialog
            break; // Exit the loop
        }
    }
}

 
public void login() {
    boolean loggedIn = false;

    while (!loggedIn) {
        JTextField accountNumberField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Account Number:", accountNumberField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String accountNumber = accountNumberField.getText();
            String password = new String(passwordField.getPassword());

            AccountDetails account = accountDAO.getAccount(accountNumber);
            
            if (account != null && account.getPassword().equals(accountDAO.hashPassword(password))) {

                JOptionPane.showMessageDialog(this, "Login successful. Welcome " + account.getAccountHolderName());
                loggedInAccount = account;
                showBankingOperations();
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account number or password. Please try again.");
            }
        } else {
            // User clicked cancel or closed the dialog
            return;
        }
    }
}

public void deleteAccount() {
    boolean inputValidated = false;

    while (!inputValidated) {
        String accountNumber = JOptionPane.showInputDialog(this, "Enter the account number to delete:");
        
        // Check if user canceled the input dialog
        if (accountNumber == null) {
            return; // Exit method if user canceled
        }
        
        // Trim whitespace and check if account number is empty
        accountNumber = accountNumber.trim();
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account number.");
            continue; // Prompt user again if account number is empty
        }

        // Check if account exists
        AccountDetails account = accountDAO.getAccount(accountNumber);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Account does not exist.");
            continue; // Prompt user again if account does not exist
        }

        // Attempt to delete associated transactions
        boolean transactionsDeleted = transactionDAO.deleteTransactionsByAccount(accountNumber);
        if (transactionsDeleted) {
            JOptionPane.showMessageDialog(this, "Associated transactions deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No transactions made by this account.");
        }

        // Delete account
        boolean deleted = accountDAO.deleteAccount(accountNumber);
        if (deleted) {
            JOptionPane.showMessageDialog(this, "Account deleted successfully.");
            inputValidated = true; // Exit the loop since account deletion was successful
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete account.");
        }
    }
}



    private void showBankingOperations() {
        // Hide banking operations panel
        getContentPane().removeAll();

        // Initialize components for banking operations menu
        JPanel bankingMenuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        bankingTitleLabel = new JLabel("Account Services", SwingConstants.CENTER);
        bankingTitleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Setting font size and style
        bankingTitleLabel.setForeground(Color.RED); // Setting font color
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        balanceInquiryButton = new JButton("Balance Inquiry");
        transactionHistoryButton = new JButton("Transaction History");
        logoutButton = new JButton("Logout");

        bankingMenuPanel.add(bankingTitleLabel);
        bankingMenuPanel.add(depositButton);
        bankingMenuPanel.add(withdrawButton);
        bankingMenuPanel.add(balanceInquiryButton);
        bankingMenuPanel.add(transactionHistoryButton);
        bankingMenuPanel.add(logoutButton);

        // Action listeners for banking operations menu buttons
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDeposit();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performWithdrawal();
            }
        });

        balanceInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBalanceInquiry();
            }
        });

        transactionHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTransactionHistory();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Add banking menu panel to frame
        add(bankingMenuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
 

private void performDeposit() {
    boolean validAmountEntered = false;

    while (!validAmountEntered) {
        try {
            String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:");
            if (amountStr == null) {
                // User clicked cancel or closed the dialog
                return;
            }
            
            double depositAmount = Double.parseDouble(amountStr);
            if (depositAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be greater than zero.");
            } else {
                 
                    boolean depositSuccess = transactionDAO.deposit(depositAmount, loggedInAccount.getAccountNumber());
                    if (depositSuccess) {
                        JOptionPane.showMessageDialog(this, "Deposit successful.");
                        validAmountEntered = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to process deposit.");
                    } 
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please enter a valid number.");
        }
    }
}

 

private void performWithdrawal() {
    boolean validAmountEntered = false;

    while (!validAmountEntered) {
        try {
            String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
            if (amountStr == null) {
                // User clicked cancel or closed the dialog
                return;
            }
            
            double withdrawAmount = Double.parseDouble(amountStr);
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Withdrawal amount must be greater than zero.");
            } else {
                 
                    boolean withdrawSuccess = transactionDAO.withdraw(withdrawAmount, loggedInAccount.getAccountNumber());
                    if (withdrawSuccess) {
                        JOptionPane.showMessageDialog(this, "Withdrawal successful.");
                        validAmountEntered = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to process withdrawal. Insufficient balance.");
                    }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please enter a valid number.");
        }
    }
}

    private void showBalanceInquiry() {
        double balance = accountDAO.getAccountBalance(loggedInAccount.getAccountNumber());
        JOptionPane.showMessageDialog(this, "Current balance: $" + String.format("%.2f", balance));
    }
 

private void showTransactionHistory() {
    List<Transaction> transactions = transactionDAO.getAllTransactions(loggedInAccount.getAccountNumber());
    
    // Check if there are no transactions
    if (transactions.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No transactions found.");
        return;
    }
    
    StringBuilder transactionDetails = new StringBuilder("Transaction History:\n");

 /*   // Add initial balance transaction first
    transactionDetails.append("Initial Balance of $")
                     .append(String.format("%.2f", loggedInAccount.getBalance()))
                     .append(" at Account Creation\n");*/

    // Append other transactions
    for (Transaction transaction : transactions) {
        transactionDetails.append(transaction.getTransactionType())
                .append(" of $").append(String.format("%.2f", transaction.getAmount()))
                .append(" at ").append(transaction.getTimestamp())
                .append("\n");
    }
    
    JOptionPane.showMessageDialog(this, transactionDetails.toString());
}


    private void logout() {
        JOptionPane.showMessageDialog(this, "Logout successful.");
        // Clear logged in account and show main menu again
        loggedInAccount = null;
        getContentPane().removeAll();
        add(mainMenuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void exitApplication() {
        JOptionPane.showMessageDialog(this, "Thank you for using our Banking System!");
        System.exit(0);
    }

 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BankingSystem();
            }
        });
    }
}
 