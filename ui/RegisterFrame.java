package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterFrame extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passwordField;

    public RegisterFrame() {

        setTitle("User Registration");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("📝 Register New User", JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        ModernTheme.stylePanel(panel);

        JLabel nameLabel = new JLabel("Name:");
        ModernTheme.styleLabel(nameLabel, 0);
        panel.add(nameLabel);
        nameField = new JTextField();
        ModernTheme.styleTextField(nameField);
        panel.add(nameField);

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

        JButton registerBtn = new JButton("Register");
        ModernTheme.styleButton(registerBtn);
        JButton backBtn = new JButton("Back to Login");
        ModernTheme.styleButton(backBtn);

        panel.add(registerBtn);
        panel.add(backBtn);

        add(panel, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    private void registerUser() {

        String name = nameField.getText();
        String email = emailField.getText();
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