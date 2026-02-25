package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageQuestionsFrame extends JFrame {

    private JTextField questionField, opt1Field, opt2Field, opt3Field, opt4Field;
    private JTextField correctOptionField, topicField, difficultyField;
    private JTable table;
    private DefaultTableModel model;

    private int selectedQuestionId = -1;

    public ManageQuestionsFrame() {

        setTitle("Manage Questions");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------------- TABLE ----------------
        String[] columns = {
                "ID", "Question", "Option1", "Option2",
                "Option3", "Option4", "Correct", "Topic", "Difficulty"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        loadQuestions();

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------------- FORM PANEL ----------------
        JPanel formPanel = new JPanel(new GridLayout(9,2,10,10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Question Details"));

        questionField = new JTextField();
        opt1Field = new JTextField();
        opt2Field = new JTextField();
        opt3Field = new JTextField();
        opt4Field = new JTextField();
        correctOptionField = new JTextField();
        topicField = new JTextField();
        difficultyField = new JTextField();

        formPanel.add(new JLabel("Question:"));
        formPanel.add(questionField);

        formPanel.add(new JLabel("Option 1:"));
        formPanel.add(opt1Field);

        formPanel.add(new JLabel("Option 2:"));
        formPanel.add(opt2Field);

        formPanel.add(new JLabel("Option 3:"));
        formPanel.add(opt3Field);

        formPanel.add(new JLabel("Option 4:"));
        formPanel.add(opt4Field);

        formPanel.add(new JLabel("Correct Option (1-4):"));
        formPanel.add(correctOptionField);

        formPanel.add(new JLabel("Topic:"));
        formPanel.add(topicField);

        formPanel.add(new JLabel("Difficulty:"));
        formPanel.add(difficultyField);

        // ---------------- BUTTONS ----------------
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        formPanel.add(addBtn);
        formPanel.add(updateBtn);

        add(formPanel, BorderLayout.SOUTH);

        JPanel deletePanel = new JPanel();
        deletePanel.add(deleteBtn);
        add(deletePanel, BorderLayout.NORTH);

        // ---------------- EVENTS ----------------

        // Table Row Selection
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedQuestionId = Integer.parseInt(model.getValueAt(row, 0).toString());
                questionField.setText(model.getValueAt(row, 1).toString());
                opt1Field.setText(model.getValueAt(row, 2).toString());
                opt2Field.setText(model.getValueAt(row, 3).toString());
                opt3Field.setText(model.getValueAt(row, 4).toString());
                opt4Field.setText(model.getValueAt(row, 5).toString());
                correctOptionField.setText(model.getValueAt(row, 6).toString());
                topicField.setText(model.getValueAt(row, 7).toString());
                difficultyField.setText(model.getValueAt(row, 8).toString());
            }
        });

        addBtn.addActionListener(e -> addQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());

        setVisible(true);
    }

    private void loadQuestions() {
        model.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getInt("correct_option"),
                        rs.getString("topic"),
                        rs.getString("difficulty")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addQuestion() {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, topic, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, questionField.getText());
            pst.setString(2, opt1Field.getText());
            pst.setString(3, opt2Field.getText());
            pst.setString(4, opt3Field.getText());
            pst.setString(5, opt4Field.getText());
            pst.setInt(6, Integer.parseInt(correctOptionField.getText()));
            pst.setString(7, topicField.getText());
            pst.setString(8, difficultyField.getText());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question Added Successfully!");
            loadQuestions();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateQuestion() {
        if (selectedQuestionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE questions SET question_text=?, option1=?, option2=?, option3=?, option4=?, correct_option=?, topic=?, difficulty=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, questionField.getText());
            pst.setString(2, opt1Field.getText());
            pst.setString(3, opt2Field.getText());
            pst.setString(4, opt3Field.getText());
            pst.setString(5, opt4Field.getText());
            pst.setInt(6, Integer.parseInt(correctOptionField.getText()));
            pst.setString(7, topicField.getText());
            pst.setString(8, difficultyField.getText());
            pst.setInt(9, selectedQuestionId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question Updated Successfully!");
            loadQuestions();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteQuestion() {
        if (selectedQuestionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM questions WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, selectedQuestionId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question Deleted Successfully!");
            loadQuestions();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        questionField.setText("");
        opt1Field.setText("");
        opt2Field.setText("");
        opt3Field.setText("");
        opt4Field.setText("");
        correctOptionField.setText("");
        topicField.setText("");
        difficultyField.setText("");
        selectedQuestionId = -1;
    }
}