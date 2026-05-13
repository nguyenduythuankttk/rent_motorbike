package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UIStyles {
    // Premium Color Palette
    public static final Color PRIMARY = new Color(79, 70, 229);    // Indigo 600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202); // Indigo 700
    public static final Color SECONDARY = new Color(15, 23, 42);   // Slate 900
    public static final Color BACKGROUND = new Color(248, 250, 252); // Slate 50
    public static final Color TEXT_MAIN = new Color(30, 41, 59);    // Slate 800
    public static final Color TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color BORDER = new Color(226, 232, 240);    // Slate 200
    public static final Color WHITE = Color.WHITE;

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static void applyGlobalStyles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    // Helper để bọc text vào HTML giúp hỗ trợ cả Tiếng Việt và Icon
    private static String toHtml(String text) {
        return "<html><body style='font-family: \"Segoe UI\", \"Segoe UI Emoji\", \"Segoe UI Symbol\"; font-size: 11pt;'>" + text + "</body></html>";
    }

    public static class RoundedPanel extends JPanel {
        private int radius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }

    public static class ModernButton extends JButton {
        private boolean isHovered = false;

        public ModernButton(String text) {
            super(toHtml(text));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? PRIMARY_HOVER : PRIMARY);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, BORDER),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, BORDER),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    public static class SidebarButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;

        public SidebarButton(String text) {
            super(toHtml(text));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(new Color(148, 163, 184));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(12, 24, 12, 24));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? WHITE : new Color(148, 163, 184));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fill(new RoundRectangle2D.Float(10, 4, getWidth() - 20, getHeight() - 8, 10, 10));
                g2.setColor(PRIMARY);
                g2.fillRect(0, 12, 4, getHeight() - 24);
            } else if (isHovered) {
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fill(new RoundRectangle2D.Float(10, 4, getWidth() - 20, getHeight() - 8, 10, 10));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_INPUT);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(PRIMARY);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(BACKGROUND);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        ((javax.swing.table.DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        public RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius / 2, radius / 2, radius / 2, radius / 2); }
    }
}
