package ui;

import session.Session;
import ui.panels.RentMotorbikePanel;
import ui.panels.RentalHistoryPanel;
import ui.panels.ViewMotorbikesPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDashboard extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_SIDEBAR = new Color(39, 55, 70);
    private static final Color C_ACTIVE  = new Color(39, 174, 96);
    private static final Color C_TEXT    = new Color(189, 195, 199);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton activeBtn;

    private ViewMotorbikesPanel viewPanel;
    private RentMotorbikePanel  rentPanel;
    private RentalHistoryPanel  historyPanel;

    public UserDashboard() {
        setTitle("Thuê Xe Máy - Tài Khoản: " + Session.getCurrentUser().getUsername());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 580));
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblLogo = new JLabel("🏍  Thuê Xe Máy");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeader.setBackground(C_PRIMARY);
        String name = Session.getCurrentUser().getFullName();
        JLabel lblUser = new JLabel("👤  Xin chào, " + name);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(C_TEXT);
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightHeader.add(lblUser);
        rightHeader.add(btnLogout);
        header.add(lblLogo,     BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        // ---- Sidebar ----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblMenu = new JLabel("  MENU");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenu.setForeground(new Color(127, 140, 141));
        lblMenu.setAlignmentX(LEFT_ALIGNMENT);
        lblMenu.setBorder(new EmptyBorder(0, 16, 12, 0));

        JButton btnView    = navBtn("🏍  Xem xe sẵn có",    "VIEW");
        JButton btnRent    = navBtn("📅  Thuê xe",           "RENT");
        JButton btnHistory = navBtn("📋  Lịch sử thuê",     "HISTORY");

        sidebar.add(lblMenu);
        sidebar.add(btnView);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnRent);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnHistory);
        sidebar.add(Box.createVerticalGlue());

        // ---- Content ----
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        viewPanel    = new ViewMotorbikesPanel();
        rentPanel    = new RentMotorbikePanel();
        historyPanel = new RentalHistoryPanel();

        // Kết nối "Thuê xe này" từ ViewPanel → chuyển sang RentPanel với xe đã chọn
        viewPanel.setOnRentAction(motorbike -> {
            rentPanel.selectMotorbike(motorbike);
            setActive(btnRent, "RENT");
        });

        contentPanel.add(viewPanel,    "VIEW");
        contentPanel.add(rentPanel,    "RENT");
        contentPanel.add(historyPanel, "HISTORY");

        add(header,       BorderLayout.NORTH);
        add(sidebar,      BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setActive(btnView, "VIEW");

        btnView.addActionListener(e    -> { viewPanel.loadData(new dao.MotorbikeDAO().getAvailable()); setActive(btnView, "VIEW"); });
        btnRent.addActionListener(e    -> { rentPanel.loadMotorbikes(); setActive(btnRent, "RENT"); });
        btnHistory.addActionListener(e -> { historyPanel.loadData(); setActive(btnHistory, "HISTORY"); });

        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                Session.logout();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }

    private JButton navBtn(String text, String card) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setForeground(C_TEXT);
        b.setBackground(C_SIDEBAR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(12, 20, 12, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private void setActive(JButton btn, String card) {
        if (activeBtn != null) {
            activeBtn.setBackground(C_SIDEBAR);
            activeBtn.setForeground(C_TEXT);
            activeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        btn.setBackground(C_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activeBtn = btn;
        cardLayout.show(contentPanel, card);
    }
}
