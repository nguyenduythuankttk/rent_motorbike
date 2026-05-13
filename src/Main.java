import ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Dùng Nimbus L&F — hỗ trợ đầy đủ custom color cho button trên macOS
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception e2) { /* dùng default */ }
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
