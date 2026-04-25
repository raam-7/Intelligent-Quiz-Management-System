package ui;

import database.DBConnection;
import models.Question;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizFrame extends JFrame {

    private final int userId;
    private final ArrayList<Question> questions = new ArrayList<>();
    private final String selectedTopic;
    private final String selectedDifficulty;
    private final int requestedQuestionCount;
    private int currentIndex = 0;
    private int score = 0;

    private JLabel questionLabel;
    private JLabel timerLabel;
    private JLabel difficultyLabel;
    private JLabel progressLabel;
    private JRadioButton opt1;
    private JRadioButton opt2;
    private JRadioButton opt3;
    private JRadioButton opt4;
    private ButtonGroup group;

    private Timer timer;
    private int timeLeft = 15;
    private int totalTimeTaken = 0;

    private final Map<String, Integer> topicCorrect = new HashMap<>();
    private final Map<String, Integer> topicTotal = new HashMap<>();

    private int correctInRow = 0;
    private String currentDifficulty = "Easy";

    public QuizFrame(int userId) {
        this(userId, "All Topics", "All Difficulties", 10);
    }

    public QuizFrame(int userId, String selectedTopic, String selectedDifficulty, int requestedQuestionCount) {
        this.userId = userId;
        this.selectedTopic = selectedTopic;
        this.selectedDifficulty = selectedDifficulty;
        this.requestedQuestionCount = requestedQuestionCount;
        if ("Medium".equalsIgnoreCase(selectedDifficulty)) {
            this.currentDifficulty = "Medium";
        } else if ("Hard".equalsIgnoreCase(selectedDifficulty)) {
            this.currentDifficulty = "Hard";
        }

        setTitle("Intelligent Quiz System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        ModernTheme.prepareFrame(this, 980, 650);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(createTopPanel(), BorderLayout.NORTH);
        page.add(createQuestionCard(), BorderLayout.CENTER);

        JButton nextBtn = new JButton("Next Question");
        ModernTheme.styleButton(nextBtn);
        nextBtn.addActionListener(e -> nextQuestion());
        JPanel footer = ModernTheme.createSectionPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(nextBtn);
        page.add(footer, BorderLayout.SOUTH);

        add(page);

        loadQuestions();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No questions matched the selected filters.\nTopic: " + selectedTopic
                            + "\nDifficulty: " + selectedDifficulty
                            + "\nTry 'All Topics' or 'All Difficulties', or add matching questions from admin."
            );
            new UserDashboard(userId);
            dispose();
        } else {
            displayQuestion();
            startTimer();
        }

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel card = ModernTheme.createCardPanel(new BorderLayout(16, 0));

        JPanel left = new JPanel(new GridLayout(0, 1, 0, 6));
        left.setOpaque(false);
        left.add(ModernTheme.createSectionTitle("Interactive Quiz Session"));
        progressLabel = ModernTheme.createSubtleLabel("Question 1");
        left.add(progressLabel);
        left.add(ModernTheme.createSubtleLabel("Filter: " + selectedTopic + "  |  " + selectedDifficulty + "  |  " + requestedQuestionCount + " questions"));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        timerLabel = ModernTheme.createSubtleLabel("Time Left: 15s");
        timerLabel.setForeground(ModernTheme.DANGER_COLOR);

        difficultyLabel = ModernTheme.createSubtleLabel("Level: Easy");
        difficultyLabel.setForeground(ModernTheme.SUCCESS_COLOR);

        right.add(timerLabel);
        right.add(difficultyLabel);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel createQuestionCard() {
        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 20));

        questionLabel = new JLabel();
        questionLabel.setFont(ModernTheme.HEADER_FONT);
        questionLabel.setForeground(ModernTheme.TEXT_COLOR);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        optionsPanel.setOpaque(false);

        opt1 = new JRadioButton();
        opt2 = new JRadioButton();
        opt3 = new JRadioButton();
        opt4 = new JRadioButton();
        ModernTheme.styleRadioButton(opt1);
        ModernTheme.styleRadioButton(opt2);
        ModernTheme.styleRadioButton(opt3);
        ModernTheme.styleRadioButton(opt4);

        group = new ButtonGroup();
        group.add(opt1);
        group.add(opt2);
        group.add(opt3);
        group.add(opt4);

        optionsPanel.add(opt1);
        optionsPanel.add(opt2);
        optionsPanel.add(opt3);
        optionsPanel.add(opt4);

        card.add(questionLabel, BorderLayout.NORTH);
        card.add(optionsPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadQuestions() {
        try {
            Connection conn = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM questions WHERE 1=1");

            if (!"All Topics".equalsIgnoreCase(selectedTopic)) {
                sql.append(" AND LOWER(TRIM(topic)) = LOWER(TRIM(?))");
            }
            if (!"All Difficulties".equalsIgnoreCase(selectedDifficulty)) {
                sql.append(" AND LOWER(TRIM(difficulty)) = LOWER(TRIM(?))");
            }

            sql.append(" ORDER BY id DESC");

            PreparedStatement pst = conn.prepareStatement(sql.toString());
            int parameterIndex = 1;

            if (!"All Topics".equalsIgnoreCase(selectedTopic)) {
                pst.setString(parameterIndex++, selectedTopic.trim());
            }
            if (!"All Difficulties".equalsIgnoreCase(selectedDifficulty)) {
                pst.setString(parameterIndex, selectedDifficulty.trim());
            }

            ResultSet rs = pst.executeQuery();

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

            Collections.shuffle(questions);
            if (questions.size() > requestedQuestionCount) {
                questions.subList(requestedQuestionCount, questions.size()).clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        Question q = questions.get(currentIndex);
        questionLabel.setText("<html><body style='width:720px'>Q" + (currentIndex + 1) + ". " + q.getQuestionText() + "</body></html>");
        opt1.setText(q.getOption1());
        opt2.setText(q.getOption2());
        opt3.setText(q.getOption3());
        opt4.setText(q.getOption4());
        progressLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size() + "  |  Topic: " + q.getTopic());

        group.clearSelection();
        timeLeft = 15;
        timerLabel.setText("Time Left: 15s");
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            totalTimeTaken++;
            timerLabel.setText("Time Left: " + timeLeft + "s");

            if (timeLeft <= 5) {
                timerLabel.setForeground(ModernTheme.WARNING_COLOR);
            }
            if (timeLeft <= 2) {
                timerLabel.setForeground(ModernTheme.DANGER_COLOR);
            }

            if (timeLeft <= 0) {
                nextQuestion();
            }
        });
        timer.start();
    }

    private void nextQuestion() {
        Question currentQ = questions.get(currentIndex);
        String topic = currentQ.getTopic();
        topicTotal.put(topic, topicTotal.getOrDefault(topic, 0) + 1);

        int selected = -1;
        if (opt1.isSelected()) {
            selected = 1;
        }
        if (opt2.isSelected()) {
            selected = 2;
        }
        if (opt3.isSelected()) {
            selected = 3;
        }
        if (opt4.isSelected()) {
            selected = 4;
        }

        if (selected == currentQ.getCorrectOption()) {
            score++;
            topicCorrect.put(topic, topicCorrect.getOrDefault(topic, 0) + 1);
            correctInRow++;

            if (correctInRow >= 2) {
                if ("Easy".equals(currentDifficulty)) {
                    currentDifficulty = "Medium";
                    difficultyLabel.setForeground(ModernTheme.WARNING_COLOR);
                } else if ("Medium".equals(currentDifficulty)) {
                    currentDifficulty = "Hard";
                    difficultyLabel.setForeground(ModernTheme.DANGER_COLOR);
                }
                correctInRow = 0;
            }
        } else {
            correctInRow = 0;
            if ("Hard".equals(currentDifficulty)) {
                currentDifficulty = "Medium";
                difficultyLabel.setForeground(ModernTheme.WARNING_COLOR);
            } else if ("Medium".equals(currentDifficulty)) {
                currentDifficulty = "Easy";
                difficultyLabel.setForeground(ModernTheme.SUCCESS_COLOR);
            }
        }

        difficultyLabel.setText("Level: " + currentDifficulty);
        currentIndex++;

        if (currentIndex < questions.size()) {
            timerLabel.setForeground(ModernTheme.DANGER_COLOR);
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

    private void showAnalyticsReport(double accuracy) {
        StringBuilder report = new StringBuilder();
        report.append("===== PERFORMANCE REPORT =====\n\n");
        report.append("Total Questions: ").append(questions.size()).append("\n");
        report.append("Correct Answers: ").append(score).append("\n");
        report.append("Accuracy: ").append(String.format("%.2f", accuracy)).append("%\n");
        report.append("Total Time Taken: ").append(totalTimeTaken).append(" seconds\n\n");
        report.append("Topic-wise Performance:\n");

        String weakTopic = "";
        double lowestAccuracy = 100;

        for (String topicName : topicTotal.keySet()) {
            int correct = topicCorrect.getOrDefault(topicName, 0);
            int total = topicTotal.get(topicName);
            double topicAccuracy = (correct * 100.0) / total;
            report.append(topicName).append(": ").append(String.format("%.2f", topicAccuracy)).append("%\n");

            if (topicAccuracy < lowestAccuracy) {
                lowestAccuracy = topicAccuracy;
                weakTopic = topicName;
            }
        }

        report.append("\nWeak Area: ").append(weakTopic);
        report.append("\nRecommendation: Focus more on ").append(weakTopic).append(" concepts.");

        JOptionPane.showMessageDialog(this, report.toString());
    }

    private void saveResult(int score, double accuracy, int timeTaken) {
        try {
            Connection conn = DBConnection.getConnection();
            ensureTopicStatsTable(conn);
            String sql = "INSERT INTO results (user_id, score, accuracy, time_taken) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setInt(1, userId);
            pst.setInt(2, score);
            pst.setDouble(3, accuracy);
            pst.setInt(4, timeTaken);
            pst.executeUpdate();

            int resultId = -1;
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                resultId = generatedKeys.getInt(1);
            }

            if (resultId != -1) {
                saveTopicStats(conn, resultId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void saveTopicStats(Connection conn, int resultId) throws Exception {
        String sql = "INSERT INTO result_topic_stats (result_id, topic, correct_count, total_count) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (String topic : topicTotal.keySet()) {
                pst.setInt(1, resultId);
                pst.setString(2, topic);
                pst.setInt(3, topicCorrect.getOrDefault(topic, 0));
                pst.setInt(4, topicTotal.getOrDefault(topic, 0));
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }
}
