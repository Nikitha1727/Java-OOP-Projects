
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileIntegrityChecker extends JFrame {

    private File selectedFile;

    private JLabel fileLabel;
    private JLabel resultLabel;

    private JTextField hashField;
    private JTextArea outputArea;

    public FileIntegrityChecker() {

        setTitle("File Integrity Checker");
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createUI();
    }

    // CREATE USER INTERFACE
    private void createUI() {

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(25, 30, 45));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("FILE INTEGRITY CHECKER");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Check whether your files have been modified"
        );
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton chooseButton = createButton("Choose File");
        JButton calculateButton = createButton("Calculate SHA-256");
        JButton verifyButton = createButton("Verify Integrity");

        fileLabel = new JLabel("No file selected");
        fileLabel.setForeground(Color.LIGHT_GRAY);
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultLabel = new JLabel("Waiting for file...");
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hashField = new JTextField();
        hashField.setMaximumSize(new Dimension(550, 40));
        hashField.setFont(new Font("Arial", Font.PLAIN, 13));

        hashField.setToolTipText("Enter the expected SHA-256 hash");

        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setMaximumSize(new Dimension(550, 110));

        chooseButton.addActionListener(e -> chooseFile());

        calculateButton.addActionListener(e -> calculateHash());

        verifyButton.addActionListener(e -> verifyIntegrity());

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitle);

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(chooseButton);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(fileLabel);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(calculateButton);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel hashTitle = new JLabel("Expected SHA-256 Hash:");
        hashTitle.setForeground(Color.WHITE);
        hashTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(hashTitle);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(hashField);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(verifyButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(resultLabel);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    // CREATE BUTTON
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(99, 102, 241));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setMaximumSize(new Dimension(250, 40));

        return button;
    }

    // SELECT FILE
    private void chooseFile() {

        JFileChooser chooser = new JFileChooser();

        int choice = chooser.showOpenDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            fileLabel.setText(
                "Selected: " + selectedFile.getName()
            );

            outputArea.setText("");
            resultLabel.setText("File selected successfully");
            resultLabel.setForeground(Color.YELLOW);
        }
    }

    // CALCULATE SHA-256 HASH
    private String calculateSHA256(File file) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (FileInputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {

                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : hashBytes) {

            hexString.append(
                String.format("%02x", b & 0xff)
            );
        }

        return hexString.toString();
    }

    // DISPLAY HASH
    private void calculateHash() {

        if (selectedFile == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a file first!"
            );

            return;
        }

        try {

            String hash = calculateSHA256(selectedFile);

            outputArea.setText(
                "FILE NAME:\n" + selectedFile.getName()
                + "\n\nSHA-256 HASH:\n" + hash
            );

            resultLabel.setText("Hash calculated successfully");
            resultLabel.setForeground(Color.GREEN);

        } catch (Exception e) {

            showError(e);
        }
    }

    // VERIFY FILE INTEGRITY
    private void verifyIntegrity() {

        if (selectedFile == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a file first!"
            );

            return;
        }

        String expectedHash = hashField.getText()
                                       .trim()
                                       .toLowerCase();

        if (!expectedHash.matches("[0-9a-f]{64}")) {

            JOptionPane.showMessageDialog(
                this,
                "Enter a valid 64-character SHA-256 hash."
            );

            return;
        }

        try {

            String actualHash = calculateSHA256(selectedFile);

            if (actualHash.equals(expectedHash)) {

                resultLabel.setText("FILE IS INTEGRITY VERIFIED");
                resultLabel.setForeground(Color.GREEN);

                outputArea.setText(
                    "SUCCESS!\n\n"
                    + "The calculated hash matches the expected hash.\n"
                    + "No difference was detected."
                );

            } else {

                resultLabel.setText("FILE INTEGRITY CHECK FAILED");
                resultLabel.setForeground(Color.RED);

                outputArea.setText(
                    "WARNING!\n\n"
                    + "The calculated hash does not match.\n"
                    + "The file may have been modified or the expected "
                    + "hash may be incorrect."
                );
            }

        } catch (Exception e) {

            showError(e);
        }
    }

    // ERROR HANDLING
    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
            this,
            "Error: " + e.getMessage(),
            "Application Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            FileIntegrityChecker app =
                new FileIntegrityChecker();

            app.setVisible(true);
        });
    }
}
