package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("🔐 Admin Panel", JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        ModernTheme.stylePanel(panel);

        JButton manageQuestionsBtn = new JButton("❓ Manage Questions");
        ModernTheme.styleButton(manageQuestionsBtn);
        
        JButton viewReportsBtn = new JButton("📊 View Advanced Analytics");
        ModernTheme.styleButton(viewReportsBtn);
        
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(ModernTheme.DANGER_COLOR);
        ModernTheme.styleButton(logoutBtn);

        panel.add(manageQuestionsBtn);
        panel.add(viewReportsBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        // ✅ Connect Manage Questions
        manageQuestionsBtn.addActionListener(e -> {
            new ManageQuestionsFrame();
        });

        // ✅ View Advanced Analytics
        viewReportsBtn.addActionListener(e -> {
            new AdminAnalyticsFrame();
        });

        // ✅ Logout
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}