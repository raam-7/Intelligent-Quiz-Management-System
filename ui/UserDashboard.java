package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 18));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(ModernTheme.createSectionTitle("Start Smart Quiz"));
        content.add(Box.createVerticalStrut(8));
        content.add(ModernTheme.createSubtleLabel("Pick a topic, difficulty, and number of questions before starting."));
        content.add(Box.createVerticalStrut(18));

        JComboBox<String> topicBox = new JComboBox<>(loadFilterValues("SELECT DISTINCT topic FROM questions WHERE topic IS NOT NULL AND topic <> '' ORDER BY topic", "All Topics"));
        JComboBox<String> difficultyBox = new JComboBox<>(loadFilterValues("SELECT DISTINCT difficulty FROM questions WHERE difficulty IS NOT NULL AND difficulty <> '' ORDER BY difficulty", "All Difficulties"));
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));

        topicBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        difficultyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        countSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        content.add(createFilterField("Topic Category", topicBox));
        content.add(Box.createVerticalStrut(12));
        content.add(createFilterField("Difficulty", difficultyBox));
        content.add(Box.createVerticalStrut(12));
        content.add(createFilterField("Question Count", countSpinner));

        JButton startQuizBtn = new JButton("Start Quiz");
        ModernTheme.styleButton(startQuizBtn);
        startQuizBtn.addActionListener(e -> {
            String topic = String.valueOf(topicBox.getSelectedItem());
            String difficulty = String.valueOf(difficultyBox.getSelectedItem());
            int questionCount = (Integer) countSpinner.getValue();
            new QuizFrame(userId, topic, difficulty, questionCount);
            dispose();
        });

        card.add(content, BorderLayout.CENTER);
        card.add(startQuizBtn, BorderLayout.SOUTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
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

    private JPanel createFilterField(String labelText, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel label = ModernTheme.createSubtleLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private String[] loadFilterValues(String sql, String allOption) {
        List<String> values = new ArrayList<>();
        values.add(allOption);

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values.toArray(new String[0]);
    }
}
