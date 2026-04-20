package ui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private final int userId;

    public UserDashboard(int userId) {
        this.userId = userId;

        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        ModernTheme.prepareFrame(this, 980, 680);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Welcome back, learner", "User ID " + userId + "  |  Launch quizzes, review your progress, and track the leaderboard."), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        ModernTheme.styleTabbedPane(tabs);
        tabs.addTab("Quiz", createQuizTab());
        tabs.addTab("Results", createResultsTab());
        tabs.addTab("Leaderboard", createLeaderboardTab());
        tabs.addTab("Profile", createProfileTab());
        page.add(tabs, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        ModernTheme.styleDangerButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        JPanel footer = ModernTheme.createSectionPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(logoutBtn);
        page.add(footer, BorderLayout.SOUTH);

        add(page);
        setVisible(true);
    }

    private JPanel createQuizTab() {
        return createActionCard("Start Smart Quiz", "A timed quiz flow with adaptive difficulty, live progress, and clearer interaction states.", "Start Quiz", () -> {
            new QuizFrame(userId);
            dispose();
        });
    }

    private JPanel createResultsTab() {
        return createActionCard("View My Results", "Inspect previous attempts, export reports, and open personal analytics charts.", "Open Results", () -> new ResultFrame(userId));
    }

    private JPanel createLeaderboardTab() {
        return createActionCard("Leaderboard", "See the strongest performers and compare accuracy across the platform.", "Open Leaderboard", LeaderboardFrame::new);
    }

    private JPanel createProfileTab() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        JPanel card = ModernTheme.createCardPanel(new GridLayout(0, 1, 0, 10));
        card.add(ModernTheme.createSectionTitle("Profile Snapshot"));
        card.add(ModernTheme.createSubtleLabel("Current learner account"));
        card.add(ModernTheme.createMetricCard("User ID", String.valueOf(userId), ModernTheme.PRIMARY_COLOR));
        card.add(ModernTheme.createMetricCard("Mode", "Quiz Ready", ModernTheme.SUCCESS_COLOR));

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createActionCard(String title, String description, String buttonText, Runnable action) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 18));
        JPanel content = new JPanel(new GridLayout(0, 1, 0, 10));
        content.setOpaque(false);
        content.add(ModernTheme.createSectionTitle(title));
        content.add(ModernTheme.createSubtleLabel("<html><body style='width:300px'>" + description + "</body></html>"));

        JButton button = new JButton(buttonText);
        ModernTheme.styleButton(button);
        button.addActionListener(e -> action.run());

        card.add(content, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }
}
