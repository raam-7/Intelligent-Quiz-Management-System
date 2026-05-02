package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminAnalyticsFrame extends JFrame {

    private static final Color PAGE_TOP = new Color(4, 10, 26);
    private static final Color PAGE_BOTTOM = new Color(8, 18, 42);
    private static final Color CARD_TOP = new Color(18, 32, 58, 236);
    private static final Color CARD_BOTTOM = new Color(7, 18, 40, 238);
    private static final Color CARD_BORDER = new Color(52, 77, 118);
    private static final Color TEXT_MAIN = new Color(248, 251, 255);
    private static final Color TEXT_MUTED = new Color(188, 204, 232);
    private static final Color PURPLE = new Color(146, 92, 255);

    public AdminAnalyticsFrame() {
        setTitle("Advanced Admin Analytics Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1360, 820);

        JPanel page = new AnalyticsPagePanel(new BorderLayout(0, 22));
        page.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        page.add(createHeroPanel(), BorderLayout.NORTH);
        page.add(createSectionTabs(), BorderLayout.CENTER);

        add(ModernTheme.createScrollPane(page));
        setVisible(true);
    }

    private JPanel createHeroPanel() {
        JPanel hero = new HeroPanel(new BorderLayout(20, 0));
        hero.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(78, 142, 232), 1, 8),
                BorderFactory.createEmptyBorder(26, 30, 26, 30)
        ));

        JLabel icon = new IconBadge("\u25AE", new Color(188, 150, 255), 66, 36);
        hero.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(0, 1, 0, 8));
        text.setOpaque(false);
        JLabel title = new JLabel("Questions by Topic");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52 / 2));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Distribution of questions across different topics in the question bank.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitle.setForeground(TEXT_MUTED);
        text.add(title);
        text.add(subtitle);
        hero.add(text, BorderLayout.CENTER);
        return hero;
    }

    private JPanel createChartsSection() {
        JPanel section = new JPanel(new GridLayout(1, 2, 20, 0));
        section.setOpaque(false);
        section.add(createTopicChartCard());
        section.add(createPieChartCard());
        return section;
    }

    private JTabbedPane createSectionTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        tabs.setForeground(TEXT_MAIN);
        tabs.setBackground(new Color(12, 22, 43));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());

        tabs.addTab("Analytics", createAnalyticsSection());
        tabs.addTab("Users", createUsersSection());
        tabs.addTab("Ranking", createRankingSection());
        return tabs;
    }

    private JPanel createAnalyticsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panel.add(createChartsSection(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUsersSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panel.add(createUserPerformanceCard(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRankingSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panel.add(createTopPerformersCard(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTopicChartCard() {
        JPanel card = new GlassCard(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(22, 22, 18, 22)
        ));

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.add(new IconBadge("\u2630", PURPLE, 36, 18), BorderLayout.WEST);

        JLabel title = new JLabel("Questions by Topic");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 40 / 2));
        title.setForeground(TEXT_MAIN);
        header.add(title, BorderLayout.CENTER);

        JComboBox<String> chartType = new JComboBox<>(new String[]{"Bar Chart"});
        chartType.setEnabled(false);
        chartType.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        chartType.setForeground(TEXT_MAIN);
        chartType.setBackground(new Color(20, 31, 57));
        chartType.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(72, 96, 140), 1, 7),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        header.add(chartType, BorderLayout.EAST);

        TopicBarChart panel = createTopicChart();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(panel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPieChartCard() {
        JPanel card = new GlassCard(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(22, 22, 18, 22)
        ));

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.add(new IconBadge("\u25D4", PURPLE, 36, 18), BorderLayout.WEST);

        JLabel title = new JLabel("Difficulty Distribution");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        header.add(title, BorderLayout.CENTER);

        DifficultyPieChart pie = createDifficultyPieChart();
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(pie, BorderLayout.CENTER);
        return card;
    }

    private TopicBarChart createTopicChart() {
        List<TopicCount> data = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT topic, COUNT(*) as count FROM questions GROUP BY topic ORDER BY count DESC, topic");
            while (rs.next()) {
                String topic = rs.getString("topic");
                int count = rs.getInt("count");
                String name = topic == null || topic.trim().isEmpty() ? "Uncategorized" : topic.trim();
                data.add(new TopicCount(name, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new TopicBarChart(data);
    }

    private DifficultyPieChart createDifficultyPieChart() {
        List<DifficultyCount> data = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT difficulty, COUNT(*) as count FROM questions GROUP BY difficulty ORDER BY count DESC");
            while (rs.next()) {
                String difficulty = rs.getString("difficulty");
                String name = difficulty == null || difficulty.trim().isEmpty() ? "Unknown" : difficulty.trim();
                data.add(new DifficultyCount(name, rs.getInt("count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DifficultyPieChart(data);
    }

    private JPanel createUserPerformanceCard() {
        String[] columns = {"User ID", "User Name", "Attempts", "Avg Accuracy (%)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT u.id, u.name, COUNT(r.id) as attempts, AVG(r.accuracy) as avg_accuracy " +
                            "FROM users u LEFT JOIN results r ON u.id = r.user_id " +
                            "WHERE u.role = 'user' " +
                            "GROUP BY u.id, u.name " +
                            "HAVING attempts > 0 " +
                            "ORDER BY avg_accuracy DESC, attempts DESC, u.name ASC"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("attempts"),
                        String.format("%.2f", rs.getDouble("avg_accuracy"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createDataTableCard("User Performance", model, new Color[]{
                new Color(130, 170, 255), new Color(205, 223, 255), new Color(91, 225, 176), new Color(174, 136, 255)
        });
    }

    private JPanel createTopPerformersCard() {
        String[] columns = {"Rank", "User Name", "Best Score (%)", "Avg Score (%)", "Attempts"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT u.name, MAX(r.accuracy) as max_accuracy, AVG(r.accuracy) as avg_accuracy, COUNT(r.id) as total_attempts " +
                            "FROM users u JOIN results r ON u.id = r.user_id " +
                            "WHERE u.role = 'user' " +
                            "GROUP BY u.id, u.name " +
                            "ORDER BY max_accuracy DESC, avg_accuracy DESC, total_attempts DESC, u.name ASC LIMIT 10"
            );
            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rank++,
                        rs.getString("name"),
                        String.format("%.2f", rs.getDouble("max_accuracy")),
                        String.format("%.2f", rs.getDouble("avg_accuracy")),
                        rs.getInt("total_attempts")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createDataTableCard("Top Performers", model, new Color[]{
                new Color(255, 196, 101), new Color(205, 223, 255), new Color(255, 122, 145), new Color(174, 136, 255), new Color(91, 225, 176)
        });
    }

    private JPanel createDataTableCard(String titleText, DefaultTableModel model, Color[] columnColors) {
        JPanel card = new GlassCard(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 10),
                BorderFactory.createEmptyBorder(16, 16, 14, 16)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);

        JTable table = new JTable(model);
        styleDataTable(table, columnColors);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new RoundedLineBorder(new Color(74, 99, 140), 1, 8));
        scrollPane.setPreferredSize(new Dimension(1140, 530));

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void styleDataTable(JTable table, Color[] columnColors) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_MAIN);
        table.setBackground(new Color(11, 22, 43));
        table.setGridColor(new Color(59, 78, 112));
        table.setSelectionBackground(new Color(70, 58, 190));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setBackground(new Color(27, 38, 67));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (isSelected) {
                    label.setForeground(Color.WHITE);
                } else {
                    label.setForeground(column < columnColors.length ? columnColors[column] : TEXT_MAIN);
                }
                label.setBackground(isSelected ? new Color(70, 58, 190) : (row % 2 == 0 ? new Color(12, 24, 47) : new Color(10, 20, 39)));
                return label;
            }
        });
    }

    private static class AnalyticsPagePanel extends JPanel {
        private AnalyticsPagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, PAGE_TOP, 0, getHeight(), PAGE_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(58, 156, 255, 18));
            g2.fillOval(getWidth() - 250, -120, 340, 260);
            g2.setColor(new Color(142, 72, 255, 14));
            g2.fillOval(-130, getHeight() - 210, 320, 260);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class HeroPanel extends JPanel {
        private HeroPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            LinearGradientPaint paint = new LinearGradientPaint(
                    0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.48f, 1f},
                    new Color[]{new Color(122, 23, 198), new Color(47, 84, 220), new Color(9, 163, 224)}
            );
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(new Color(255, 255, 255, 14));
            for (int x = getWidth() / 2; x < getWidth() + 160; x += 44) {
                g2.drawArc(x - 220, -100, 430, 220, 204, 118);
            }
            g2.setColor(new Color(66, 227, 255, 180));
            g2.fillOval(getWidth() - 44, 32, 8, 8);
            g2.fillOval(getWidth() - 66, 86, 6, 6);
            g2.fillOval(getWidth() - 222, 90, 7, 7);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlassCard extends JPanel {
        private GlassCard(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 110));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 10, 10, 10);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, getWidth(), getHeight(), CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 10, 10);
            g2.setColor(new Color(255, 255, 255, 15));
            g2.drawLine(14, 1, Math.max(14, getWidth() - 18), 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconBadge extends JLabel {
        private final Color accent;
        private final int fontSize;

        private IconBadge(String text, Color accent, int size, int fontSize) {
            super(text, SwingConstants.CENTER);
            this.accent = accent;
            this.fontSize = fontSize;
            setPreferredSize(new Dimension(size, size));
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            setForeground(accent);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 42));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
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

    private static class TopicCount {
        private final String topic;
        private final int count;

        private TopicCount(String topic, int count) {
            this.topic = topic;
            this.count = count;
        }
    }

    private static class DifficultyCount {
        private final String label;
        private final int count;

        private DifficultyCount(String label, int count) {
            this.label = label;
            this.count = count;
        }
    }

    private static class TopicBarChart extends JComponent {
        private final List<TopicCount> data;

        private TopicBarChart(List<TopicCount> data) {
            this.data = data;
            setPreferredSize(new Dimension(1200, 610));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 0));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int left = 70;
            int right = 24;
            int top = 26;
            int bottom = 120;
            int chartW = Math.max(100, getWidth() - left - right);
            int chartH = Math.max(100, getHeight() - top - bottom);
            int baseY = top + chartH;

            int max = 1;
            for (TopicCount row : data) {
                max = Math.max(max, row.count);
            }

            g2.setColor(new Color(108, 131, 171, 95));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (int i = 0; i <= max; i++) {
                int y = baseY - (int) ((i / (double) max) * chartH);
                g2.drawLine(left, y, left + chartW, y);
                g2.setColor(TEXT_MAIN);
                g2.drawString(String.valueOf(i), 48, y + 4);
                g2.setColor(new Color(108, 131, 171, 95));
            }

            g2.setColor(new Color(140, 160, 195));
            g2.drawLine(left, top, left, baseY);
            g2.drawLine(left, baseY, left + chartW, baseY);

            int n = Math.max(data.size(), 1);
            int slot = chartW / n;
            int barW = Math.max(20, Math.min(56, (int) (slot * 0.42)));

            g2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
            for (int i = 0; i < data.size(); i++) {
                TopicCount row = data.get(i);
                int x = left + i * slot + (slot - barW) / 2;
                int h = (int) ((row.count / (double) max) * chartH);
                int y = baseY - h;

                GradientPaint barPaint = new GradientPaint(
                        x, y, new Color(189, 140, 255),
                        x, y + h, new Color(92, 58, 225)
                );
                g2.setPaint(barPaint);
                g2.fillRoundRect(x, y, barW, h, 8, 8);

                g2.setColor(new Color(215, 198, 255, 160));
                g2.drawRoundRect(x, y, barW, h, 8, 8);

                g2.setColor(TEXT_MAIN);
                String value = String.valueOf(row.count);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(value, x + (barW - fm.stringWidth(value)) / 2, y - 8);

                String topic = row.topic.length() > 15 ? row.topic.substring(0, 15) + "..." : row.topic;
                g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
                g2.setColor(TEXT_MAIN);
                Graphics2D gLabel = (Graphics2D) g2.create();
                gLabel.rotate(-0.38, x + barW / 2.0, baseY + 46);
                gLabel.drawString(topic, x - 10, baseY + 46);
                gLabel.dispose();
            }

            g2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
            g2.setColor(new Color(219, 231, 248));
            g2.drawString("Count", 14, top + chartH / 2);
            g2.drawString("Topic", left + chartW / 2 - 20, getHeight() - 22);
            g2.dispose();
        }
    }

    private static class DifficultyPieChart extends JComponent {
        private final List<DifficultyCount> data;

        private DifficultyPieChart(List<DifficultyCount> data) {
            this.data = data;
            setPreferredSize(new Dimension(560, 610));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = 0;
            for (DifficultyCount row : data) {
                total += Math.max(0, row.count);
            }
            total = Math.max(total, 1);

            int diameter = Math.min(getWidth() - 120, getHeight() - 180);
            diameter = Math.max(diameter, 220);
            int x = (getWidth() - diameter) / 2;
            int y = 40;

            Color[] palette = new Color[]{
                    new Color(123, 203, 78),
                    new Color(255, 189, 74),
                    new Color(255, 93, 118),
                    new Color(95, 153, 255)
            };

            double start = 110;
            for (int i = 0; i < data.size(); i++) {
                DifficultyCount row = data.get(i);
                double extent = 360.0 * row.count / total;
                Color c = palette[i % palette.length];
                g2.setColor(c);
                g2.fillArc(x, y, diameter, diameter, (int) Math.round(start), (int) Math.round(extent));
                g2.setColor(new Color(12, 20, 39));
                g2.setStroke(new BasicStroke(3f));
                g2.drawArc(x, y, diameter, diameter, (int) Math.round(start), (int) Math.round(extent));
                start += extent;
            }

            int inner = (int) (diameter * 0.55);
            int ix = x + (diameter - inner) / 2;
            int iy = y + (diameter - inner) / 2;
            g2.setColor(new Color(10, 20, 40));
            g2.fillOval(ix, iy, inner, inner);
            g2.setColor(new Color(82, 106, 148));
            g2.drawOval(ix, iy, inner, inner);

            g2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
            g2.setColor(TEXT_MAIN);
            g2.drawString("Total", ix + inner / 2 - 24, iy + inner / 2 - 4);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.drawString(String.valueOf(total), ix + inner / 2 - 16, iy + inner / 2 + 30);

            int ly = y + diameter + 30;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            for (int i = 0; i < data.size(); i++) {
                DifficultyCount row = data.get(i);
                Color c = palette[i % palette.length];
                int lx = 36 + (i % 2) * ((getWidth() - 72) / 2);
                int rowY = ly + (i / 2) * 28;
                g2.setColor(c);
                g2.fillRoundRect(lx, rowY - 12, 14, 14, 4, 4);
                g2.setColor(TEXT_MAIN);
                int pct = (int) Math.round(row.count * 100.0 / total);
                g2.drawString(row.label + ": " + row.count + " (" + pct + "%)", lx + 22, rowY);
            }
            g2.dispose();
        }
    }
}
