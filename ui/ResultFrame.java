package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResultFrame extends JFrame {

    private final int userId;

    public ResultFrame(int userId) {
        this.userId = userId;

        setTitle("My Quiz Results");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        ModernTheme.prepareFrame(this, 1100, 620);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("My Results", "Review attempts, export a report, and open analytics with one click."), BorderLayout.NORTH);

        String[] columns = {"Attempt ID", "Score", "Accuracy (%)", "Time Taken (sec)", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        ModernTheme.styleTable(table);

        try {
            loadResults(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading results: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        ModernTheme.styleScrollPane(scrollPane);

        JButton analyticsBtn = new JButton("View Analytics");
        ModernTheme.styleButton(analyticsBtn);
        JButton exportBtn = new JButton("Export to CSV");
        ModernTheme.styleSecondaryButton(exportBtn);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(analyticsBtn);
        buttonPanel.add(exportBtn);

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 18));
        card.add(ModernTheme.createSectionTitle("Attempt History"), BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);

        exportBtn.addActionListener(e -> exportResults(model));
        analyticsBtn.addActionListener(e -> openAnalytics(model));

        add(page);
        setVisible(true);
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

    private void openAnalytics(DefaultTableModel model) {
        Map<String, Double> topicAccuracyMap = new HashMap<>();

        try {
            Connection conn = DBConnection.getConnection();
            ensureTopicStatsTable(conn);

            String topicSql = """
                    SELECT rts.topic,
                           SUM(rts.correct_count) AS total_correct,
                           SUM(rts.total_count) AS total_questions
                    FROM result_topic_stats rts
                    JOIN results r ON r.id = rts.result_id
                    WHERE r.user_id = ?
                    GROUP BY rts.topic
                    ORDER BY rts.topic
                    """;

            try (PreparedStatement pst = conn.prepareStatement(topicSql)) {
                pst.setInt(1, userId);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    int totalCorrect = rs.getInt("total_correct");
                    int totalQuestions = rs.getInt("total_questions");
                    double topicAccuracy = totalQuestions > 0 ? (totalCorrect * 100.0 / totalQuestions) : 0;
                    topicAccuracyMap.put(rs.getString("topic"), topicAccuracy);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int totalQuestions = 0;
        int totalCorrect = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int score = Integer.parseInt(model.getValueAt(i, 1).toString());
                double accuracy = Double.parseDouble(model.getValueAt(i, 2).toString());
                totalCorrect += score;
                totalQuestions += accuracy > 0 ? (int) (score * 100 / accuracy) : 0;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (topicAccuracyMap.isEmpty()) {
            topicAccuracyMap.put("No topic data", 0.0);
        }

        new AnalyticsDashboardFrame(totalQuestions, totalCorrect, topicAccuracyMap);
    }

    private void ensureTopicStatsTable(Connection conn) throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS result_topic_stats (
                    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    result_id INT NOT NULL,
                    topic VARCHAR(100) NOT NULL,
                    correct_count INT NOT NULL,
                    total_count INT NOT NULL,
                    CONSTRAINT fk_result_topic_stats_result
                        FOREIGN KEY (result_id) REFERENCES results(id)
                        ON DELETE CASCADE
                )
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void loadResults(DefaultTableModel model) throws Exception {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new Exception("Database connection is null");
        }

        String sql = "SELECT id, score, accuracy, time_taken, date FROM results WHERE user_id = ? ORDER BY date DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("score"),
                            String.format("%.2f", rs.getDouble("accuracy")),
                            rs.getInt("time_taken"),
                            rs.getTimestamp("date")
                    });
                }
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No previous results found for this user.", "No Results", JOptionPane.INFORMATION_MESSAGE);
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
}
