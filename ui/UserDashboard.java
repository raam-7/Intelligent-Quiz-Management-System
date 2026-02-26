package ui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private int userId;

    public UserDashboard(int userId) {

        this.userId = userId;

        setTitle("User Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("👤 Welcome User - ID: " + userId, JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ModernTheme.BUTTON_FONT);

        // Quiz Tab
        JPanel quizTab = new JPanel(new GridBagLayout());
        quizTab.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton startQuizBtn = new JButton("🎯 Start Quiz");
        ModernTheme.styleButton(startQuizBtn);
        quizTab.add(startQuizBtn);
        tabs.addTab("Quiz", quizTab);

        // Results Tab
        JPanel resultsTab = new JPanel(new GridBagLayout());
        resultsTab.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton viewResultsBtn = new JButton("📊 View My Results");
        ModernTheme.styleButton(viewResultsBtn);
        resultsTab.add(viewResultsBtn);
        tabs.addTab("Results", resultsTab);

        // Leaderboard Tab
        JPanel lbTab = new JPanel(new GridBagLayout());
        lbTab.setBackground(ModernTheme.BACKGROUND_COLOR);
        JButton leaderboardBtn = new JButton("🏆 Leaderboard");
        ModernTheme.styleButton(leaderboardBtn);
        lbTab.add(leaderboardBtn);
        tabs.addTab("Leaderboard", lbTab);

        // Profile Tab
        JPanel profileTab = new JPanel(new GridLayout(3,1,10,10));
        profileTab.setBackground(ModernTheme.BACKGROUND_COLOR);
        JLabel info = new JLabel("User ID: " + userId);
        info.setFont(ModernTheme.LABEL_FONT);
        profileTab.add(info);
        tabs.addTab("Profile", profileTab);

        add(tabs, BorderLayout.CENTER);

        // Actions
        startQuizBtn.addActionListener(e -> {
            new QuizFrame(userId);
            dispose();
        });

        viewResultsBtn.addActionListener(e -> new ResultFrame(userId));

        leaderboardBtn.addActionListener(e -> new LeaderboardFrame());

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