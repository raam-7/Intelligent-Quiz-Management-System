package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("🔐 Admin Panel", JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ModernTheme.BUTTON_FONT);

        // Manage Questions Tab
        JPanel managePanel = new JPanel();
        managePanel.setLayout(new GridBagLayout());
        managePanel.setBackground(ModernTheme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JButton manageQuestionsBtn = new JButton("❓ Open Manage Questions");
        ModernTheme.styleButton(manageQuestionsBtn);
        gbc.gridx = 0; gbc.gridy = 0;
        managePanel.add(manageQuestionsBtn, gbc);

        tabs.addTab("Manage", managePanel);

        // Users Tab
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBackground(ModernTheme.BACKGROUND_COLOR);
        JPanel usersTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usersTop.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton openUsersBtn = new JButton("👥 Open User Performance");
        ModernTheme.styleButton(openUsersBtn);
        usersTop.add(openUsersBtn);
        usersPanel.add(usersTop, BorderLayout.NORTH);
        tabs.addTab("Users", usersPanel);

        // Analytics Tab
        JPanel analyticsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        analyticsPanel.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton viewReportsBtn = new JButton("📊 Open Analytics");
        ModernTheme.styleButton(viewReportsBtn);
        analyticsPanel.add(viewReportsBtn);
        tabs.addTab("Analytics", analyticsPanel);

        add(tabs, BorderLayout.CENTER);

        // Actions
        manageQuestionsBtn.addActionListener(e -> new ManageQuestionsFrame());
        openUsersBtn.addActionListener(e -> new AdminAnalyticsFrame());
        viewReportsBtn.addActionListener(e -> new AdminAnalyticsFrame());

        // Bottom Logout
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(ModernTheme.DANGER_COLOR);
        ModernTheme.styleButton(logoutBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}