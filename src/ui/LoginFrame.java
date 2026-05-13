package ui;

import dao.UserDAO;
import model.User;
import session.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private static final Color C_PRIMARY  = new Color(44, 62, 80);
    private static final Color C_ACCENT   = new Color(52, 152, 219);
    private static final Color C_LIGHT    = new Color(236, 240, 241);
    private static final Color C_MUTED    = new Color(127, 140, 141);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Đăng Nhập - Hệ Thống Thuê Xe Máy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        // Nền gradient
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, C_PRIMARY, 0, getHeight(), new Color(41, 128, 185)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());

        // Card trắng chính giữa
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(36, 44, 36, 44));
        card.setPreferredSize(new Dimension(370, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // --- Icon ---
        JLabel lblIcon = new JLabel("🏍", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 6, 0);
        card.add(lblIcon, gbc);

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("THUÊ XE MÁY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(C_PRIMARY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Đăng nhập vào tài khoản của bạn", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(C_MUTED);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 24, 0);
        card.add(lblSub, gbc);

        // --- Label username ---
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(C_PRIMARY);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(lblUser, gbc);

        // --- Field username ---
        txtUsername = new JTextField();
        styleField(txtUsername);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 14, 0);
        card.add(txtUsername, gbc);

        // --- Label password ---
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(C_PRIMARY);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(lblPass, gbc);

        // --- Field password ---
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        card.add(txtPassword, gbc);

        // --- Nút ĐĂNG NHẬP ---
        JButton btnLogin = createBtn("ĐĂNG NHẬP", C_ACCENT, Color.WHITE);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 12, 0);
        card.add(btnLogin, gbc);

        // --- Dòng đăng ký ---
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setBackground(Color.WHITE);
        JLabel lblRegText = new JLabel("Chưa có tài khoản?");
        lblRegText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRegText.setForeground(C_MUTED);
        JButton btnReg = new JButton("Đăng ký ngay");
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReg.setForeground(C_ACCENT);
        btnReg.setBackground(Color.WHITE);
        btnReg.setBorderPainted(false);
        btnReg.setOpaque(false);
        btnReg.setFocusPainted(false);
        btnReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regRow.add(lblRegText);
        regRow.add(btnReg);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(regRow, gbc);

        bg.add(card);
        setContentPane(bg);

        btnLogin.addActionListener(e -> handleLogin());
        btnReg.addActionListener(e -> new RegisterDialog(this).setVisible(true));
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(280, 44));
        b.setMinimumSize(new Dimension(280, 44));
        return b;
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User found = userDAO.login(user, pass);
        if (found != null) {
            Session.setCurrentUser(found);
            dispose();
            if ("Admin".equals(found.getRole())) new AdminDashboard().setVisible(true);
            else                                  new UserDashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}
