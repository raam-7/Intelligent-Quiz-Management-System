package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private static final Color PAGE_TOP = new Color(4, 10, 26);
    private static final Color PAGE_BOTTOM = new Color(8, 18, 42);
    private static final Color CARD_TOP = new Color(18, 32, 58, 236);
    private static final Color CARD_BOTTOM = new Color(7, 18, 40, 238);
    private static final Color CARD_BORDER = new Color(52, 77, 118);
    private static final Color TEXT_MAIN = new Color(248, 251, 255);
    private static final Color TEXT_MUTED = new Color(185, 202, 231);
    private static final Color PURPLE = new Color(170, 80, 255);
    private static final Color GREEN = new Color(34, 218, 151);
    private static final Color ORANGE = new Color(255, 165, 64);
    private static final Color BLUE = new Color(72, 168, 255);
    private static final Color TAB_MUTED = new Color(150, 165, 194);

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1180, 780);

        JPanel page = new AdminPagePanel(new BorderLayout(0, 26));
        page.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        page.add(createHero(), BorderLayout.NORTH);

        JPanel center = ModernTheme.createSectionPanel(new BorderLayout(0, 28));
        center.add(createOverviewStrip(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        styleTabs(tabs);
        tabs.addTab("Manage", createManageTab());
        tabs.addTab("Users", createUsersTab());
        tabs.addTab("Analytics", createAnalyticsTab());
        tabs.setTabComponentAt(0, createTabLabel("\u25C8", "Manage", true));
        tabs.setTabComponentAt(1, createTabLabel("\u263A", "Users", false));
        tabs.setTabComponentAt(2, createTabLabel("\u2318", "Analytics", false));
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component tab = tabs.getTabComponentAt(i);
                if (tab instanceof JLabel) {
                    tab.setForeground(i == tabs.getSelectedIndex() ? TEXT_MAIN : TAB_MUTED);
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

    private JPanel createHero() {
        JPanel hero = new AdminHeroPanel(new BorderLayout(24, 0));
        hero.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(84, 145, 230), 1, 10),
                BorderFactory.createEmptyBorder(34, 34, 34, 34)
        ));

        hero.add(createLargeBadge("\u265B", Color.WHITE), BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(0, 1, 0, 8));
        text.setOpaque(false);
        JLabel title = new JLabel("Admin Command Center");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Manage questions, review user performance, and explore live analytics from one place.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(225, 236, 255));
        text.add(title);
        text.add(subtitle);
        hero.add(text, BorderLayout.CENTER);
        return hero;
    }

    private JPanel createOverviewStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 4, 22, 0));
        strip.setOpaque(false);
        strip.add(createStatusCard("\u25AB", "Question Bank", "Manage", "Add, edit and organize\nquiz questions", PURPLE));
        strip.add(createStatusCard("\u263A", "Users", "Review", "Review user activity\nand performance", GREEN));
        strip.add(createStatusCard("\u25AE", "Reports", "Analyze", "View reports and\ninsights", ORANGE));
        strip.add(createStatusCard("\u25E6", "Status", "Online", "System is active\nand running", BLUE));
        return strip;
    }

    private JPanel createManageTab() {
        JPanel panel = createTabPanel();
        JLabel intro = createMutedLabel("Update the quiz bank and keep content fresh.", 18);
        panel.add(intro, BorderLayout.NORTH);
        panel.add(createActionCard("\u25C9", "Question Bank", "Add, edit, or remove questions witha\ncleaner data management view.", "Open Manager", PURPLE, () -> new ManageQuestionsFrame()), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUsersTab() {
        JPanel panel = createTabPanel();
        JLabel intro = createMutedLabel("Review learner activity, attempts, averages, and rankings.", 18);
        panel.add(intro, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setOpaque(false);
        grid.add(createActionCard("\u263A", "User Performance", "Inspect attempts, averages,\nand performance trends.", "Open Users", GREEN, () -> new AdminAnalyticsFrame()));
        grid.add(createActionCard("\u2605", "Top Performers", "Spot high-performing users\nand ranking patterns.", "Open Rankings", BLUE, () -> new AdminAnalyticsFrame()));
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnalyticsTab() {
        JPanel panel = createTabPanel();
        JLabel intro = createMutedLabel("Explore question distribution, difficulty mix, attempts, and platform performance.", 18);
        panel.add(intro, BorderLayout.NORTH);
        panel.add(createActionCard("\u2318", "System Analytics", "Explore questions by topic, difficulty mix,\nattempts, and system-wide performance.", "View Analytics", ORANGE, () -> new AdminAnalyticsFrame()), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabPanel() {
        JPanel panel = createGlassCard(new BorderLayout(0, 28), 42, 32, 42, 32);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(42, 32, 42, 32)
        ));
        return panel;
    }

    private JPanel createStatusCard(String icon, String label, String value, String helper, Color accent) {
        JPanel card = new GlowCardPanel(new BorderLayout(18, 0), accent);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(30, 24, 28, 24)
        ));
        card.add(createCircleBadge(icon, accent, 66, 31), BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(0, 1, 0, 6));
        text.setOpaque(false);
        JLabel labelText = createMutedLabel(label, 16);
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        valueText.setForeground(accent);
        JLabel helperText = createMutedLabel("<html>" + helper.replace("\n", "<br>") + "</html>", 15);
        text.add(labelText);
        text.add(valueText);
        text.add(helperText);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionCard(String icon, String title, String description, String buttonText, Color accent, Runnable action) {
        JPanel card = new GlowCardPanel(new BorderLayout(0, 26), accent);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140), 1, 10),
                BorderFactory.createEmptyBorder(38, 38, 38, 38)
        ));

        JPanel content = new JPanel(new BorderLayout(34, 0));
        content.setOpaque(false);
        content.add(createSquareBadge(icon, accent), BorderLayout.WEST);

        JPanel copy = new JPanel(new GridLayout(0, 1, 0, 12));
        copy.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_MAIN);
        JLabel descriptionLabel = createMutedLabel("<html>" + description.replace("\n", "<br>") + "</html>", 18);
        copy.add(titleLabel);
        copy.add(descriptionLabel);
        content.add(copy, BorderLayout.CENTER);

        JButton button = new GradientButton("\u21F1  " + buttonText, new Color(147, 54, 235), new Color(78, 80, 239));
        button.setPreferredSize(new Dimension(520, 66));
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 23));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());

        card.add(content, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);
        return card;
    }

    private JLabel createTabLabel(String icon, String text, boolean selected) {
        JLabel label = new JLabel(icon + "  " + text);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(18, 36, 18, 36));
        return label;
    }

    private void styleTabs(JTabbedPane tabs) {
        ModernTheme.styleTabbedPane(tabs);
        tabs.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        tabs.setForeground(Color.WHITE);
        tabs.setBackground(Color.BLACK);
        tabs.setOpaque(true);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new BlackTabbedPaneUI());
    }

    private JLabel createMutedLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private JPanel createGlassCard(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel panel = new GlassPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        ));
        return panel;
    }

    private JLabel createLargeBadge(String symbol, Color accent) {
        JLabel label = new IconBadge(symbol, accent, 86, 38, true);
        label.setPreferredSize(new Dimension(86, 86));
        return label;
    }

    private JLabel createCircleBadge(String symbol, Color accent, int size, int fontSize) {
        JLabel label = new IconBadge(symbol, accent, size, fontSize, true);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }

    private JLabel createSquareBadge(String symbol, Color accent) {
        JLabel label = new IconBadge(symbol, accent, 128, 58, false);
        label.setPreferredSize(new Dimension(128, 128));
        return label;
    }

    private static class AdminPagePanel extends JPanel {
        private AdminPagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, PAGE_TOP, 0, getHeight(), PAGE_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(44, 134, 255, 18));
            g2.fillOval(getWidth() - 260, -110, 340, 300);
            g2.setColor(new Color(168, 74, 255, 16));
            g2.fillOval(-140, getHeight() - 210, 320, 260);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class AdminHeroPanel extends JPanel {
        private AdminHeroPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            LinearGradientPaint paint = new LinearGradientPaint(
                    0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.42f, 1f},
                    new Color[]{new Color(124, 25, 198), new Color(43, 92, 214), new Color(15, 181, 221)}
            );
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.setColor(new Color(255, 255, 255, 16));
            g2.setStroke(new BasicStroke(1f));
            for (int x = -80; x < getWidth(); x += 70) {
                g2.drawLine(x, getHeight(), x + 170, 0);
            }
            for (int i = 0; i < 5; i++) {
                g2.drawArc(550 + i * 50, -80 + i * 20, 420, 250, 188, 110);
            }

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillOval(getWidth() - 150, 40, 18, 18);
            g2.setColor(new Color(48, 233, 237, 142));
            g2.fillOval(getWidth() - 54, 34, 13, 13);
            g2.fillOval(getWidth() - 104, 77, 10, 10);
            g2.setColor(new Color(255, 255, 255, 72));
            g2.fillOval(getWidth() - 165, 109, 8, 8);
            g2.setColor(new Color(58, 189, 246, 82));
            g2.fillOval(getWidth() - 48, getHeight() - 34, 44, 44);
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
            g2.fillRoundRect(5, 6, getWidth() - 10, getHeight() - 10, 10, 10);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, getWidth(), getHeight(), CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 10, 10);
            g2.setColor(new Color(255, 255, 255, 16));
            g2.drawLine(14, 1, Math.max(14, getWidth() - 18), 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlowCardPanel extends GlassPanel {
        private final Color accent;

        private GlowCardPanel(LayoutManager layout, Color accent) {
            super(layout);
            this.accent = accent;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int y = getHeight() - 5;
            g2.setPaint(new GradientPaint(20, y, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0),
                    getWidth() / 2, y, accent));
            g2.fillRoundRect(10, y, getWidth() - 24, 4, 4, 4);
            g2.dispose();
        }
    }

    private static class IconBadge extends JLabel {
        private final Color accent;
        private final int fontSize;
        private final boolean circle;

        private IconBadge(String text, Color accent, int size, int fontSize, boolean circle) {
            super(text, SwingConstants.CENTER);
            this.accent = accent;
            this.fontSize = fontSize;
            this.circle = circle;
            setPreferredSize(new Dimension(size, size));
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            setForeground(accent);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 46));
            if (circle) {
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
            } else {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            }
            g2.dispose();
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            super.paintComponent(g);
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
            g2.fillRoundRect(0, 5, getWidth(), getHeight() - 2, 9, 9);
            g2.setPaint(new GradientPaint(0, 0, left, getWidth(), getHeight(), right));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 5, 9, 9);
            g2.setColor(new Color(255, 255, 255, 42));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 8, 9, 9);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BlackTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.BLACK);
            g2.fillRect(x, y, w, h);
            if (isSelected) {
                g2.setColor(PURPLE);
                g2.fillRect(x, y + h - 4, w, 4);
            }
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, tabPane.getWidth(), Math.max(0, calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)));
            g2.dispose();
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
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
