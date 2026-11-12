
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureNotesManager extends JFrame {

    private static final String FILE_NAME = "secure_notes.dat";

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;

    private char[] masterPassword;
    private byte[] salt;

    private JTextArea noteArea;
    private JLabel statusLabel;

    private final Color BACKGROUND = new Color(25, 30, 45);
    private final Color PANEL = new Color(38, 45, 62);
    private final Color ACCENT = new Color(99, 102, 241);

    public SecureNotesManager() {

        setTitle("Secure Notes Manager");
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        showLoginScreen();
    }

    // LOGIN SCREEN
    private void showLoginScreen() {

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(80, 120, 60, 120));

        JLabel title = new JLabel("PRIVATE NOTES");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Your thoughts. Your privacy."
        );
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40));

        JButton unlockButton = createButton("Unlock Notes");

        JLabel message = new JLabel("Enter your master password");
        message.setForeground(Color.LIGHT_GRAY);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        unlockButton.addActionListener(e -> {

            char[] entered = passwordField.getPassword();

            if (entered.length < 8) {
                message.setText("Use at least 8 characters.");
                message.setForeground(Color.PINK);
                return;
            }

            try {

                if (new File(FILE_NAME).exists()) {

                    String saved = loadFile();

                    String[] parts = saved.split("\\n", 2);

                    if (parts.length != 2) {
                        throw new Exception("Invalid notes file.");
                    }

                    salt = Base64.getDecoder().decode(parts[0]);

                    masterPassword = entered.clone();

                    // Check the password by decrypting the notes.
                    decrypt(parts[1]);

                } else {

                    salt = new byte[16];
                    new SecureRandom().nextBytes(salt);

                    masterPassword = entered.clone();
                }

                java.util.Arrays.fill(entered, '\0');

                showNotesScreen();

            } catch (Exception ex) {

                java.util.Arrays.fill(entered, '\0');

                message.setText("Incorrect password or invalid file.");
                message.setForeground(Color.RED);
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(45));
        panel.add(message);
        panel.add(Box.createVerticalStrut(15));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));
        panel.add(unlockButton);

        add(panel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // NOTES DASHBOARD
    private void showNotesScreen() {

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel heading = new JLabel("My Private Notes");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Arial", Font.BOLD, 23));

        JButton lockButton = createButton("Lock");

        lockButton.addActionListener(e -> lockApplication());

        header.add(heading, BorderLayout.WEST);
        header.add(lockButton, BorderLayout.EAST);

        noteArea = new JTextArea();
        noteArea.setFont(new Font("Arial", Font.PLAIN, 16));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(noteArea);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(BACKGROUND);
        center.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel noteTitle = new JLabel("Write your private notes");
        noteTitle.setForeground(Color.WHITE);

        center.add(noteTitle, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BACKGROUND);
        footer.setBorder(new EmptyBorder(10, 25, 20, 25));

        JButton saveButton = createButton("Save Encrypted Notes");

        statusLabel = new JLabel("Your notes are private.");
        statusLabel.setForeground(Color.LIGHT_GRAY);

        saveButton.addActionListener(e -> saveNotes());

        footer.add(statusLabel, BorderLayout.NORTH);
        footer.add(saveButton, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Load existing notes
        if (new File(FILE_NAME).exists()) {

            try {

                String saved = loadFile();
                String[] parts = saved.split("\\n", 2);

                noteArea.setText(decrypt(parts[1]));

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                    this,
                    "Could not load notes."
                );
            }
        }

        revalidate();
        repaint();
    }

    // GENERATE ENCRYPTION KEY
    private SecretKey generateKey() throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
            masterPassword,
            salt,
            ITERATIONS,
            KEY_LENGTH
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return new SecretKeySpec(keyBytes, "AES");
    }

    // ENCRYPT NOTES
    private String encrypt(String text) throws Exception {

        SecretKey key = generateKey();

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted = cipher.doFinal(
            text.getBytes(StandardCharsets.UTF_8)
        );

        byte[] combined = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);

        System.arraycopy(
            encrypted, 0, combined, iv.length, encrypted.length
        );

        return Base64.getEncoder().encodeToString(combined);
    }

    // DECRYPT NOTES
    private String decrypt(String encryptedText) throws Exception {

        byte[] combined = Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[12];

        byte[] encrypted = new byte[combined.length - 12];

        System.arraycopy(combined, 0, iv, 0, 12);

        System.arraycopy(
            combined, 12, encrypted, 0, encrypted.length
        );

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(
            Cipher.DECRYPT_MODE,
            generateKey(),
            new GCMParameterSpec(128, iv)
        );

        byte[] original = cipher.doFinal(encrypted);

        return new String(original, StandardCharsets.UTF_8);
    }

    // SAVE NOTES
    private void saveNotes() {

        try {

            String encrypted = encrypt(noteArea.getText());

            String content =
                Base64.getEncoder().encodeToString(salt)
                + "\n" + encrypted;

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                writer.write(content);
            }

            statusLabel.setText("Notes encrypted and saved successfully.");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to save notes: " + e.getMessage()
            );
        }
    }

    // READ FILE
    private String loadFile() throws Exception {

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString().trim();
    }

    // LOCK APPLICATION
    private void lockApplication() {

        if (!noteArea.getText().isEmpty()) {

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Save changes before locking?",
                "Lock Notes",
                JOptionPane.YES_NO_CANCEL_OPTION
            );

            if (choice == JOptionPane.CANCEL_OPTION ||
                choice == JOptionPane.CLOSED_OPTION) {
                return;
            }

            if (choice == JOptionPane.YES_OPTION) {
                saveNotes();
            }
        }

        if (masterPassword != null) {
            java.util.Arrays.fill(masterPassword, '\0');
            masterPassword = null;
        }

        salt = null;

        showLoginScreen();
    }

    // BUTTON DESIGN
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SecureNotesManager app = new SecureNotesManager();

            app.setVisible(true);
        });
    }
}
