package ui;

import javax.swing.*;
import java.awt.*;

public class ModernTheme {

    // 🎨 Modern Color Palette
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);      // Steel Blue
    public static final Color SECONDARY_COLOR = new Color(100, 150, 200);   // Light Blue
    public static final Color SUCCESS_COLOR = new Color(50, 150, 80);       // Green
    public static final Color DANGER_COLOR = new Color(220, 50, 50);        // Red
    public static final Color WARNING_COLOR = new Color(255, 165, 0);       // Orange
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);  // Light Gray
    public static final Color TEXT_COLOR = new Color(33, 33, 33);           // Dark Gray
    public static final Color ACCENT_COLOR = new Color(30, 144, 255);       // Dodger Blue

    // 🎨 Modern Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // 🎨 Apply modern theme to a button
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    // 🎨 Apply modern theme to a panel
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
    }

    // 🎨 Apply modern theme to a label
    public static void styleLabel(JLabel label, int style) {
        label.setForeground(TEXT_COLOR);
        
        if (style == 1) { // Title
            label.setFont(TITLE_FONT);
        } else if (style == 2) { // Header
            label.setFont(HEADER_FONT);
        } else { // Regular
            label.setFont(LABEL_FONT);
        }
    }

    // 🎨 Apply modern theme to a text field
    public static void styleTextField(JTextField textField) {
        textField.setFont(LABEL_FONT);
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
    }

    // 🎨 Apply modern theme to a password field
    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(LABEL_FONT);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
    }

    // 🎨 Create a modern panel with title
    public static JPanel createModernPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        if (title != null) {
            JLabel titleLabel = new JLabel(title);
            styleLabel(titleLabel, 2); // Header style
            panel.add(titleLabel, BorderLayout.NORTH);
        }
        
        return panel;
    }
}
