import ui.LoginFrame;
import ui.ModernTheme;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernTheme.applyGlobalTheme();
            new LoginFrame();
        });
    }
}
