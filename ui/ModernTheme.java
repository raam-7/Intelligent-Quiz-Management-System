package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTheme {

    public static final Color PRIMARY_COLOR = new Color(139, 92, 246);
    public static final Color PRIMARY_HOVER = new Color(167, 139, 250);
    public static final Color SECONDARY_COLOR = new Color(24, 32, 52);
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    public static final Color DANGER_COLOR = new Color(239, 68, 68);
    public static final Color WARNING_COLOR = new Color(251, 146, 60);
    public static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    public static final Color SURFACE_COLOR = new Color(30, 41, 59);
    public static final Color SURFACE_ALT_COLOR = new Color(51, 65, 85);
    public static final Color BORDER_COLOR = new Color(71, 85, 105);
    public static final Color TEXT_COLOR = new Color(226, 232, 240);
    public static final Color MUTED_TEXT_COLOR = new Color(148, 163, 184);
    public static final Color ACCENT_COLOR = new Color(34, 211, 238);
    public static final Color INFO_COLOR = new Color(96, 165, 250);
    public static final Color CARD_SHADOW = new Color(0, 0, 0, 80);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font STAT_FONT = new Font("Segoe UI", Font.BOLD, 30);

    public static final int ARC = 8;

    private ModernTheme() {
    }

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Application.useSystemFontSettings", Boolean.TRUE);
        UIManager.put("Button.arc", ARC);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(12, 0, 0, 0));
        UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);
        UIManager.put("TabbedPane.focus", PRIMARY_COLOR);
        UIManager.put("Table.alternateRowColor", SURFACE_ALT_COLOR);
        UIManager.put("ScrollBar.width", 12);
    }

    public static void prepareFrame(JFrame frame, int width, int height) {
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    public static JPanel createPagePanel(LayoutManager layout) {
        JPanel panel = new AnimatedBackgroundPanel();
        panel.setLayout(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel createSectionPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new ElevatedPanel(layout);
        panel.setBackground(SURFACE_COLOR);
        panel.setBorder(createCardBorder());
        return panel;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, ARC),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new HeroPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(117, 145, 210, 130), 1, ARC),
                BorderFactory.createEmptyBorder(26, 30, 26, 30)
        ));
        panel.setBackground(PRIMARY_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(LABEL_FONT);
        subtitleLabel.setForeground(MUTED_TEXT_COLOR);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel createAuthHeroPanel(String title, String subtitle, String note) {
        JPanel panel = new HeroPanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(117, 145, 210, 130), 1, ARC),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JPanel copy = createSectionPanel(new GridLayout(0, 1, 0, 8));
        JLabel titleLabel = new JLabel("<html><body style='width:260px'>" + title + "</body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(TEXT_COLOR);
        JLabel subtitleLabel = new JLabel("<html><body style='width:280px'>" + subtitle + "</body></html>");
        subtitleLabel.setFont(LABEL_FONT);
        subtitleLabel.setForeground(MUTED_TEXT_COLOR);
        copy.add(titleLabel);
        copy.add(subtitleLabel);

        JPanel notePanel = new JPanel(new BorderLayout(0, 8));
        notePanel.setOpaque(false);
        notePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(255, 255, 255, 90), 1, ARC),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel noteTitle = new JLabel("Quiz workspace");
        noteTitle.setFont(BUTTON_FONT);
        noteTitle.setForeground(TEXT_COLOR);
        JLabel noteLabel = new JLabel("<html><body style='width:250px'>" + note + "</body></html>");
        noteLabel.setFont(SMALL_FONT);
        noteLabel.setForeground(MUTED_TEXT_COLOR);
        notePanel.add(noteTitle, BorderLayout.NORTH);
        notePanel.add(noteLabel, BorderLayout.CENTER);

        panel.add(copy, BorderLayout.NORTH);
        panel.add(notePanel, BorderLayout.SOUTH);
        return panel;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JLabel createSubtleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(MUTED_TEXT_COLOR);
        return label;
    }

    public static JLabel createPillLabel(String text, Color color) {
        JLabel label = new PillLabel(text, new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
        label.setFont(SMALL_FONT);
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return label;
    }

    public static JLabel createStatValue(String value, Color color) {
        JLabel label = new JLabel(value);
        label.setFont(STAT_FONT);
        label.setForeground(color);
        return label;
    }

    public static void stylePanel(JPanel panel) {
        panel.setOpaque(false);
    }

    public static void styleLabel(JLabel label, int style) {
        label.setForeground(TEXT_COLOR);
        if (style == 1) {
            label.setFont(TITLE_FONT);
        } else if (style == 2) {
            label.setFont(HEADER_FONT);
        } else {
            label.setFont(LABEL_FONT);
        }
    }

    public static void styleButton(JButton button) {
        styleButton(button, PRIMARY_COLOR, PRIMARY_HOVER);
    }

    public static void styleButton(JButton button, Color baseColor, Color hoverColor) {
        button.setUI(new RoundedButtonUI(baseColor));
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(12, 18, 12, 18));
        button.setBorderPainted(false);
        button.setText(button.getText());

        for (java.awt.event.MouseListener listener : button.getMouseListeners()) {
            if (listener.getClass().getName().contains("ModernTheme")) {
                button.removeMouseListener(listener);
            }
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(baseColor);
                button.repaint();
            }
        });
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, SECONDARY_COLOR, new Color(30, 41, 59));
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, DANGER_COLOR, new Color(185, 28, 28));
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(LABEL_FONT);
        textField.setBackground(SURFACE_ALT_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(PRIMARY_COLOR);
        textField.setOpaque(true);
        textField.setBorder(createFieldBorder(BORDER_COLOR));
        textField.setMargin(new Insets(10, 12, 10, 12));
        textField.setPreferredSize(new Dimension(240, 44));
        installFieldFocus(textField);
    }

    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(LABEL_FONT);
        passwordField.setBackground(SURFACE_ALT_COLOR);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setCaretColor(PRIMARY_COLOR);
        passwordField.setOpaque(true);
        passwordField.setBorder(createFieldBorder(BORDER_COLOR));
        passwordField.setMargin(new Insets(10, 12, 10, 12));
        passwordField.setPreferredSize(new Dimension(240, 44));
        installFieldFocus(passwordField);
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(LABEL_FONT);
        textArea.setBackground(SURFACE_ALT_COLOR);
        textArea.setForeground(TEXT_COLOR);
        textArea.setCaretColor(PRIMARY_COLOR);
        textArea.setOpaque(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(createFieldBorder(BORDER_COLOR));
        installFieldFocus(textArea);
    }

    public static void styleTabbedPane(JTabbedPane tabs) {
        tabs.setFont(BUTTON_FONT);
        tabs.setBackground(BACKGROUND_COLOR);
        tabs.setForeground(TEXT_COLOR);
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(LABEL_FONT);
        comboBox.setBackground(SURFACE_ALT_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFocusable(false);
        comboBox.setBorder(createFieldBorder(BORDER_COLOR));
        comboBox.setPreferredSize(new Dimension(240, 44));
        comboBox.setUI(new BasicComboBoxUI());
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(LABEL_FONT);
        spinner.setPreferredSize(new Dimension(240, 44));
        spinner.setBorder(createFieldBorder(BORDER_COLOR));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField field = ((JSpinner.DefaultEditor) editor).getTextField();
            field.setFont(LABEL_FONT);
            field.setForeground(TEXT_COLOR);
            field.setBackground(SURFACE_ALT_COLOR);
            field.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }
    }

    public static void styleTable(JTable table) {
        table.setFont(LABEL_FONT);
        table.setForeground(TEXT_COLOR);
        table.setBackground(SURFACE_COLOR);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(BUTTON_FONT);
        header.setBackground(new Color(51, 65, 85));
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, ARC),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        scrollPane.getViewport().setBackground(SURFACE_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
    }

    public static JScrollPane createScrollPane(Component content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        return scrollPane;
    }

    public static void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(LABEL_FONT);
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setBackground(SURFACE_ALT_COLOR);
        radioButton.setFocusPainted(false);
        radioButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, ARC),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radioButton.setOpaque(true);
    }

    public static JPanel createMetricCard(String title, String value, Color color) {
        JPanel card = createCardPanel(new BorderLayout(0, 10));
        JLabel titleLabel = createSubtleLabel(title);
        JLabel valueLabel = createStatValue(value, color);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private static Border createFieldBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(color, 1, ARC),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
    }

    private static void installFieldFocus(JComponent component) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                component.setBorder(createFieldBorder(PRIMARY_COLOR));
            }

            @Override
            public void focusLost(FocusEvent e) {
                component.setBorder(createFieldBorder(BORDER_COLOR));
            }
        });
    }

    private static class AnimatedBackgroundPanel extends JPanel {
        private int drift;
        private final Timer animationTimer;

        private AnimatedBackgroundPanel() {
            animationTimer = new Timer(80, e -> {
                drift = (drift + 1) % 128;
                repaint();
            });
            animationTimer.setRepeats(true);
            animationTimer.start();
            addHierarchyListener(e -> {
                if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                    if (isDisplayable()) {
                        animationTimer.start();
                    } else {
                        animationTimer.stop();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(30, 41, 59)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(139, 92, 246, 28));
            for (int y = -128 + drift; y < getHeight(); y += 64) {
                g2.drawLine(0, y, getWidth(), y + 42);
            }
            g2.setColor(new Color(34, 211, 238, 42));
            for (int x = -64 + drift; x < getWidth(); x += 96) {
                for (int y = 44; y < getHeight(); y += 110) {
                    g2.fillOval(x, y, 5, 5);
                }
            }
            g2.dispose();
        }
    }

    private static class HeroPanel extends JPanel {
        private int shimmer;
        private final Timer animationTimer;

        private HeroPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            animationTimer = new Timer(70, e -> {
                shimmer = (shimmer + 6) % 420;
                repaint();
            });
            animationTimer.setRepeats(true);
            animationTimer.start();
            addHierarchyListener(e -> {
                if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                    if (isDisplayable()) {
                        animationTimer.start();
                    } else {
                        animationTimer.stop();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(109, 40, 217),
                    getWidth(), getHeight(), new Color(34, 211, 238)
            );
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            g2.setColor(new Color(255, 255, 255, 26));
            for (int x = -getHeight(); x < getWidth(); x += 34) {
                g2.drawLine(x, getHeight(), x + getHeight(), 0);
            }
            g2.setColor(new Color(255, 255, 255, 38));
            int highlightX = shimmer - 180;
            g2.fillRoundRect(highlightX, 0, 80, getHeight(), ARC, ARC);
            g2.setColor(new Color(255, 255, 255, 75));
            g2.fillOval(getWidth() - 104, 22, 14, 14);
            g2.fillOval(getWidth() - 70, 54, 8, 8);
            g2.fillOval(getWidth() - 134, 86, 6, 6);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ElevatedPanel extends JPanel {
        private ElevatedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_SHADOW);
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, ARC, ARC);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, ARC, ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PillLabel extends JLabel {
        private final Color fillColor;

        private PillLabel(String text, Color fillColor) {
            super(text);
            this.fillColor = fillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButtonUI extends BasicButtonUI {
        private final Color fallbackColor;

        private RoundedButtonUI(Color fallbackColor) {
            this.fallbackColor = fallbackColor;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(button.getBackground() != null ? button.getBackground() : fallbackColor);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ARC, ARC);
            g2.dispose();
            super.paint(g, c);
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(139, 92, 246, 150);
            trackColor = new Color(51, 65, 85, 130);
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
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, ARC, ARC);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, ARC, ARC);
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

    private static class FilledRoundedBorder extends AbstractBorder {
        private final Color fillColor;
        private final int radius;

        private FilledRoundedBorder(Color fillColor, int radius) {
            this.fillColor = fillColor;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(x, y, width, height, radius, radius);
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        private RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int value = Math.max(radius / 4, 8);
            insets.set(value, value, value, value);
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (thickness > 0) {
                g2.setColor(color);
                g2.setStroke(new BasicStroke(thickness));
                g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            } else {
                g2.setColor(c.getBackground());
                g2.fillRoundRect(x, y, width, height, radius, radius);
            }
            g2.dispose();
        }
    }
}
