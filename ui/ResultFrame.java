package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResultFrame extends JFrame {

    private static final Color PAGE_TOP = new Color(4, 10, 26);
    private static final Color PAGE_BOTTOM = new Color(8, 18, 42);
    private static final Color CARD_TOP = new Color(17, 31, 56, 238);
    private static final Color CARD_BOTTOM = new Color(7, 18, 40, 240);
    private static final Color CARD_BORDER = new Color(52, 77, 118);
    private static final Color TEXT_MAIN = new Color(248, 251, 255);
    private static final Color TEXT_MUTED = new Color(170, 190, 222);
    private static final Color BLUE = new Color(72, 132, 255);
    private static final Color BLUE_LIGHT = new Color(100, 166, 255);
    private static final Color GREEN = new Color(52, 232, 139);
    private static final Color RED = new Color(255, 73, 93);
    private static final Color RED_DARK = new Color(188, 34, 54);
    private static final Color PURPLE = new Color(142, 92, 255);
    private static final Color AMBER = new Color(255, 184, 55);

    private final int userId;
    private final DefaultTableModel exportModel = new DefaultTableModel(
            new String[]{"Attempt ID", "Score", "Accuracy (%)", "Time Taken (sec)", "Date"}, 0
    );

    public ResultFrame(int userId) {
        this.userId = userId;

        setTitle("Performance Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1280, 720);

        AttemptStats stats = loadLatestAttempt();

        JPanel page = new AnalysisPagePanel(new BorderLayout(22, 0));
        page.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel summaryCard = createSummaryCard(stats);
        JPanel chartCard = createChartCard(stats);
        page.add(summaryCard, BorderLayout.WEST);
        page.add(chartCard, BorderLayout.CENTER);
        page.add(createBottomTrack(), BorderLayout.SOUTH);

        add(ModernTheme.createScrollPane(page));
        setVisible(true);
    }

    private JPanel createSummaryCard(AttemptStats stats) {
        JPanel card = createGlassCard(new BorderLayout(0, 24), 24, 24, 24, 24);
        card.setPreferredSize(new Dimension(390, 620));

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.add(createIconBadge("\u2301", PURPLE), BorderLayout.WEST);
        JLabel title = new JLabel("Performance Overview");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        title.setForeground(TEXT_MAIN);
        header.add(title, BorderLayout.CENTER);

        JPanel rows = new JPanel(new GridLayout(6, 1, 0, 0));
        rows.setOpaque(false);
        rows.add(createMetricRow("\u2630", "Total Questions", String.valueOf(stats.totalQuestions), BLUE));
        rows.add(createMetricRow("\u2713", "Correct Answers", String.valueOf(stats.correctAnswers), GREEN));
        rows.add(createMetricRow("\u00D7", "Incorrect Answers", String.valueOf(stats.incorrectAnswers), RED));
        rows.add(createMetricRow("%", "Accuracy", stats.accuracyLabel(), PURPLE));
        rows.add(createMetricRow("\u25F7", "Time Taken", formatTime(stats.timeTakenSeconds), AMBER));
        rows.add(createMetricRow("\u25A6", "Completed On", stats.completedOnLabel(), BLUE_LIGHT));

        JPanel progress = new JPanel(new BorderLayout(0, 16));
        progress.setOpaque(false);
        JLabel progressTitle = new JLabel("Accuracy Progress");
        progressTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        progressTitle.setForeground(TEXT_MAIN);
        progress.add(progressTitle, BorderLayout.NORTH);

        JPanel progressLine = new JPanel(new BorderLayout(16, 0));
        progressLine.setOpaque(false);
        progressLine.add(new AccuracyBar(stats.accuracy), BorderLayout.CENTER);
        JLabel percent = new JLabel(stats.accuracyLabel());
        percent.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        percent.setForeground(RED);
        progressLine.add(percent, BorderLayout.EAST);
        progress.add(progressLine, BorderLayout.CENTER);

        JLabel note = new JLabel("You scored better than " + Math.round(stats.accuracy) + "% of attempts");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        note.setForeground(TEXT_MUTED);
        progress.add(note, BorderLayout.SOUTH);

        card.add(header, BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);
        card.add(progress, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createChartCard(AttemptStats stats) {
        JPanel card = createGlassCard(new BorderLayout(0, 18), 24, 28, 24, 28);

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.add(createIconBadge("\u25D4", PURPLE), BorderLayout.WEST);

        JPanel copy = new JPanel(new GridLayout(0, 1, 0, 4));
        copy.setOpaque(false);
        JLabel title = new JLabel("Answer Distribution");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Correct vs Incorrect Answers");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_MUTED);
        copy.add(title);
        copy.add(subtitle);
        header.add(copy, BorderLayout.CENTER);

        JButton exportButton = new OutlineButton("\u21E9  Export");
        exportButton.setPreferredSize(new Dimension(118, 48));
        exportButton.addActionListener(e -> exportResults(exportModel));
        header.add(exportButton, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(new AnswerDistributionChart(stats), BorderLayout.CENTER);
        return card;
    }

    private JPanel createMetricRow(String icon, String label, String value, Color accent) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(74, 97, 132, 105)),
                BorderFactory.createEmptyBorder(14, 0, 14, 0)
        ));
        row.add(createSmallIconBadge(icon, accent), BorderLayout.WEST);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        labelText.setForeground(TEXT_MAIN);
        row.add(labelText, BorderLayout.CENTER);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI Semibold", Font.BOLD, 19));
        valueText.setForeground(accent);
        row.add(valueText, BorderLayout.EAST);
        return row;
    }

    private JLabel createIconBadge(String symbol, Color color) {
        JLabel label = new IconBadge(symbol, color, 50, 24);
        label.setPreferredSize(new Dimension(50, 50));
        return label;
    }

    private JLabel createSmallIconBadge(String symbol, Color color) {
        JLabel label = new IconBadge(symbol, color, 44, 21);
        label.setPreferredSize(new Dimension(44, 44));
        return label;
    }

    private JPanel createBottomTrack() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 72, 0, 72));
        wrapper.add(new TimelineTrack(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createGlassCard(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel panel = new GlassPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 14),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        ));
        return panel;
    }

    private AttemptStats loadLatestAttempt() {
        List<AttemptStats> attempts = new ArrayList<>();
        exportModel.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                throw new Exception("Database connection is null");
            }

            String sql = "SELECT id, score, accuracy, time_taken, date FROM results WHERE user_id = ? ORDER BY date DESC";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int score = rs.getInt("score");
                        double accuracy = rs.getDouble("accuracy");
                        int total = accuracy > 0 ? (int) Math.round(score * 100.0 / accuracy) : score;
                        AttemptStats attempt = new AttemptStats(
                                rs.getInt("id"),
                                Math.max(total, score),
                                score,
                                Math.max(total - score, 0),
                                accuracy,
                                rs.getInt("time_taken"),
                                rs.getTimestamp("date")
                        );
                        attempts.add(attempt);
                        exportModel.addRow(new Object[]{
                                attempt.id,
                                attempt.correctAnswers,
                                String.format("%.2f", attempt.accuracy),
                                attempt.timeTakenSeconds,
                                attempt.completedOn
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading performance data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (attempts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No previous results found for this user.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            return AttemptStats.empty();
        }
        return attempts.get(0);
    }

    private void exportResults(DefaultTableModel model) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("quiz_results_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                exportToCSV(filePath, model);
                JOptionPane.showMessageDialog(this, "Results exported successfully to:\n" + filePath, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportToCSV(String filePath, DefaultTableModel model) throws Exception {
        FileWriter csvWriter = new FileWriter(filePath);

        for (int i = 0; i < model.getColumnCount(); i++) {
            csvWriter.append(model.getColumnName(i));
            if (i < model.getColumnCount() - 1) {
                csvWriter.append(",");
            }
        }
        csvWriter.append("\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                String cellValue = value != null ? value.toString() : "";
                if (cellValue.contains(",") || cellValue.contains("\"") || cellValue.contains("\n")) {
                    cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                }

                csvWriter.append(cellValue);
                if (j < model.getColumnCount() - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    private String formatTime(int seconds) {
        int minutes = Math.max(seconds, 0) / 60;
        int remainingSeconds = Math.max(seconds, 0) % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private static class AttemptStats {
        private final int id;
        private final int totalQuestions;
        private final int correctAnswers;
        private final int incorrectAnswers;
        private final double accuracy;
        private final int timeTakenSeconds;
        private final Timestamp completedOn;

        private AttemptStats(int id, int totalQuestions, int correctAnswers, int incorrectAnswers, double accuracy, int timeTakenSeconds, Timestamp completedOn) {
            this.id = id;
            this.totalQuestions = totalQuestions;
            this.correctAnswers = correctAnswers;
            this.incorrectAnswers = incorrectAnswers;
            this.accuracy = accuracy;
            this.timeTakenSeconds = timeTakenSeconds;
            this.completedOn = completedOn;
        }

        private static AttemptStats empty() {
            return new AttemptStats(0, 0, 0, 0, 0, 0, null);
        }

        private String accuracyLabel() {
            return Math.round(accuracy) + "%";
        }

        private String completedOnLabel() {
            if (completedOn == null) {
                return "No attempts";
            }
            return completedOn.toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
    }

    private static class AnalysisPagePanel extends JPanel {
        private AnalysisPagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, PAGE_TOP, 0, getHeight(), PAGE_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(92, 76, 255, 22));
            g2.fillOval(getWidth() / 2 - 110, -160, 260, 260);
            g2.setColor(new Color(26, 117, 255, 18));
            g2.fillOval(getWidth() - 180, getHeight() - 180, 280, 240);
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
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(5, 6, getWidth() - 10, getHeight() - 10, 14, 14);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, getWidth(), getHeight(), CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 14, 14);
            g2.setColor(new Color(255, 255, 255, 18));
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
            setForeground(accent);
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 42));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            super.paintComponent(g);
        }
    }

    private static class AccuracyBar extends JComponent {
        private final double accuracy;

        private AccuracyBar(double accuracy) {
            this.accuracy = Math.max(0, Math.min(100, accuracy));
            setPreferredSize(new Dimension(260, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = 16;
            int y = (getHeight() - h) / 2;
            g2.setColor(new Color(40, 55, 86));
            g2.fillRoundRect(0, y, getWidth(), h, h, h);
            int fill = (int) (getWidth() * accuracy / 100.0);
            g2.setPaint(new GradientPaint(0, y, new Color(255, 58, 108), fill, y, new Color(255, 119, 68)));
            g2.fillRoundRect(0, y, fill, h, h, h);
            g2.dispose();
        }
    }

    private static class AnswerDistributionChart extends JComponent {
        private final AttemptStats stats;

        private AnswerDistributionChart(AttemptStats stats) {
            this.stats = stats;
            setPreferredSize(new Dimension(720, 510));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = Math.max(stats.totalQuestions, 1);
            double correctFraction = stats.correctAnswers / (double) total;
            double correctAngle = 360 * correctFraction;
            double incorrectAngle = 360 - correctAngle;
            int diameter = Math.min(getWidth() - 230, getHeight() - 70);
            int x = (getWidth() - diameter) / 2 - 12;
            int y = (getHeight() - diameter) / 2 + 6;

            g2.setColor(new Color(255, 73, 93, 52));
            g2.fillOval(x - 18, y - 18, diameter + 36, diameter + 36);
            g2.setColor(new Color(72, 132, 255, 42));
            g2.fillOval(x - 28, y + 28, diameter / 2, diameter / 2);

            Arc2D.Double correct = new Arc2D.Double(x + 8, y, diameter, diameter, -correctAngle + 86, correctAngle - 3, Arc2D.PIE);
            Arc2D.Double incorrect = new Arc2D.Double(x - 8, y, diameter, diameter, 89, incorrectAngle - 3, Arc2D.PIE);

            g2.setPaint(new GradientPaint(x, y, new Color(255, 68, 88), x + diameter, y + diameter, RED_DARK));
            g2.fill(correct);
            g2.setColor(new Color(255, 126, 139));
            g2.setStroke(new BasicStroke(3f));
            g2.draw(correct);

            g2.setPaint(new GradientPaint(x, y, new Color(76, 122, 255), x + diameter, y + diameter, new Color(32, 62, 190)));
            g2.fill(incorrect);
            g2.setColor(new Color(101, 162, 255));
            g2.draw(incorrect);

            drawCenterLabel(g2, x + diameter / 2 + diameter / 5, y + diameter / 2 - 70, "Correct", stats.correctAnswers, correctFraction, new Color(121, 29, 44, 210));
            drawCenterLabel(g2, x + diameter / 2 - diameter / 3, y + diameter / 2 + 4, "Incorrect", stats.incorrectAnswers, 1 - correctFraction, new Color(19, 45, 134, 220));
            drawCallout(g2, x + diameter + 46, y + diameter / 2 - 26, "Correct", RED, true);
            drawCallout(g2, x - 82, y + diameter / 2 + 82, "Incorrect", BLUE, false);

            g2.dispose();
        }

        private void drawCenterLabel(Graphics2D g2, int x, int y, String label, int count, double fraction, Color fill) {
            g2.setColor(fill);
            g2.fillRoundRect(x - 58, y - 32, 112, 64, 8, 8);
            g2.setColor(TEXT_MAIN);
            g2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
            drawCentered(g2, label, x, y - 8);
            drawCentered(g2, count + " (" + Math.round(fraction * 100) + "%)", x, y + 16);
        }

        private void drawCallout(Graphics2D g2, int x, int y, String label, Color color, boolean rightSide) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            int dotX = rightSide ? x : x + 48;
            g2.fillOval(dotX - 5, y - 5, 10, 10);
            if (rightSide) {
                g2.draw(new Line2D.Double(x - 58, y + 3, x - 8, y));
                g2.drawArc(x - 86, y - 4, 52, 24, 18, 74);
                g2.setColor(TEXT_MAIN);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.drawString(label, x + 16, y + 8);
            } else {
                g2.draw(new Line2D.Double(x + 56, y, x + 106, y - 12));
                g2.drawArc(x + 104, y - 30, 54, 28, 205, 70);
                g2.setColor(TEXT_MAIN);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.drawString(label, x - 18, y + 8);
            }
        }

        private void drawCentered(Graphics2D g2, String text, int centerX, int baseline) {
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(text, centerX - metrics.stringWidth(text) / 2, baseline);
        }
    }

    private static class TimelineTrack extends JComponent {
        private TimelineTrack() {
            setPreferredSize(new Dimension(800, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int y = getHeight() / 2 - 3;
            g2.setColor(new Color(64, 78, 112));
            g2.fillRoundRect(0, y, getWidth(), 6, 6, 6);
            g2.setPaint(new GradientPaint(getWidth() / 3, y, PURPLE, getWidth() * 2 / 3, y, new Color(123, 85, 255)));
            g2.fillRoundRect(getWidth() / 3, y, getWidth() * 2 / 5, 6, 6, 6);
            g2.dispose();
        }
    }

    private static class OutlineButton extends JButton {
        private OutlineButton(String text) {
            super(text);
            setForeground(BLUE_LIGHT);
            setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(19, 33, 58, 190));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(new Color(54, 79, 121));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
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
