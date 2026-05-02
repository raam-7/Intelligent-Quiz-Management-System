package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Intelligent Quiz Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 900, 560);

        JPanel root = ModernTheme.createPagePanel(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel hero = ModernTheme.createAuthHeroPanel(
                "Intelligent Quiz Hub",
                "Adaptive quizzes, progress insights, and admin tools in one focused workspace.",
                "Sign in as an admin to manage questions and analytics, or as a learner to start a filtered quiz session."
        );
        gbc.gridx = 0;
        gbc.weightx = 0.46;
        gbc.insets = new Insets(0, 0, 0, 18);
        root.add(hero, gbc);

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 20));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Welcome back");
        formTitle.setFont(ModernTheme.HEADER_FONT);
        formTitle.setForeground(ModernTheme.TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formTitle);

        JLabel formSubtitle = new JLabel("Use your registered email and password.");
        formSubtitle.setFont(ModernTheme.LABEL_FONT);
        formSubtitle.setForeground(ModernTheme.MUTED_TEXT_COLOR);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(8));
        card.add(formSubtitle);
        card.add(Box.createVerticalStrut(18));

        JLabel emailLabel = createFieldLabel("Email");
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

        JButton loginBtn = new JButton("Login");
        ModernTheme.styleButton(loginBtn);
        JButton registerBtn = new JButton("Register");
        ModernTheme.styleSecondaryButton(registerBtn);
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        card.add(buttons);

        gbc.gridx = 1;
        gbc.weightx = 0.54;
        gbc.insets = new Insets(0, 0, 0, 0);
        root.add(card, gbc);
        add(ModernTheme.createScrollPane(root));

        loginBtn.addActionListener(e -> loginUser());
        registerBtn.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });

        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> emailField.requestFocusInWindow());
        setVisible(true);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernTheme.LABEL_FONT);
        label.setForeground(ModernTheme.TEXT_COLOR);
        return label;
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                JOptionPane.showMessageDialog(this, "Login Successful!");
                if ("admin".equals(role)) {
                    new AdminDashboard();
                } else {
                    new UserDashboard(userId);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to login right now.");
        }
    }
}
