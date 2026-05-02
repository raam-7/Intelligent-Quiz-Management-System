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
        ModernTheme.prepareFrame(this, 940, 620);

        JPanel root = ModernTheme.createPagePanel(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel hero = ModernTheme.createAuthHeroPanel(
                "Create your learner account",
                "Register once, then use the dashboard to start quizzes, track results, and compare progress.",
                "New accounts are created as learners. Admin access can still be reached from the login screen with an admin account."
        );
        gbc.gridx = 0;
        gbc.weightx = 0.44;
        gbc.insets = new Insets(0, 0, 0, 18);
        root.add(hero, gbc);

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 20));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Register new user");
        formTitle.setFont(ModernTheme.HEADER_FONT);
        formTitle.setForeground(ModernTheme.TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formTitle);
        card.add(Box.createVerticalStrut(8));
        JLabel formSubtitle = ModernTheme.createSubtleLabel("Enter your details to unlock quiz sessions.");
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formSubtitle);

        card.add(Box.createVerticalStrut(18));
        JLabel nameLabel = createFieldLabel("Full Name");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(6));
        nameField = new JTextField(22);
        ModernTheme.styleTextField(nameField);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameField);
        card.add(Box.createVerticalStrut(14));

        JLabel emailLabel = createFieldLabel("Email Address");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        emailField = new JTextField(22);
        ModernTheme.styleTextField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        JLabel passwordLabel = createFieldLabel("Password");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField(22);
        ModernTheme.stylePasswordField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton registerBtn = new JButton("Create Account");
        ModernTheme.styleButton(registerBtn);
        JButton backBtn = new JButton("Back to Login");
        ModernTheme.styleSecondaryButton(backBtn);
        buttons.add(registerBtn);
        buttons.add(backBtn);
        card.add(buttons);

        gbc.gridx = 1;
        gbc.weightx = 0.56;
        gbc.insets = new Insets(0, 0, 0, 0);
        root.add(card, gbc);
        add(ModernTheme.createScrollPane(root));

        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setMinimumSize(new Dimension(700, 520));
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
