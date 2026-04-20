package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        ModernTheme.prepareFrame(this, 980, 680);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Admin Command Center", "Manage questions, review user performance, and explore live analytics from one place."), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        ModernTheme.styleTabbedPane(tabs);
        tabs.addTab("Manage", createManageTab());
        tabs.addTab("Users", createUsersTab());
        tabs.addTab("Analytics", createAnalyticsTab());
        page.add(tabs, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        ModernTheme.styleDangerButton(logoutBtn);
        JPanel footer = ModernTheme.createSectionPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(logoutBtn);
        page.add(footer, BorderLayout.SOUTH);

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        add(page);
        setVisible(true);
    }

    private JPanel createManageTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));
        panel.setOpaque(false);
        panel.add(ModernTheme.createSubtleLabel("Update the quiz bank and keep content fresh."), BorderLayout.NORTH);
        panel.add(createActionCard("Question Bank", "Add, edit, or remove questions with a cleaner data management view.", "Open Manager", () -> new ManageQuestionsFrame()), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 18, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));
        panel.setOpaque(false);
        panel.add(createActionCard("User Performance", "Inspect how learners are doing across attempts, averages, and rankings.", "Open Users", () -> new AdminAnalyticsFrame()));
        panel.add(createActionCard("Top Performers", "Jump into the analytics dashboard to spot strong performers and trends.", "Open Rankings", () -> new AdminAnalyticsFrame()));
        return panel;
    }

    private JPanel createAnalyticsTab() {
        JPanel panel = new JPanel(new GridLayout(1, 1, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));
        panel.setOpaque(false);
        panel.add(createActionCard("System Analytics", "Explore questions by topic, difficulty mix, attempts, and system-wide performance.", "View Analytics", () -> new AdminAnalyticsFrame()));
        return panel;
    }

    private JPanel createActionCard(String title, String description, String buttonText, Runnable action) {
        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 18));

        JPanel content = new JPanel(new GridLayout(0, 1, 0, 10));
        content.setOpaque(false);
        content.add(ModernTheme.createSectionTitle(title));
        content.add(ModernTheme.createSubtleLabel("<html><body style='width:260px'>" + description + "</body></html>"));

        JButton button = new JButton(buttonText);
        ModernTheme.styleButton(button);
        button.addActionListener(e -> action.run());

        card.add(content, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);
        return card;
    }
}
