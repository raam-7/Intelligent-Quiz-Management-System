package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManageQuestionsFrame extends JFrame {

    private JTextArea questionField;
    private JTextField opt1Field;
    private JTextField opt2Field;
    private JTextField opt3Field;
    private JTextField opt4Field;
    private JComboBox<String> correctOptionBox;
    private JTextField topicField;
    private JComboBox<String> difficultyBox;
    private JTable table;
    private DefaultTableModel model;

    private int selectedQuestionId = -1;

    public ManageQuestionsFrame() {
        setTitle("Manage Questions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1240, 760);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Question Manager", "Add, edit, and maintain your quiz bank with reliable form controls."), BorderLayout.NORTH);

        String[] columns = {
                "ID", "Question", "Option1", "Option2",
                "Option3", "Option4", "Correct", "Topic", "Difficulty"
        };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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
        JScrollPane formScroll = new JScrollPane(createFormPanel());
        ModernTheme.styleScrollPane(formScroll);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formCard.add(formScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, formCard);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(720);
        splitPane.setResizeWeight(0.66);

        page.add(splitPane, BorderLayout.CENTER);
        add(page);

        loadQuestions();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedQuestionId = Integer.parseInt(model.getValueAt(row, 0).toString());
                questionField.setText(model.getValueAt(row, 1).toString());
                opt1Field.setText(model.getValueAt(row, 2).toString());
                opt2Field.setText(model.getValueAt(row, 3).toString());
                opt3Field.setText(model.getValueAt(row, 4).toString());
                opt4Field.setText(model.getValueAt(row, 5).toString());
                correctOptionBox.setSelectedItem(model.getValueAt(row, 6).toString());
                topicField.setText(model.getValueAt(row, 7).toString());
                difficultyBox.setSelectedItem(model.getValueAt(row, 8).toString());
            }
        });

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 12, 0);

        questionField = new JTextArea(4, 20);
        configurePlainArea(questionField);
        JScrollPane questionScroll = new JScrollPane(questionField);
        questionScroll.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true));
        questionScroll.setPreferredSize(new Dimension(320, 110));

        opt1Field = createPlainField();
        opt2Field = createPlainField();
        opt3Field = createPlainField();
        opt4Field = createPlainField();
        topicField = createPlainField();

        correctOptionBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        configurePlainCombo(correctOptionBox);

        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        configurePlainCombo(difficultyBox);

        form.add(createFieldBlock("Question", questionScroll), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Option 1", opt1Field), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Option 2", opt2Field), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Option 3", opt3Field), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Option 4", opt4Field), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Correct Option", correctOptionBox), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Topic", topicField), gbc);
        gbc.gridy++;
        form.add(createFieldBlock("Difficulty", difficultyBox), gbc);

        JButton addBtn = new JButton("Add Question");
        ModernTheme.styleButton(addBtn);
        JButton updateBtn = new JButton("Update Question");
        ModernTheme.styleSecondaryButton(updateBtn);
        JButton deleteBtn = new JButton("Delete Question");
        ModernTheme.styleDangerButton(deleteBtn);
        JButton clearBtn = new JButton("Clear Form");
        ModernTheme.styleSecondaryButton(clearBtn);
        JButton importBtn = new JButton("Import CSV");
        ModernTheme.styleButton(importBtn);
        JButton sampleBtn = new JButton("Download Sample CSV");
        ModernTheme.styleSecondaryButton(sampleBtn);

        addBtn.addActionListener(e -> addQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());
        clearBtn.addActionListener(e -> clearFields());
        importBtn.addActionListener(e -> importQuestionsFromCsv());
        sampleBtn.addActionListener(e -> downloadSampleCsv());

        JPanel actions = new JPanel(new GridLayout(6, 1, 0, 10));
        actions.setOpaque(false);
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(clearBtn);
        actions.add(importBtn);
        actions.add(sampleBtn);

        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 0, 0);
        form.add(actions, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 0, 0);
        JLabel helpLabel = ModernTheme.createSubtleLabel("<html>CSV format: <b>question_text, option1, option2, option3, option4, correct_option, topic, difficulty</b><br>You can include a header row.</html>");
        form.add(helpLabel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(Box.createVerticalGlue(), gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);

        JLabel label = ModernTheme.createSubtleLabel(labelText);
        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
    }

    private JTextField createPlainField() {
        JTextField field = new JTextField();
        configurePlainField(field);
        return field;
    }

    private void configurePlainField(JTextField field) {
        field.setFont(ModernTheme.LABEL_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setEditable(true);
        field.setEnabled(true);
        field.setFocusable(true);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(320, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private void configurePlainArea(JTextArea area) {
        area.setFont(ModernTheme.LABEL_FONT);
        area.setBackground(Color.WHITE);
        area.setForeground(Color.BLACK);
        area.setCaretColor(Color.BLACK);
        area.setEditable(true);
        area.setEnabled(true);
        area.setFocusable(true);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    private void configurePlainCombo(JComboBox<String> combo) {
        combo.setFont(ModernTheme.LABEL_FONT);
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setFocusable(true);
        combo.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true));
        combo.setPreferredSize(new Dimension(320, 42));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private void loadQuestions() {
        model.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions ORDER BY id DESC");

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
        if (!validateForm()) {
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, topic, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, questionField.getText().trim());
            pst.setString(2, opt1Field.getText().trim());
            pst.setString(3, opt2Field.getText().trim());
            pst.setString(4, opt3Field.getText().trim());
            pst.setString(5, opt4Field.getText().trim());
            pst.setInt(6, Integer.parseInt(String.valueOf(correctOptionBox.getSelectedItem())));
            pst.setString(7, topicField.getText().trim());
            pst.setString(8, String.valueOf(difficultyBox.getSelectedItem()));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question added successfully.");
            loadQuestions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to add question.");
        }
    }

    private void updateQuestion() {
        if (selectedQuestionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first.");
            return;
        }

        if (!validateForm()) {
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
            pst.setInt(6, Integer.parseInt(String.valueOf(correctOptionBox.getSelectedItem())));
            pst.setString(7, topicField.getText().trim());
            pst.setString(8, String.valueOf(difficultyBox.getSelectedItem()));
            pst.setInt(9, selectedQuestionId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question updated successfully.");
            loadQuestions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to update question.");
        }
    }

    private void deleteQuestion() {
        if (selectedQuestionId == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected question?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM questions WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, selectedQuestionId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Question deleted successfully.");
            loadQuestions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to delete question.");
        }
    }

    private void importQuestionsFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Questions CSV");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        List<String[]> rows = readCsvRows(file);
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid question rows were found in the CSV file.");
            return;
        }

        int importedCount = 0;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, topic, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            for (String[] row : rows) {
                pst.setString(1, row[0].trim());
                pst.setString(2, row[1].trim());
                pst.setString(3, row[2].trim());
                pst.setString(4, row[3].trim());
                pst.setString(5, row[4].trim());
                pst.setInt(6, Integer.parseInt(row[5].trim()));
                pst.setString(7, row[6].trim());
                pst.setString(8, normalizeDifficulty(row[7].trim()));
                pst.addBatch();
                importedCount++;
            }

            pst.executeBatch();
            loadQuestions();
            clearFields();

            if (!rows.isEmpty()) {
                fillFormFromRow(rows.get(0));
            }

            JOptionPane.showMessageDialog(this, importedCount + " question(s) imported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to import questions from CSV.");
        }
    }

    private List<String[]> readCsvRows(File file) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstRow = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = parseCsvLine(line);
                if (values.length < 8) {
                    continue;
                }

                if (firstRow && "question_text".equalsIgnoreCase(values[0].trim())) {
                    firstRow = false;
                    continue;
                }

                if (!isValidCsvRow(values)) {
                    continue;
                }

                rows.add(values);
                firstRow = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading CSV file.");
        }

        return rows;
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    private boolean isValidCsvRow(String[] values) {
        if (values[0].trim().isEmpty()
                || values[1].trim().isEmpty()
                || values[2].trim().isEmpty()
                || values[3].trim().isEmpty()
                || values[4].trim().isEmpty()
                || values[6].trim().isEmpty()
                || values[7].trim().isEmpty()) {
            return false;
        }

        String correctOption = values[5].trim();
        return "1".equals(correctOption) || "2".equals(correctOption) || "3".equals(correctOption) || "4".equals(correctOption);
    }

    private String normalizeDifficulty(String difficulty) {
        if ("medium".equalsIgnoreCase(difficulty)) {
            return "Medium";
        }
        if ("hard".equalsIgnoreCase(difficulty)) {
            return "Hard";
        }
        return "Easy";
    }

    private void fillFormFromRow(String[] row) {
        questionField.setText(row[0].trim());
        opt1Field.setText(row[1].trim());
        opt2Field.setText(row[2].trim());
        opt3Field.setText(row[3].trim());
        opt4Field.setText(row[4].trim());
        correctOptionBox.setSelectedItem(row[5].trim());
        topicField.setText(row[6].trim());
        difficultyBox.setSelectedItem(normalizeDifficulty(row[7].trim()));
    }

    private void downloadSampleCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sample CSV");
        fileChooser.setSelectedFile(new File("questions_sample.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("question_text,option1,option2,option3,option4,correct_option,topic,difficulty\n");
            writer.write("\"What is JVM?\",\"Java Virtual Machine\",\"Java Very Much\",\"Joint Virtual Method\",\"None\",1,\"Java\",\"Easy\"\n");
            writer.write("\"Which keyword is used for inheritance?\",\"extends\",\"implements\",\"inherit\",\"super\",1,\"OOP\",\"Easy\"\n");
            writer.write("\"Which collection allows duplicate values?\",\"Set\",\"List\",\"Map\",\"Queue\",2,\"Collections\",\"Medium\"\n");

            JOptionPane.showMessageDialog(this, "Sample CSV downloaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to create sample CSV.");
        }
    }

    private boolean validateForm() {
        if (questionField.getText().trim().isEmpty()
                || opt1Field.getText().trim().isEmpty()
                || opt2Field.getText().trim().isEmpty()
                || opt3Field.getText().trim().isEmpty()
                || opt4Field.getText().trim().isEmpty()
                || topicField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all question details.");
            return false;
        }

        String correctOption = String.valueOf(correctOptionBox.getSelectedItem());
        if (!"1".equals(correctOption) && !"2".equals(correctOption) && !"3".equals(correctOption) && !"4".equals(correctOption)) {
            JOptionPane.showMessageDialog(this, "Correct option must be between 1 and 4.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        questionField.setText("");
        opt1Field.setText("");
        opt2Field.setText("");
        opt3Field.setText("");
        opt4Field.setText("");
        correctOptionBox.setSelectedIndex(0);
        topicField.setText("");
        difficultyBox.setSelectedItem("Easy");
        table.clearSelection();
        selectedQuestionId = -1;
        SwingUtilities.invokeLater(() -> questionField.requestFocusInWindow());
    }
}
