package ui;

import database.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 📊 Advanced Admin Analytics Dashboard
 * Displays comprehensive system statistics and performance metrics
 */
public class AdminAnalyticsFrame extends JFrame {

    public AdminAnalyticsFrame() {
        setTitle("📊 Advanced Admin Analytics Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        // Header
        JLabel title = new JLabel("📊 System Analytics & Reports", JLabel.CENTER);
        ModernTheme.styleLabel(title, 1);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ModernTheme.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title);
        title.setForeground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        // Main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.BUTTON_FONT);

        // Tab 1: System Statistics
        tabbedPane.addTab("System Stats", createSystemStatsPanel());

        // Tab 2: User Performance
        tabbedPane.addTab("User Performance", createUserPerformancePanel());

        // Tab 3: Quiz Analytics
        tabbedPane.addTab("Quiz Analytics", createQuizAnalyticsPanel());

        // Tab 4: Top Performers
        tabbedPane.addTab("Top Performers", createTopPerformersPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // 📊 System Statistics Panel
    private JPanel createSystemStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        ModernTheme.stylePanel(panel);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Total Users
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            rs.next();
            int totalUsers = rs.getInt("count");

            // Total Questions
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM questions");
            rs.next();
            int totalQuestions = rs.getInt("count");

            // Total Quiz Attempts
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM results");
            rs.next();
            int totalAttempts = rs.getInt("count");

            // Average System Accuracy
            rs = stmt.executeQuery("SELECT AVG(accuracy) as avg_accuracy FROM results");
            rs.next();
            double avgAccuracy = rs.getDouble("avg_accuracy");

            panel.add(createStatCard("👥 Total Users", String.valueOf(totalUsers), ModernTheme.SUCCESS_COLOR));
            panel.add(createStatCard("❓ Total Questions", String.valueOf(totalQuestions), ModernTheme.ACCENT_COLOR));
            panel.add(createStatCard("📋 Quiz Attempts", String.valueOf(totalAttempts), ModernTheme.WARNING_COLOR));
            panel.add(createStatCard("📈 Avg Accuracy", String.format("%.2f%%", avgAccuracy), ModernTheme.PRIMARY_COLOR));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    // 📊 User Performance Panel
    private JPanel createUserPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        ModernTheme.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Query user performance stats
            ResultSet rs = stmt.executeQuery(
                    "SELECT u.id, u.name, COUNT(r.id) as attempts, AVG(r.accuracy) as avg_accuracy " +
                    "FROM users u LEFT JOIN results r ON u.id = r.user_id " +
                    "WHERE u.role = 'user' " +
                    "GROUP BY u.id, u.name " +
                    "ORDER BY avg_accuracy DESC"
            );

            String[] columns = {"User ID", "User Name", "Attempts", "Avg Accuracy (%)"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("attempts"),
                        String.format("%.2f", rs.getDouble("avg_accuracy"))
                });
            }

            JTable table = new JTable(model);
            table.setFont(ModernTheme.LABEL_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setBackground(ModernTheme.PRIMARY_COLOR);
            table.getTableHeader().setForeground(Color.WHITE);

            panel.add(new JScrollPane(table), BorderLayout.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    // 📊 Quiz Analytics Panel (with Charts)
    private JPanel createQuizAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        ModernTheme.stylePanel(panel);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Bar Chart: Questions by Topic
            DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
            ResultSet rs = stmt.executeQuery(
                    "SELECT topic, COUNT(*) as count FROM questions GROUP BY topic"
            );

            while (rs.next()) {
                barDataset.addValue(rs.getInt("count"), "Questions", rs.getString("topic"));
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Questions by Topic",
                    "Topic",
                    "Count",
                    barDataset
            );

            ChartPanel barPanel = new ChartPanel(barChart);
            panel.add(barPanel);

            // Pie Chart: Quiz Difficulty Distribution
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            rs = stmt.executeQuery(
                    "SELECT difficulty, COUNT(*) as count FROM questions GROUP BY difficulty"
            );

            while (rs.next()) {
                pieDataset.setValue(rs.getString("difficulty"), rs.getInt("count"));
            }

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Difficulty Distribution",
                    pieDataset,
                    true,
                    true,
                    false
            );

            ChartPanel piePanel = new ChartPanel(pieChart);
            panel.add(piePanel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    // 📊 Top Performers Panel
    private JPanel createTopPerformersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        ModernTheme.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Query top 10 performers
            ResultSet rs = stmt.executeQuery(
                    "SELECT u.name, MAX(r.accuracy) as max_accuracy, AVG(r.accuracy) as avg_accuracy, " +
                    "COUNT(r.id) as total_attempts " +
                    "FROM users u JOIN results r ON u.id = r.user_id " +
                    "WHERE u.role = 'user' " +
                    "GROUP BY u.id, u.name " +
                    "ORDER BY max_accuracy DESC LIMIT 10"
            );

            String[] columns = {"Rank", "User Name", "Best Score (%)", "Avg Score (%)", "Total Attempts"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

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

            JTable table = new JTable(model);
            table.setFont(ModernTheme.LABEL_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setBackground(ModernTheme.SUCCESS_COLOR);
            table.getTableHeader().setForeground(Color.WHITE);

            panel.add(new JScrollPane(table), BorderLayout.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    // 🎨 Helper method to create stat cards
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, color));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ModernTheme.TEXT_COLOR);
        titleLabel.setFont(ModernTheme.LABEL_FONT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
