package ui;

import database.DBConnection;
import models.Question;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizFrame extends JFrame {

    private int userId;
    private ArrayList<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private JLabel questionLabel;
    private JLabel timerLabel;
    private JRadioButton opt1, opt2, opt3, opt4;
    private ButtonGroup group;

    private Timer timer;
    private int timeLeft = 15; // seconds per question
    private int totalTimeTaken = 0;

    // 🔥 Topic analytics
    private Map<String, Integer> topicCorrect = new HashMap<>();
    private Map<String, Integer> topicTotal = new HashMap<>();
    
    // 🧠 Adaptive Difficulty System
    private int correctInRow = 0;
    private String currentDifficulty = "Easy";
    private JLabel difficultyLabel;

    public QuizFrame(int userId) {

        this.userId = userId;

        setTitle("Intelligent Quiz System");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND_COLOR);

        // Top Panel with Question and Timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ModernTheme.PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel("", JLabel.CENTER);
        ModernTheme.styleLabel(questionLabel, 2);
        questionLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ModernTheme.PRIMARY_COLOR);
        timerLabel = new JLabel("Time Left: 15s", JLabel.RIGHT);
        timerLabel.setFont(ModernTheme.BUTTON_FONT);
        timerLabel.setForeground(ModernTheme.DANGER_COLOR);
        
        difficultyLabel = new JLabel("Level: " + currentDifficulty, JLabel.RIGHT);
        difficultyLabel.setFont(ModernTheme.BUTTON_FONT);
        difficultyLabel.setForeground(ModernTheme.SUCCESS_COLOR);
        
        rightPanel.add(timerLabel);
        rightPanel.add(new JLabel("  |  "));
        rightPanel.add(difficultyLabel);

        topPanel.add(questionLabel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        optionsPanel.setBackground(ModernTheme.BACKGROUND_COLOR);

        opt1 = new JRadioButton();
        opt2 = new JRadioButton();
        opt3 = new JRadioButton();
        opt4 = new JRadioButton();

        opt1.setFont(ModernTheme.LABEL_FONT);
        opt2.setFont(ModernTheme.LABEL_FONT);
        opt3.setFont(ModernTheme.LABEL_FONT);
        opt4.setFont(ModernTheme.LABEL_FONT);
        
        opt1.setBackground(ModernTheme.BACKGROUND_COLOR);
        opt2.setBackground(ModernTheme.BACKGROUND_COLOR);
        opt3.setBackground(ModernTheme.BACKGROUND_COLOR);
        opt4.setBackground(ModernTheme.BACKGROUND_COLOR);

        group = new ButtonGroup();
        group.add(opt1);
        group.add(opt2);
        group.add(opt3);
        group.add(opt4);

        optionsPanel.add(opt1);
        optionsPanel.add(opt2);
        optionsPanel.add(opt3);
        optionsPanel.add(opt4);

        add(optionsPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(ModernTheme.BACKGROUND_COLOR);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton nextBtn = new JButton("Next →");
        ModernTheme.styleButton(nextBtn);
        southPanel.add(nextBtn);
        
        add(southPanel, BorderLayout.SOUTH);

        nextBtn.addActionListener(e -> nextQuestion());

        loadQuestions();

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available!");
            new UserDashboard(userId);
            dispose();
        } else {
            displayQuestion();
            startTimer();
        }

        setVisible(true);
    }

    // 🔹 Load Questions
    private void loadQuestions() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions");

            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getInt("correct_option"),
                        rs.getString("topic"),
                        rs.getString("difficulty")
                ));
            }
            
            // 🎲 Randomize question order
            Collections.shuffle(questions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Display Question
    private void displayQuestion() {

        Question q = questions.get(currentIndex);

        questionLabel.setText("Q" + (currentIndex + 1) + ": " + q.getQuestionText());
        opt1.setText(q.getOption1());
        opt2.setText(q.getOption2());
        opt3.setText(q.getOption3());
        opt4.setText(q.getOption4());

        group.clearSelection();
        timeLeft = 15;
        timerLabel.setText("Time Left: 15s");
    }

    // 🔹 Start Timer
    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            totalTimeTaken++;
            timerLabel.setText("Time Left: " + timeLeft + "s");

            if (timeLeft <= 0) {
                nextQuestion();
            }
        });
        timer.start();
    }

    // 🔹 Next Question Logic
    private void nextQuestion() {

        Question currentQ = questions.get(currentIndex);
        String topic = currentQ.getTopic();

        topicTotal.put(topic, topicTotal.getOrDefault(topic, 0) + 1);

        int selected = -1;
        if (opt1.isSelected()) selected = 1;
        if (opt2.isSelected()) selected = 2;
        if (opt3.isSelected()) selected = 3;
        if (opt4.isSelected()) selected = 4;

        if (selected == currentQ.getCorrectOption()) {
            score++;
            topicCorrect.put(topic, topicCorrect.getOrDefault(topic, 0) + 1);
            
            // 🧠 Adaptive Difficulty: Increase on 2 consecutive correct
            correctInRow++;
            if (correctInRow >= 2) {
                if (currentDifficulty.equals("Easy")) {
                    currentDifficulty = "Medium";
                    difficultyLabel.setText("Level: " + currentDifficulty);
                    difficultyLabel.setForeground(new Color(255, 165, 0));
                } else if (currentDifficulty.equals("Medium")) {
                    currentDifficulty = "Hard";
                    difficultyLabel.setText("Level: " + currentDifficulty);
                    difficultyLabel.setForeground(Color.RED);
                }
                correctInRow = 0;
            }
        } else {
            // 🧠 Adaptive Difficulty: Decrease on wrong answer
            correctInRow = 0;
            if (currentDifficulty.equals("Hard")) {
                currentDifficulty = "Medium";
                difficultyLabel.setText("Level: " + currentDifficulty);
                difficultyLabel.setForeground(new Color(255, 165, 0));
            } else if (currentDifficulty.equals("Medium")) {
                currentDifficulty = "Easy";
                difficultyLabel.setText("Level: " + currentDifficulty);
                difficultyLabel.setForeground(new Color(0, 128, 0));
            }
        }

        currentIndex++;

        if (currentIndex < questions.size()) {
            displayQuestion();
        } else {

            timer.stop();

            double accuracy = (score * 100.0) / questions.size();
            saveResult(score, accuracy, totalTimeTaken);

            showAnalyticsReport(accuracy);
            new UserDashboard(userId);
            dispose();
        }
    }

    // 🔹 Show Intelligent Analytics Report
    private void showAnalyticsReport(double accuracy) {

        StringBuilder report = new StringBuilder();

        report.append("===== PERFORMANCE REPORT =====\n\n");
        report.append("Total Questions: ").append(questions.size()).append("\n");
        report.append("Correct Answers: ").append(score).append("\n");
        report.append("Accuracy: ")
                .append(String.format("%.2f", accuracy)).append("%\n");
        report.append("Total Time Taken: ")
                .append(totalTimeTaken).append(" seconds\n\n");

        report.append("Topic-wise Performance:\n");

        String weakTopic = "";
        double lowestAccuracy = 100;

        for (String topicName : topicTotal.keySet()) {

            int correct = topicCorrect.getOrDefault(topicName, 0);
            int total = topicTotal.get(topicName);

            double topicAccuracy = (correct * 100.0) / total;

            report.append(topicName)
                    .append(": ")
                    .append(String.format("%.2f", topicAccuracy))
                    .append("%\n");

            if (topicAccuracy < lowestAccuracy) {
                lowestAccuracy = topicAccuracy;
                weakTopic = topicName;
            }
        }

        report.append("\nWeak Area: ").append(weakTopic);
        report.append("\nRecommendation: Focus more on ")
                .append(weakTopic).append(" concepts.");

        JOptionPane.showMessageDialog(this, report.toString());
    }

    // 🔹 Save Result
    private void saveResult(int score, double accuracy, int timeTaken) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO results (user_id, score, accuracy, time_taken) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, userId);
            pst.setInt(2, score);
            pst.setDouble(3, accuracy);
            pst.setInt(4, timeTaken);

            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}