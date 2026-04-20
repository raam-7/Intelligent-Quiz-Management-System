package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTheme {

    public static final Color PRIMARY_COLOR = new Color(31, 111, 235);
    public static final Color PRIMARY_HOVER = new Color(18, 88, 193);
    public static final Color SECONDARY_COLOR = new Color(15, 23, 42);
    public static final Color SUCCESS_COLOR = new Color(22, 163, 74);
    public static final Color DANGER_COLOR = new Color(220, 38, 38);
    public static final Color WARNING_COLOR = new Color(245, 158, 11);
    public static final Color BACKGROUND_COLOR = new Color(240, 244, 251);
    public static final Color SURFACE_COLOR = new Color(255, 255, 255);
    public static final Color SURFACE_ALT_COLOR = new Color(247, 250, 255);
    public static final Color BORDER_COLOR = new Color(207, 219, 236);
    public static final Color TEXT_COLOR = new Color(22, 28, 45);
    public static final Color MUTED_TEXT_COLOR = new Color(98, 108, 130);
    public static final Color ACCENT_COLOR = new Color(14, 165, 233);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font STAT_FONT = new Font("Segoe UI", Font.BOLD, 30);

    public static final int ARC = 22;

    private ModernTheme() {
    }

    public static void applyGlobalTheme() {
        UIManager.put("OptionPane.background", SURFACE_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
    }

    public static void prepareFrame(JFrame frame, int width, int height) {
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    public static JPanel createPagePanel(LayoutManager layout) {
        JPanel panel = new GradientPanel();
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
        JPanel panel = new JPanel(layout);
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
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new FilledRoundedBorder(PRIMARY_COLOR, 28),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        panel.setBackground(PRIMARY_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(LABEL_FONT);
        subtitleLabel.setForeground(new Color(225, 235, 255));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
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
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
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
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(baseColor);
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
        textField.setPreferredSize(new Dimension(240, 42));
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
        passwordField.setPreferredSize(new Dimension(240, 42));
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
    }

    public static void styleTable(JTable table) {
        table.setFont(LABEL_FONT);
        table.setForeground(TEXT_COLOR);
        table.setBackground(SURFACE_COLOR);
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 236, 245));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(BUTTON_FONT);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, 18),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        scrollPane.getViewport().setBackground(SURFACE_COLOR);
    }

    public static void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(LABEL_FONT);
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setBackground(SURFACE_ALT_COLOR);
        radioButton.setFocusPainted(false);
        radioButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 1, 18),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                BorderFactory.createCompoundBorder(
                        new FilledRoundedBorder(SURFACE_ALT_COLOR, 16),
                        new RoundedBorder(color, 1, 16)
                ),
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

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(233, 241, 255),
                    0, getHeight(), BACKGROUND_COLOR
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(201, 214, 255, 70));
            g2.fillOval(-50, -40, 220, 220);
            g2.setColor(new Color(125, 211, 252, 60));
            g2.fillOval(getWidth() - 220, 30, 240, 240);
            g2.dispose();
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
