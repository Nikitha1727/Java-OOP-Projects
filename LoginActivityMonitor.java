
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginActivityMonitor extends JFrame {

    JTextField usernameField;
    JComboBox<String> statusBox;
    JTable activityTable;
    DefaultTableModel tableModel;
    JLabel alertLabel;

    int failedAttempts = 0;

    LoginActivityMonitor() {

        setTitle("Login Activity Monitor");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(new Color(25, 25, 35));

        JLabel heading = new JLabel("LOGIN ACTIVITY MONITOR");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        heading.setHorizontalAlignment(JLabel.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        add(heading, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        topPanel.setBackground(new Color(35, 35, 48));

        usernameField = new JTextField(12);

        statusBox = new JComboBox<>(
            new String[]{"Successful", "Failed"}
        );

        JButton recordButton = new JButton("Record Login");
        JButton clearButton = new JButton("Clear History");

        topPanel.add(new JLabel("Username:"));
        topPanel.add(usernameField);
        topPanel.add(statusBox);
        topPanel.add(recordButton);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.PAGE_START);

        String[] columns = {
            "Username", "Status", "Date & Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activityTable = new JTable(tableModel);
        activityTable.setRowHeight(28);
        activityTable.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(activityTable);
        add(scrollPane, BorderLayout.CENTER);

        alertLabel = new JLabel("System Status: Monitoring login activity");
        alertLabel.setForeground(Color.GREEN);
        alertLabel.setHorizontalAlignment(JLabel.CENTER);
        alertLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        add(alertLabel, BorderLayout.SOUTH);

        recordButton.addActionListener(e -> recordLogin());

        clearButton.addActionListener(e -> clearHistory());

        setVisible(true);
    }

    void recordLogin() {

        String username = usernameField.getText().trim();
        String status = (String) statusBox.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a username."
            );
            return;
        }

        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        );

        tableModel.addRow(new Object[]{
            username,
            status,
            time
        });

        saveActivity(username, status, time);

        if (status.equals("Failed")) {

            failedAttempts++;

            if (failedAttempts >= 3) {
                alertLabel.setText(
                    "WARNING: 3 or more consecutive failed attempts!"
                );
                alertLabel.setForeground(Color.RED);
            } else {
                alertLabel.setText(
                    "Failed login attempt: " + failedAttempts
                );
                alertLabel.setForeground(Color.ORANGE);
            }

        } else {

            failedAttempts = 0;

            alertLabel.setText(
                "Login successful. Monitoring continues."
            );
            alertLabel.setForeground(Color.GREEN);
        }

        usernameField.setText("");
    }

    void saveActivity(String username, String status, String time) {

        try {
            FileWriter writer = new FileWriter(
                "login_activity.txt",
                true
            );

            writer.write(
                username + " | " + status + " | " + time
                + System.lineSeparator()
            );

            writer.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to save activity."
            );
        }
    }

    void clearHistory() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Clear the displayed activity history?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            tableModel.setRowCount(0);
            failedAttempts = 0;

            alertLabel.setText(
                "System Status: Monitoring login activity"
            );

            alertLabel.setForeground(Color.GREEN);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginActivityMonitor();
        });
    }
}
