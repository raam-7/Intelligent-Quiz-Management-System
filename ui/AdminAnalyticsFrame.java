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

public class AdminAnalyticsFrame extends JFrame {

    public AdminAnalyticsFrame() {
        setTitle("Advanced Admin Analytics Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        ModernTheme.prepareFrame(this, 1240, 760);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("System Analytics & Reports", "Monitor platform health, learner performance, and quiz content trends."), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        ModernTheme.styleTabbedPane(tabbedPane);
        tabbedPane.addTab("System Stats", createSystemStatsPanel());
        tabbedPane.addTab("User Performance", createUserPerformancePanel());
        tabbedPane.addTab("Quiz Analytics", createQuizAnalyticsPanel());
        tabbedPane.addTab("Top Performers", createTopPerformersPanel());

        page.add(tabbedPane, BorderLayout.CENTER);
        add(page);
        setVisible(true);
    }

    private JPanel createSystemStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 18, 18));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            rs.next();
            int totalUsers = rs.getInt("count");

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM questions");
            rs.next();
            int totalQuestions = rs.getInt("count");

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM results");
            rs.next();
            int totalAttempts = rs.getInt("count");

            rs = stmt.executeQuery("SELECT AVG(accuracy) as avg_accuracy FROM results");
            rs.next();
            double avgAccuracy = rs.getDouble("avg_accuracy");

            panel.add(ModernTheme.createMetricCard("Total Users", String.valueOf(totalUsers), ModernTheme.SUCCESS_COLOR));
            panel.add(ModernTheme.createMetricCard("Total Questions", String.valueOf(totalQuestions), ModernTheme.ACCENT_COLOR));
            panel.add(ModernTheme.createMetricCard("Quiz Attempts", String.valueOf(totalAttempts), ModernTheme.WARNING_COLOR));
            panel.add(ModernTheme.createMetricCard("Average Accuracy", String.format("%.2f%%", avgAccuracy), ModernTheme.PRIMARY_COLOR));
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(ModernTheme.createMetricCard("Data", "Unavailable", ModernTheme.DANGER_COLOR));
        }

        return panel;
    }

    private JPanel createUserPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
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
            ModernTheme.styleTable(table);
            JScrollPane scrollPane = new JScrollPane(table);
            ModernTheme.styleScrollPane(scrollPane);

            JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 14));
            card.add(ModernTheme.createSectionTitle("User Performance Overview"), BorderLayout.NORTH);
            card.add(scrollPane, BorderLayout.CENTER);
            panel.add(card, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createQuizAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 18, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
            ResultSet rs = stmt.executeQuery("SELECT topic, COUNT(*) as count FROM questions GROUP BY topic");
            while (rs.next()) {
                barDataset.addValue(rs.getInt("count"), "Questions", rs.getString("topic"));
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Questions by Topic",
                    "Topic",
                    "Count",
                    barDataset
            );

            DefaultPieDataset pieDataset = new DefaultPieDataset();
            rs = stmt.executeQuery("SELECT difficulty, COUNT(*) as count FROM questions GROUP BY difficulty");
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

            panel.add(wrapChart("Questions by Topic", new ChartPanel(barChart)));
            panel.add(wrapChart("Difficulty Distribution", new ChartPanel(pieChart)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createTopPerformersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
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
            ModernTheme.styleTable(table);
            JScrollPane scrollPane = new JScrollPane(table);
            ModernTheme.styleScrollPane(scrollPane);

            JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 14));
            card.add(ModernTheme.createSectionTitle("Top 10 Performers"), BorderLayout.NORTH);
            card.add(scrollPane, BorderLayout.CENTER);
            panel.add(card, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panel;
    }

    private JPanel wrapChart(String title, ChartPanel chartPanel) {
        chartPanel.setMouseWheelEnabled(true);
        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 14));
        card.add(ModernTheme.createSectionTitle(title), BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }
}
