package ui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private int userId;

    public UserDashboard(int userId) {

        this.userId = userId;

        setTitle("User Dashboard");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome User - ID: " + userId, JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,10,10)); // ✅ changed to 4 rows
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JButton startQuizBtn = new JButton("Start Quiz");
        JButton viewResultsBtn = new JButton("View Results");
        JButton leaderboardBtn = new JButton("Leaderboard");
        JButton logoutBtn = new JButton("Logout");

        panel.add(startQuizBtn);
        panel.add(viewResultsBtn);
        panel.add(leaderboardBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        // ✅ Start Quiz
        startQuizBtn.addActionListener(e -> {
            new QuizFrame(userId);
            dispose();
        });

        // ✅ Leaderboard (THIS WAS MISSING)
        leaderboardBtn.addActionListener(e -> {
            new LeaderboardFrame();
        });

        // ✅ Logout
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }
}