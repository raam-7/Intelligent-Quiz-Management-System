package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LeaderboardFrame extends JFrame {

    public LeaderboardFrame() {
        setTitle("Leaderboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.prepareFrame(this, 760, 500);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Leaderboard", "Top quiz performers ranked by score and accuracy."), BorderLayout.NORTH);

        String[] columns = {"Rank", "Name", "Score", "Accuracy"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        ModernTheme.styleTable(table);

        loadLeaderboard(model);

        JScrollPane scrollPane = new JScrollPane(table);
        ModernTheme.styleScrollPane(scrollPane);

        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 14));
        card.add(ModernTheme.createSectionTitle("Top 5 Players"), BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        page.add(card, BorderLayout.CENTER);
        add(ModernTheme.createScrollPane(page));
        setVisible(true);
    }

    private void loadLeaderboard(DefaultTableModel model) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            String sql = """
                    SELECT u.name, r.score, r.accuracy
                    FROM results r
                    JOIN users u ON r.user_id = u.id
                    ORDER BY r.score DESC
                    LIMIT 5
                    """;

            ResultSet rs = stmt.executeQuery(sql);
            int rank = 1;

            while (rs.next()) {
                model.addRow(new Object[]{
                        rank++,
                        rs.getString("name"),
                        rs.getInt("score"),
                        String.format("%.2f%%", rs.getDouble("accuracy"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to load leaderboard.");
        }
    }
}
