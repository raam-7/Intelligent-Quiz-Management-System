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
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("🎓 Quiz Management System", JLabel.CENTER);
        ModernTheme.styleLabel(title, 1); // Title style
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        ModernTheme.stylePanel(panel);

        JLabel emailLabel = new JLabel("Email:");
        ModernTheme.styleLabel(emailLabel, 0);
        panel.add(emailLabel);
        emailField = new JTextField();
        ModernTheme.styleTextField(emailField);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        ModernTheme.styleLabel(passwordLabel, 0);
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        ModernTheme.stylePasswordField(passwordField);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        ModernTheme.styleButton(loginBtn);
        JButton registerBtn = new JButton("Register");
        ModernTheme.styleButton(registerBtn);

        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel, BorderLayout.CENTER);

        // 🔥 Button Actions
        loginBtn.addActionListener(e -> loginUser());

        registerBtn.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });

        setVisible(true);
    }

    private void loginUser() {

        String email = emailField.getText();
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

                if (role.equals("admin")) {
                    new AdminDashboard();  // We'll create this next
                } else {
                    new UserDashboard(userId); // We'll create this next
                }

                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}