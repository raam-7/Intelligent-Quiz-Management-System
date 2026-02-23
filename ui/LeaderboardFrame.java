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
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"Rank", "Name", "Score", "Accuracy"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        loadLeaderboard(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

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
        }
    }
}