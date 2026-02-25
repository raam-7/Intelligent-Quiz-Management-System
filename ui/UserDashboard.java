package ui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private int userId;

    public UserDashboard(int userId) {

        this.userId = userId;

        setTitle("User Dashboard");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        JLabel title = new JLabel("👤 Welcome User - ID: " + userId, JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        ModernTheme.stylePanel(panel);

        JButton startQuizBtn = new JButton("🎯 Start Quiz");
        ModernTheme.styleButton(startQuizBtn);
        JButton viewResultsBtn = new JButton("📊 View Results");
        ModernTheme.styleButton(viewResultsBtn);
        JButton leaderboardBtn = new JButton("🏆 Leaderboard");
        ModernTheme.styleButton(leaderboardBtn);
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(ModernTheme.DANGER_COLOR);
        ModernTheme.styleButton(logoutBtn);

        panel.add(startQuizBtn);
        panel.add(viewResultsBtn);
        panel.add(leaderboardBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        // ✅ Start Quiz
        startQuizBtn.addActionListener(e -> {
            new QuizFrame(userId);
            dispose();
        });

        // ✅ View Results (moved below creation)
        viewResultsBtn.addActionListener(e -> {
            new ResultFrame(userId);
        });

        // ✅ Leaderboard
        leaderboardBtn.addActionListener(e -> {
            new LeaderboardFrame();
        });

        // ✅ Logout
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}