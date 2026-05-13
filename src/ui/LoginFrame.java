package ui;

import dao.UserDAO;
import model.User;
import session.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Đăng Nhập - Hệ Thống Thuê Xe Máy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650); // Larger size for modern split layout
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIStyles.WHITE);

        // --- Left Panel: Image/Branding ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient Background
                GradientPaint gp = new GradientPaint(0, 0, UIStyles.PRIMARY, getWidth(), getHeight(), new Color(99, 102, 241));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Overlay decoration
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(-100, -100, 400, 400);
                g2.fillOval(getWidth() - 200, getHeight() - 200, 400, 400);

                g2.dispose();
            }
        };
        leftPanel.setPreferredSize(new Dimension(500, 650));
        leftPanel.setLayout(new GridBagLayout());

        JLabel lblBrandIcon = new JLabel("🏍");
        lblBrandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        lblBrandIcon.setForeground(UIStyles.WHITE);

        JLabel lblBrandName = new JLabel("RENTAL MOTO", SwingConstants.CENTER);
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblBrandName.setForeground(UIStyles.WHITE);

        JLabel lblBrandDesc = new JLabel("Trải nghiệm hành trình tuyệt vời cùng chúng tôi", SwingConstants.CENTER);
        lblBrandDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblBrandDesc.setForeground(new Color(224, 231, 255));

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0; gbcL.gridy = 0; gbcL.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(lblBrandIcon, gbcL);
        gbcL.gridy = 1; gbcL.insets = new Insets(0, 0, 10, 0);
        leftPanel.add(lblBrandName, gbcL);
        gbcL.gridy = 2;
        leftPanel.add(lblBrandDesc, gbcL);

        // --- Right Panel: Login Form ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UIStyles.WHITE);
        rightPanel.setBorder(new EmptyBorder(0, 60, 0, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Welcome Text
        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(UIStyles.FONT_TITLE);
        lblWelcome.setForeground(UIStyles.SECONDARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(lblWelcome, gbc);

        JLabel lblSub = new JLabel("Vui lòng nhập thông tin để tiếp tục");
        lblSub.setFont(UIStyles.FONT_SUBTITLE);
        lblSub.setForeground(UIStyles.TEXT_MUTED);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 40, 0);
        rightPanel.add(lblSub, gbc);

        // Username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(UIStyles.FONT_LABEL);
        lblUser.setForeground(UIStyles.TEXT_MAIN);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        rightPanel.add(lblUser, gbc);

        txtUsername = UIStyles.createTextField();
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(UIStyles.FONT_LABEL);
        lblPass.setForeground(UIStyles.TEXT_MAIN);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 8, 0);
        rightPanel.add(lblPass, gbc);

        txtPassword = UIStyles.createPasswordField();
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(txtPassword, gbc);

        // Login Button
        UIStyles.ModernButton btnLogin = new UIStyles.ModernButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(0, 48));
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(btnLogin, gbc);

        // Register Link
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        regPanel.setBackground(UIStyles.WHITE);
        JLabel lblNoAcc = new JLabel("Chưa có tài khoản?");
        lblNoAcc.setFont(UIStyles.FONT_SUBTITLE);
        lblNoAcc.setForeground(UIStyles.TEXT_MUTED);
        JButton btnReg = new JButton("Đăng ký ngay");
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReg.setForeground(UIStyles.PRIMARY);
        btnReg.setContentAreaFilled(false);
        btnReg.setBorderPainted(false);
        btnReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regPanel.add(lblNoAcc);
        regPanel.add(btnReg);
        gbc.gridy = 7;
        rightPanel.add(regPanel, gbc);

        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);

        // Actions
        btnLogin.addActionListener(e -> handleLogin());
        btnReg.addActionListener(e -> new RegisterDialog(this).setVisible(true));
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User found = userDAO.login(user, pass);
        if (found != null) {
            Session.setCurrentUser(found);
            dispose();
            if ("Admin".equals(found.getRole())) new AdminDashboard().setVisible(true);
            else                                  new UserDashboard().setVisible(true);
        } else {
            showToast("Tên đăng nhập hoặc mật khẩu không đúng!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showToast(String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", type);
    }
}
