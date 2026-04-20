package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterFrame() {
        setTitle("User Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        root.setBackground(ModernTheme.BACKGROUND_COLOR);

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setBackground(ModernTheme.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(ModernTheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Fill in the details below to get started.");
        subtitleLabel.setFont(ModernTheme.LABEL_FONT);
        subtitleLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel formTitle = new JLabel("Register new user");
        formTitle.setFont(ModernTheme.HEADER_FONT);
        formTitle.setForeground(ModernTheme.TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formTitle);

        card.add(Box.createVerticalStrut(18));
        JLabel nameLabel = createFieldLabel("Full Name");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(6));
        nameField = new JTextField(22);
        configurePlainField(nameField);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameField);
        card.add(Box.createVerticalStrut(14));

        JLabel emailLabel = createFieldLabel("Email Address");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        emailField = new JTextField(22);
        configurePlainField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        JLabel passwordLabel = createFieldLabel("Password");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField(22);
        configurePlainField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton registerBtn = new JButton("Create Account");
        stylePlainButton(registerBtn, ModernTheme.PRIMARY_COLOR, Color.WHITE);
        JButton backBtn = new JButton("Back to Login");
        stylePlainButton(backBtn, ModernTheme.SECONDARY_COLOR, Color.WHITE);
        buttons.add(registerBtn);
        buttons.add(backBtn);
        card.add(buttons);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        add(root);

        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        pack();
        setMinimumSize(new Dimension(600, 560));
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
        setVisible(true);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernTheme.LABEL_FONT);
        label.setForeground(ModernTheme.TEXT_COLOR);
        return label;
    }

    private void configurePlainField(JTextField field) {
        field.setFont(ModernTheme.LABEL_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setEditable(true);
        field.setEnabled(true);
        field.setFocusable(true);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(340, 42));
    }

    private void stylePlainButton(JButton button, Color background, Color foreground) {
        button.setFont(ModernTheme.BUTTON_FONT);
        button.setText(button.getText());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(12, 16, 12, 16));
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'user')";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new LoginFrame();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Email already exists!");
        }
    }
}
