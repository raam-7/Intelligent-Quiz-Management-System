package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManageQuestionsFrame extends JFrame {

    private JTextField questionField;
    private JTextField opt1Field;
    private JTextField opt2Field;
    private JTextField opt3Field;
    private JTextField opt4Field;
    private JTextField correctOptionField;
    private JTextField topicField;
    private JTextField difficultyField;
    private JTable table;
    private DefaultTableModel model;

    private int selectedQuestionId = -1;

    public ManageQuestionsFrame() {
        setTitle("Manage Questions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.prepareFrame(this, 1220, 720);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Question Manager", "Edit the quiz bank with a cleaner table, better form spacing, and faster actions."), BorderLayout.NORTH);

        String[] columns = {
                "ID", "Question", "Option1", "Option2",
                "Option3", "Option4", "Correct", "Topic", "Difficulty"
        };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        ModernTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(table);
        ModernTheme.styleScrollPane(tableScroll);

        JPanel tableCard = ModernTheme.createCardPanel(new BorderLayout(0, 14));
        tableCard.add(ModernTheme.createSectionTitle("Question Library"), BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        JPanel formCard = ModernTheme.createCardPanel(new BorderLayout(0, 18));
        formCard.add(ModernTheme.createSectionTitle("Question Details"), BorderLayout.NORTH);
        formCard.add(createFormPanel(), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, formCard);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(720);
        splitPane.setResizeWeight(0.68);

        page.add(splitPane, BorderLayout.CENTER);
        add(page);

        loadQuestions();

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

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);

        questionField = new JTextField();
        opt1Field = new JTextField();
        opt2Field = new JTextField();
        opt3Field = new JTextField();
        opt4Field = new JTextField();
        correctOptionField = new JTextField();
        topicField = new JTextField();
        difficultyField = new JTextField();

        JTextField[] fields = {
                questionField, opt1Field, opt2Field, opt3Field,
                opt4Field, correctOptionField, topicField, difficultyField
        };
        for (JTextField field : fields) {
            ModernTheme.styleTextField(field);
        }

        form.add(createFieldBlock("Question", questionField));
        form.add(createFieldBlock("Option 1", opt1Field));
        form.add(createFieldBlock("Option 2", opt2Field));
        form.add(createFieldBlock("Option 3", opt3Field));
        form.add(createFieldBlock("Option 4", opt4Field));
        form.add(createFieldBlock("Correct Option (1-4)", correctOptionField));
        form.add(createFieldBlock("Topic", topicField));
        form.add(createFieldBlock("Difficulty", difficultyField));

        JButton addBtn = new JButton("Add");
        ModernTheme.styleButton(addBtn);
        JButton updateBtn = new JButton("Update");
        ModernTheme.styleSecondaryButton(updateBtn);
        JButton deleteBtn = new JButton("Delete");
        ModernTheme.styleDangerButton(deleteBtn);
        JButton clearBtn = new JButton("Clear");
        ModernTheme.styleSecondaryButton(clearBtn);

        addBtn.addActionListener(e -> addQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());
        clearBtn.addActionListener(e -> clearFields());

        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(clearBtn);

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(actions, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createFieldBlock(String labelText, JTextField field) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        block.add(ModernTheme.createSubtleLabel(labelText), BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
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
            JOptionPane.showMessageDialog(this, "Unable to load questions.");
        }
    }

    private void addQuestion() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, topic, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, questionField.getText().trim());
            pst.setString(2, opt1Field.getText().trim());
            pst.setString(3, opt2Field.getText().trim());
            pst.setString(4, opt3Field.getText().trim());
            pst.setString(5, opt4Field.getText().trim());
            pst.setInt(6, Integer.parseInt(correctOptionField.getText().trim()));
            pst.setString(7, topicField.getText().trim());
            pst.setString(8, difficultyField.getText().trim());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question Added Successfully!");
            loadQuestions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to add question.");
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

            pst.setString(1, questionField.getText().trim());
            pst.setString(2, opt1Field.getText().trim());
            pst.setString(3, opt2Field.getText().trim());
            pst.setString(4, opt3Field.getText().trim());
            pst.setString(5, opt4Field.getText().trim());
            pst.setInt(6, Integer.parseInt(correctOptionField.getText().trim()));
            pst.setString(7, topicField.getText().trim());
            pst.setString(8, difficultyField.getText().trim());
            pst.setInt(9, selectedQuestionId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question Updated Successfully!");
            loadQuestions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to update question.");
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
            JOptionPane.showMessageDialog(this, "Unable to delete question.");
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
        table.clearSelection();
        selectedQuestionId = -1;
    }
}
