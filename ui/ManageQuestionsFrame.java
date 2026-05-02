package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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

    private static final Color PAGE_TOP = new Color(4, 10, 26);
    private static final Color PAGE_BOTTOM = new Color(8, 18, 42);
    private static final Color CARD_TOP = new Color(18, 32, 58, 236);
    private static final Color CARD_BOTTOM = new Color(7, 18, 40, 238);
    private static final Color CARD_BORDER = new Color(52, 77, 118);
    private static final Color FIELD_BG = new Color(25, 39, 63);
    private static final Color FIELD_BORDER = new Color(80, 108, 150);
    private static final Color TEXT_MAIN = new Color(248, 251, 255);
    private static final Color TEXT_MUTED = new Color(190, 207, 235);
    private static final Color PURPLE = new Color(142, 72, 255);
    private static final Color BLUE = new Color(58, 156, 255);
    private static final Color GREEN = new Color(34, 218, 151);
    private static final Color ORANGE = new Color(255, 165, 64);
    private static final Color RED = new Color(239, 68, 90);

    private JTextArea questionField;
    private JTextField opt1Field;
    private JTextField opt2Field;
    private JTextField opt3Field;
    private JTextField opt4Field;
    private JComboBox<String> correctOptionBox;
    private JComboBox<String> topicField;
    private JComboBox<String> difficultyBox;
    private JTextField searchField;
    private JComboBox<String> topicFilterBox;
    private JLabel countLabel;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private int selectedQuestionId = -1;

    public ManageQuestionsFrame() {
        setTitle("Manage Questions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        ModernTheme.prepareFrame(this, 1320, 780);

        JPanel page = new ManagerPagePanel(new BorderLayout(0, 18));
        page.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        page.add(createHero(), BorderLayout.NORTH);

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
        styleManagerTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(table);
        styleScrollPane(tableScroll);

        JPanel tableCard = createLibraryCard(tableScroll);
        JPanel formCard = createDetailsCard();

        JPanel split = new JPanel(new GridLayout(1, 2, 24, 0));
        split.setOpaque(false);
        split.add(tableCard);
        split.add(formCard);
        page.add(split, BorderLayout.CENTER);
        add(ModernTheme.createScrollPane(page));

        loadQuestions();
        refreshTopicControls();
        applyFilters();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int row = table.convertRowIndexToModel(viewRow);
                selectedQuestionId = Integer.parseInt(model.getValueAt(row, 0).toString());
                questionField.setText(model.getValueAt(row, 1).toString());
                opt1Field.setText(model.getValueAt(row, 2).toString());
                opt2Field.setText(model.getValueAt(row, 3).toString());
                opt3Field.setText(model.getValueAt(row, 4).toString());
                opt4Field.setText(model.getValueAt(row, 5).toString());
                correctOptionBox.setSelectedItem("Option " + model.getValueAt(row, 6));
                topicField.setSelectedItem(model.getValueAt(row, 7).toString());
                difficultyBox.setSelectedItem(model.getValueAt(row, 8).toString());
            }
        });

        setVisible(true);
    }

    private JPanel createHero() {
        JPanel hero = new ManagerHeroPanel(new BorderLayout(22, 0));
        hero.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(78, 142, 232), 1, 8),
                BorderFactory.createEmptyBorder(26, 26, 26, 26)
        ));
        hero.add(createIconBadge("\u25A6", PURPLE, 64, 31, false), BorderLayout.WEST);

        JPanel copy = new JPanel(new GridLayout(0, 1, 0, 7));
        copy.setOpaque(false);
        JLabel title = new JLabel("Question Manager");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 30));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Add, edit, and maintain your quiz bank with reliable form controls.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_MUTED);
        copy.add(title);
        copy.add(subtitle);
        hero.add(copy, BorderLayout.CENTER);
        return hero;
    }

    private JPanel createLibraryCard(JScrollPane tableScroll) {
        JPanel card = createGlassCard(new BorderLayout(0, 14), 20, 18, 20, 18);

        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setOpaque(false);
        titleRow.add(createIconBadge("\u2637", PURPLE, 36, 20, true), BorderLayout.WEST);
        JLabel title = new JLabel("Question Library");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);
        titleRow.add(title, BorderLayout.CENTER);

        searchField = new JTextField();
        styleTextField(searchField, "Search questions...");
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::applyFilters));

        topicFilterBox = new JComboBox<>(new String[]{"All Topics"});
        styleComboBox(topicFilterBox);
        topicFilterBox.addActionListener(e -> applyFilters());

        JButton addButton = new GradientButton("+  Add Question", PURPLE, new Color(83, 65, 235));
        styleActionButton(addButton, 150, 42);
        addButton.addActionListener(e -> {
            clearFields();
            questionField.requestFocusInWindow();
        });

        JPanel toolbar = new JPanel(new GridBagLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(68, 88, 128), 1, 8),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 12);
        toolbar.add(searchField, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        toolbar.add(topicFilterBox, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        toolbar.add(addButton, gbc);

        countLabel = new JLabel("Showing 0 questions");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(countLabel, BorderLayout.WEST);

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(titleRow, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createDetailsCard() {
        JPanel card = createGlassCard(new BorderLayout(0, 18), 20, 18, 20, 18);

        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setOpaque(false);
        titleRow.add(createIconBadge("\u25A3", PURPLE, 36, 20, true), BorderLayout.WEST);
        JLabel title = new JLabel("Question Details");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);
        titleRow.add(title, BorderLayout.CENTER);

        JScrollPane formScroll = new JScrollPane(createFormPanel());
        styleScrollPane(formScroll);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(formScroll, BorderLayout.CENTER);
        return card;
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
        styleScrollPane(questionScroll);
        questionScroll.setPreferredSize(new Dimension(320, 104));

        opt1Field = createPlainField();
        opt2Field = createPlainField();
        opt3Field = createPlainField();
        opt4Field = createPlainField();

        correctOptionBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3", "Option 4"});
        configurePlainCombo(correctOptionBox);

        topicField = new JComboBox<>();
        topicField.setEditable(true);
        configurePlainCombo(topicField);

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
        JPanel selectRow = new JPanel(new GridLayout(1, 3, 14, 0));
        selectRow.setOpaque(false);
        selectRow.add(createFieldBlock("Correct Option", correctOptionBox));
        selectRow.add(createFieldBlock("Topic", topicField));
        selectRow.add(createFieldBlock("Difficulty", difficultyBox));
        form.add(selectRow, gbc);

        JButton addBtn = new JButton("Add Question");
        styleActionButton(addBtn, 0, 44);
        JButton updateBtn = new JButton("Update Question");
        styleSecondaryActionButton(updateBtn);
        JButton deleteBtn = new JButton("Delete Question");
        ModernTheme.styleDangerButton(deleteBtn);
        JButton clearBtn = new JButton("Clear Form");
        styleSecondaryActionButton(clearBtn);
        JButton importBtn = new JButton("Import CSV");
        styleActionButton(importBtn, 0, 44);
        JButton sampleBtn = new JButton("Download Sample CSV");
        styleSecondaryActionButton(sampleBtn);

        addBtn.addActionListener(e -> addQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());
        clearBtn.addActionListener(e -> clearFields());
        importBtn.addActionListener(e -> importQuestionsFromCsv());
        sampleBtn.addActionListener(e -> downloadSampleCsv());

        JPanel actions = new JPanel(new GridLayout(2, 3, 12, 12));
        actions.setOpaque(false);
        actions.add(clearBtn);
        actions.add(updateBtn);
        actions.add(addBtn);
        actions.add(deleteBtn);
        actions.add(sampleBtn);
        actions.add(importBtn);

        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 0, 0);
        form.add(actions, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 0, 0);
        JLabel helpLabel = new JLabel("<html>CSV format: <b>question_text, option1, option2, option3, option4, correct_option, topic, difficulty</b><br>You can include a header row.</html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        helpLabel.setForeground(TEXT_MUTED);
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

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_MAIN);
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
        styleTextField(field, "");
        field.setPreferredSize(new Dimension(320, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private void configurePlainArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(TEXT_MAIN);
        area.setCaretColor(PURPLE);
        area.setBackground(FIELD_BG);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void configurePlainCombo(JComboBox<String> combo) {
        styleComboBox(combo);
        combo.setPreferredSize(new Dimension(220, 42));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(PURPLE);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 6),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_MAIN);
        combo.setBackground(FIELD_BG);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 6),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        combo.setUI(new ManagerComboBoxUI());
        combo.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                label.setBackground(isSelected ? new Color(78, 65, 222) : FIELD_BG);
                label.setForeground(TEXT_MAIN);
                list.setBackground(FIELD_BG);
                return label;
            }
        });
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new RoundedLineBorder(FIELD_BORDER, 1, 6));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new ManagerScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ManagerScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
    }

    private void styleActionButton(JButton button, int width, int height) {
        button.setUI(new ModernThemeButtonUI(PURPLE, new Color(78, 65, 222)));
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (width > 0 && height > 0) {
            button.setPreferredSize(new Dimension(width, height));
        }
    }

    private void styleSecondaryActionButton(JButton button) {
        button.setUI(new ModernThemeButtonUI(new Color(26, 39, 63), new Color(40, 56, 86)));
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        button.setForeground(TEXT_MAIN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 44));
    }

    private void styleManagerTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_MAIN);
        table.setBackground(new Color(11, 22, 43));
        table.setRowHeight(30);
        table.setGridColor(new Color(59, 78, 112));
        table.setSelectionBackground(new Color(65, 57, 190));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setBackground(new Color(27, 38, 67));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                label.setForeground(isSelected ? Color.WHITE : TEXT_MAIN);
                label.setBackground(isSelected ? new Color(65, 57, 190) : (row % 2 == 0 ? new Color(12, 24, 47) : new Color(10, 20, 39)));
                if (column == 7 && !isSelected) {
                    label.setForeground(BLUE);
                }
                if (column == 8 && !isSelected) {
                    String difficulty = value != null ? value.toString() : "";
                    if ("Hard".equalsIgnoreCase(difficulty)) {
                        label.setForeground(RED);
                    } else if ("Medium".equalsIgnoreCase(difficulty)) {
                        label.setForeground(ORANGE);
                    } else {
                        label.setForeground(GREEN);
                    }
                }
                return label;
            }
        });
    }

    private int getCorrectOptionValue() {
        String value = String.valueOf(correctOptionBox.getSelectedItem()).replace("Option", "").trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private String getTopicValue() {
        Object selected = topicField.getEditor().getItem();
        return selected == null ? "" : selected.toString().trim();
    }

    private void refreshTopicControls() {
        List<String> topics = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String topic = String.valueOf(model.getValueAt(i, 7));
            if (!topic.trim().isEmpty() && !topics.contains(topic)) {
                topics.add(topic);
            }
        }

        Object selectedFilter = topicFilterBox != null ? topicFilterBox.getSelectedItem() : "All Topics";
        Object selectedTopic = topicField != null ? topicField.getSelectedItem() : "";

        topicFilterBox.removeAllItems();
        topicFilterBox.addItem("All Topics");
        for (String topic : topics) {
            topicFilterBox.addItem(topic);
        }
        topicFilterBox.setSelectedItem(selectedFilter != null ? selectedFilter : "All Topics");

        topicField.removeAllItems();
        topicField.addItem("");
        for (String topic : topics) {
            topicField.addItem(topic);
        }
        if (selectedTopic != null) {
            topicField.setSelectedItem(selectedTopic);
        }
    }

    private void applyFilters() {
        if (sorter == null) {
            return;
        }
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String search = searchField != null ? searchField.getText().trim() : "";
        if (!search.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(search), 1, 2, 3, 4, 5, 7, 8));
        }
        String topic = topicFilterBox != null && topicFilterBox.getSelectedItem() != null ? topicFilterBox.getSelectedItem().toString() : "All Topics";
        if (!"All Topics".equals(topic)) {
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(topic) + "$", 7));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        if (countLabel != null) {
            countLabel.setText("Showing " + table.getRowCount() + " of " + model.getRowCount() + " questions");
        }
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
        if (topicFilterBox != null && topicField != null) {
            refreshTopicControls();
            applyFilters();
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
            pst.setInt(6, getCorrectOptionValue());
            pst.setString(7, getTopicValue());
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
            pst.setInt(6, getCorrectOptionValue());
            pst.setString(7, getTopicValue());
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
        correctOptionBox.setSelectedItem("Option " + row[5].trim());
        topicField.setSelectedItem(row[6].trim());
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
                || getTopicValue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all question details.");
            return false;
        }

        int correctOption = getCorrectOptionValue();
        if (correctOption < 1 || correctOption > 4) {
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
        topicField.setSelectedItem("");
        difficultyBox.setSelectedItem("Easy");
        table.clearSelection();
        selectedQuestionId = -1;
        SwingUtilities.invokeLater(() -> questionField.requestFocusInWindow());
    }

    private JLabel createIconBadge(String symbol, Color accent, int size, int fontSize, boolean circle) {
        JLabel label = new IconBadge(symbol, accent, fontSize, circle);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }

    private JPanel createGlassCard(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel panel = new GlassPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(CARD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        ));
        return panel;
    }

    private static class ManagerPagePanel extends JPanel {
        private ManagerPagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, PAGE_TOP, 0, getHeight(), PAGE_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(58, 156, 255, 18));
            g2.fillOval(getWidth() - 250, -120, 340, 260);
            g2.setColor(new Color(142, 72, 255, 14));
            g2.fillOval(-130, getHeight() - 210, 320, 260);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ManagerHeroPanel extends JPanel {
        private ManagerHeroPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            LinearGradientPaint paint = new LinearGradientPaint(
                    0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.46f, 1f},
                    new Color[]{new Color(126, 19, 196), new Color(50, 82, 218), new Color(9, 163, 224)}
            );
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(new Color(255, 255, 255, 14));
            for (int x = -90; x < getWidth(); x += 72) {
                g2.drawLine(x, getHeight(), x + 170, 0);
            }
            g2.setColor(new Color(42, 225, 236, 152));
            g2.fillOval(getWidth() - 54, 34, 15, 15);
            g2.fillOval(getWidth() - 145, 52, 18, 18);
            g2.setColor(new Color(255, 255, 255, 68));
            g2.fillOval(getWidth() - 89, 72, 7, 7);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlassPanel extends JPanel {
        private GlassPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 110));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 10, 8, 8);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, getWidth(), getHeight(), CARD_BOTTOM));
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 8, 8);
            g2.setColor(new Color(255, 255, 255, 15));
            g2.drawLine(12, 1, Math.max(12, getWidth() - 16), 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconBadge extends JLabel {
        private final Color accent;
        private final int fontSize;
        private final boolean circle;

        private IconBadge(String text, Color accent, int fontSize, boolean circle) {
            super(text, SwingConstants.CENTER);
            this.accent = accent;
            this.fontSize = fontSize;
            this.circle = circle;
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            setForeground(accent);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 44));
            if (circle) {
                g2.fillOval(0, 0, getWidth(), getHeight());
            } else {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
            g2.dispose();
            setFont(new Font("Segoe UI Symbol", Font.BOLD, fontSize));
            super.paintComponent(g);
        }
    }

    private static class ManagerComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("\u2304");
            button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
            button.setForeground(TEXT_MAIN);
            button.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(FIELD_BG);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private static class GradientButton extends JButton {
        private final Color start;
        private final Color end;

        private GradientButton(String text, Color start, Color end) {
            super(text);
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color left = getModel().isRollover() ? start.brighter() : start;
            Color right = getModel().isRollover() ? end.brighter() : end;
            g2.setPaint(new GradientPaint(0, 0, left, getWidth(), getHeight(), right));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
            g2.setColor(new Color(255, 255, 255, 38));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernThemeButtonUI extends BasicButtonUI {
        private final Color base;
        private final Color hover;

        private ModernThemeButtonUI(Color base, Color hover) {
            this.base = base;
            this.hover = hover;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(button.getModel().isRollover() ? hover : base);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 7, 7);
            g2.setColor(new Color(255, 255, 255, 32));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 7, 7);
            g2.dispose();
            super.paint(g, c);
        }
    }

    private static class ManagerScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(116, 92, 245, 210);
            trackColor = new Color(23, 35, 58, 170);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 8, 8);
            g2.dispose();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        private SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            action.run();
        }
    }

    private static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        private RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(thickness, thickness, thickness, thickness);
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }
}
