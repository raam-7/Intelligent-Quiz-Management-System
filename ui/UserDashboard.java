package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {

    private static final Color PAGE_TOP = new Color(4, 10, 26);
    private static final Color PAGE_BOTTOM = new Color(8, 18, 42);
    private static final Color CARD_TOP = new Color(18, 32, 58, 236);
    private static final Color CARD_BOTTOM = new Color(7, 18, 40, 238);
    private static final Color CARD_BORDER = new Color(52, 77, 118);
    private static final Color ACTIVE_BLUE = new Color(47, 158, 255);
    private static final Color FIELD_BG = new Color(25, 39, 63);
    private static final Color FIELD_BORDER = new Color(80, 108, 150);
    private static final Color TEXT_MAIN = new Color(248, 251, 255);
    private static final Color TEXT_MUTED = new Color(198, 213, 238);
    private static final Color VIOLET = new Color(142, 92, 255);

    private final int userId;

    public UserDashboard(int userId) {
        this.userId = userId;

        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1180, 780);

        JPanel page = new DashboardPagePanel(new BorderLayout(0, 18));
        page.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        page.add(createDashboardHero(), BorderLayout.NORTH);

        JPanel center = ModernTheme.createSectionPanel(new BorderLayout(0, 26));
        center.add(createOverviewStrip(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        styleDashboardTabs(tabs);
        tabs.addTab("Quiz", createQuizTab());
        tabs.addTab("Results", createResultsTab());
        tabs.addTab("Leaderboard", createLeaderboardTab());
        tabs.addTab("Profile", createProfileTab());
        tabs.setTabComponentAt(0, createTabLabel("\u25B7", "Quiz", true));
        tabs.setTabComponentAt(1, createTabLabel("\u25A5", "Results", false));
        tabs.setTabComponentAt(2, createTabLabel("\u2655", "Leaderboard", false));
        tabs.setTabComponentAt(3, createTabLabel("\u263A", "Profile", false));
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component tab = tabs.getTabComponentAt(i);
                if (tab instanceof JLabel) {
                    tab.setForeground(i == tabs.getSelectedIndex() ? ACTIVE_BLUE : TEXT_MUTED);
                    tab.repaint();
                }
            }
        });
        center.add(tabs, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        ModernTheme.styleDangerButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        JPanel footer = ModernTheme.createSectionPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(logoutBtn);
        page.add(footer, BorderLayout.SOUTH);

        add(ModernTheme.createScrollPane(page));
        setVisible(true);
    }

    private JPanel createDashboardHero() {
        JPanel hero = new DashboardHeroPanel(new BorderLayout(0, 8));
        hero.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(80, 144, 230), 1, 8),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Welcome back, learner!  \uD83D\uDC4B");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel("User ID " + userId + "  |  Launch quizzes, review your progress, and track the leaderboard.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(232, 241, 255));

        hero.add(title, BorderLayout.NORTH);
        hero.add(subtitle, BorderLayout.CENTER);
        return hero;
    }

    private JPanel createOverviewStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 4, 18, 0));
        strip.setOpaque(false);
        strip.add(createStatusCard("\u2713", "Session", "Ready", "You're all set to go!", ModernTheme.SUCCESS_COLOR));
        strip.add(createStatusCard("\uD83E\uDDE0", "Quiz Mode", "Adaptive", "Questions adjust to you", VIOLET));
        strip.add(createStatusCard("\u25F7", "Timer", "15s", "Per question", ModernTheme.WARNING_COLOR));
        strip.add(createStatusCard("\u263A", "Account", "#" + userId, "Your account", ModernTheme.INFO_COLOR));
        return strip;
    }

    private JPanel createQuizTab() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel card = createGlassCard(new BorderLayout(0, 24), 26, 28, 26, 28);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout(18, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.add(createSquareIcon("\uD83D\uDE80"), BorderLayout.WEST);

        JPanel copy = new JPanel(new GridLayout(0, 1, 0, 5));
        copy.setOpaque(false);
        JLabel title = new JLabel("Start Smart Quiz");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Pick a topic, difficulty, and number of questions before starting.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitle.setForeground(TEXT_MUTED);
        copy.add(title);
        copy.add(subtitle);
        titleRow.add(copy, BorderLayout.CENTER);
        content.add(titleRow);
        content.add(Box.createVerticalStrut(24));

        JComboBox<String> topicBox = new JComboBox<>(loadFilterValues("SELECT DISTINCT topic FROM questions WHERE topic IS NOT NULL AND topic <> '' ORDER BY topic", "All Topics"));
        JComboBox<String> difficultyBox = new JComboBox<>(loadFilterValues("SELECT DISTINCT difficulty FROM questions WHERE difficulty IS NOT NULL AND difficulty <> '' ORDER BY difficulty", "All Difficulties"));
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        styleDashboardComboBox(topicBox);
        styleDashboardComboBox(difficultyBox);
        styleDashboardSpinner(countSpinner);

        topicBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        difficultyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        countSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        content.add(createFilterField("Topic Category", topicBox));
        content.add(Box.createVerticalStrut(18));
        content.add(createFilterField("Difficulty", difficultyBox));
        content.add(Box.createVerticalStrut(18));
        content.add(createFilterField("Question Count", countSpinner));

        JButton startQuizBtn = new GradientButton("\u25B6  Start Quiz", new Color(42, 113, 255), new Color(132, 73, 255));
        startQuizBtn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        startQuizBtn.setForeground(Color.WHITE);
        startQuizBtn.setFocusPainted(false);
        startQuizBtn.setBorderPainted(false);
        startQuizBtn.setContentAreaFilled(false);
        startQuizBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startQuizBtn.setPreferredSize(new Dimension(350, 58));
        startQuizBtn.addActionListener(e -> {
            String topic = String.valueOf(topicBox.getSelectedItem());
            String difficulty = String.valueOf(difficultyBox.getSelectedItem());
            int questionCount = (Integer) countSpinner.getValue();
            new QuizFrame(userId, topic, difficulty, questionCount);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startQuizBtn);

        card.add(content, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
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
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel card = createGlassCard(new GridLayout(0, 1, 0, 12), 24, 26, 24, 26);
        card.add(ModernTheme.createSectionTitle("Profile Snapshot"));
        card.add(ModernTheme.createSubtleLabel("Current learner account"));
        card.add(createStatusCard("\u263A", "User ID", String.valueOf(userId), "Personal learner profile", VIOLET));
        card.add(createStatusCard("\u2713", "Mode", "Quiz Ready", "Prepared for the next attempt", ModernTheme.SUCCESS_COLOR));

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createActionCard(String title, String description, String buttonText, Runnable action) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel card = createGlassCard(new BorderLayout(0, 18), 26, 28, 26, 28);
        JPanel content = new JPanel(new GridLayout(0, 1, 0, 10));
        content.setOpaque(false);
        content.add(ModernTheme.createSectionTitle(title));
        content.add(ModernTheme.createSubtleLabel("<html><body style='width:300px'>" + description + "</body></html>"));

        JButton button = new GradientButton(buttonText, new Color(42, 113, 255), VIOLET);
        button.setFont(ModernTheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());

        card.add(content, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFilterField(String labelText, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        label.setForeground(TEXT_MAIN);
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusCard(String icon, String label, String value, String helper, Color accent) {
        JPanel card = createGlassCard(new BorderLayout(16, 0), 22, 20, 22, 20);
        card.add(createCircleIcon(icon, accent), BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(0, 1, 0, 4));
        text.setOpaque(false);
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelText.setForeground(TEXT_MUTED);
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        valueText.setForeground(accent);
        JLabel helperText = new JLabel(helper);
        helperText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        helperText.setForeground(TEXT_MUTED);

        text.add(labelText);
        text.add(valueText);
        text.add(helperText);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createGlassCard(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel panel = new GlassPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        ));
        return panel;
    }

    private JLabel createCircleIcon(String symbol, Color accent) {
        JLabel label = new IconBadge(symbol, accent, true);
        label.setPreferredSize(new Dimension(58, 58));
        label.setMinimumSize(new Dimension(58, 58));
        return label;
    }

    private JLabel createSquareIcon(String symbol) {
        JLabel label = new IconBadge(symbol, VIOLET, false);
        label.setPreferredSize(new Dimension(58, 58));
        label.setMinimumSize(new Dimension(58, 58));
        return label;
    }

    private JLabel createTabLabel(String icon, String text, boolean selected) {
        JLabel label = new JLabel(icon + "  " + text);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        label.setForeground(selected ? ACTIVE_BLUE : TEXT_MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return label;
    }

    private void styleDashboardTabs(JTabbedPane tabs) {
        ModernTheme.styleTabbedPane(tabs);
        tabs.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        tabs.setBackground(PAGE_BOTTOM);
        tabs.setForeground(TEXT_MUTED);
        tabs.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
    }

    private void styleDashboardComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        comboBox.setPreferredSize(new Dimension(240, 56));
        comboBox.setBackground(FIELD_BG);
        comboBox.setForeground(TEXT_MAIN);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        comboBox.setUI(new DashboardComboBoxUI());
        comboBox.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                label.setBackground(isSelected ? new Color(42, 113, 255) : FIELD_BG);
                label.setForeground(TEXT_MAIN);
                list.setBackground(FIELD_BG);
                list.setForeground(TEXT_MAIN);
                return label;
            }
        });
    }

    private void styleDashboardSpinner(JSpinner spinner) {
        ModernTheme.styleSpinner(spinner);
        spinner.setPreferredSize(new Dimension(240, 56));
        spinner.setBackground(FIELD_BG);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField field = ((JSpinner.DefaultEditor) editor).getTextField();
            field.setFont(new Font("Segoe UI", Font.PLAIN, 17));
            field.setForeground(TEXT_MAIN);
            field.setBackground(FIELD_BG);
            field.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }
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

    private static class DashboardPagePanel extends JPanel {
        private DashboardPagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, PAGE_TOP, 0, getHeight(), PAGE_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(74, 144, 226, 20));
            g2.fillOval(getWidth() / 2 - 160, -120, 360, 220);
            g2.setColor(new Color(124, 58, 237, 16));
            g2.fillOval(-110, getHeight() - 190, 280, 240);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class DashboardHeroPanel extends JPanel {
        private DashboardHeroPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            LinearGradientPaint paint = new LinearGradientPaint(
                    0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.46f, 1f},
                    new Color[]{new Color(88, 38, 184), new Color(30, 90, 190), new Color(18, 161, 235)}
            );
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            g2.setColor(new Color(255, 255, 255, 18));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawArc(80, -45, 720, 260, 190, 135);
            g2.drawArc(getWidth() - 340, 56, 230, 160, 28, 140);
            g2.drawArc(getWidth() - 120, -70, 260, 260, 206, 130);

            g2.setColor(new Color(255, 255, 255, 92));
            g2.fillOval(getWidth() - 178, 42, 14, 14);
            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillOval(getWidth() - 124, 64, 16, 16);
            g2.setColor(new Color(255, 255, 255, 54));
            g2.fillOval(getWidth() - 124, 92, 5, 5);
            g2.setColor(new Color(9, 93, 187, 92));
            g2.fillOval(getWidth() - 82, getHeight() - 54, 120, 120);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlassPanel extends JPanel {
        private GlassPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 112));
            g2.fillRoundRect(4, 5, getWidth() - 8, getHeight() - 8, 8, 8);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, getWidth(), getHeight(), CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 8, 8);
            g2.setColor(new Color(255, 255, 255, 18));
            g2.drawLine(10, 1, Math.max(10, getWidth() - 14), 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconBadge extends JLabel {
        private final Color accent;
        private final boolean circle;

        private IconBadge(String text, Color accent, boolean circle) {
            super(text, SwingConstants.CENTER);
            this.accent = accent;
            this.circle = circle;
            setFont(new Font("Segoe UI Symbol", Font.BOLD, 28));
            setForeground(accent);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 44));
            if (circle) {
                g2.fillOval(0, 0, getWidth(), getHeight());
            } else {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class DashboardComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("\u2304");
            button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 21));
            button.setForeground(TEXT_MAIN);
            button.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(bounds.x - 10, bounds.y - 8, bounds.width + 18, bounds.height + 16, 8, 8);
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        private final Color start;
        private final Color end;

        private GradientButton(String text, Color start, Color end) {
            super(text);
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color left = getModel().isRollover() ? start.brighter() : start;
            Color right = getModel().isRollover() ? end.brighter() : end;
            g2.setColor(new Color(0, 0, 0, 75));
            g2.fillRoundRect(0, 4, getWidth(), getHeight() - 2, 8, 8);
            g2.setPaint(new GradientPaint(0, 0, left, getWidth(), getHeight(), right));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 4, 8, 8);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 7, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        private RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(thickness, thickness, thickness, thickness);
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }
}
