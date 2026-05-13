package ui;

import session.Session;
import ui.panels.ManageMotorbikesPanel;
import ui.panels.ManageRentalsPanel;
import ui.panels.StatisticsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private static final Color C_PRIMARY  = new Color(44, 62, 80);
    private static final Color C_SIDEBAR  = new Color(52, 73, 94);
    private static final Color C_ACTIVE   = new Color(52, 152, 219);
    private static final Color C_TEXT     = new Color(189, 195, 199);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton activeBtn;

    private ManageMotorbikesPanel motorbikesPanel;
    private ManageRentalsPanel    rentalsPanel;
    private StatisticsPanel       statsPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Hệ Thống Thuê Xe Máy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- Top header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblLogo = new JLabel("🏍  Quản Lý Thuê Xe Máy");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeader.setBackground(C_PRIMARY);
        String name = Session.getCurrentUser().getFullName();
        JLabel lblUser = new JLabel("👤  " + name + "  |  Admin");
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

        header.add(lblLogo,    BorderLayout.WEST);
        header.add(rightHeader,BorderLayout.EAST);

        // ---- Sidebar ----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblMenu = new JLabel("  MENU ĐIỀU HƯỚNG");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenu.setForeground(new Color(127, 140, 141));
        lblMenu.setAlignmentX(LEFT_ALIGNMENT);
        lblMenu.setBorder(new EmptyBorder(0, 16, 12, 0));

        JButton btnMotorbikes = navBtn("🏍  Quản lý Xe",       "MOTORBIKES");
        JButton btnRentals    = navBtn("📋  Quản lý Thuê",     "RENTALS");
        JButton btnStats      = navBtn("📊  Thống kê",         "STATS");

        sidebar.add(lblMenu);
        sidebar.add(btnMotorbikes);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnRentals);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnStats);
        sidebar.add(Box.createVerticalGlue());

        // ---- Content (CardLayout) ----
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        motorbikesPanel = new ManageMotorbikesPanel();
        rentalsPanel    = new ManageRentalsPanel();
        statsPanel      = new StatisticsPanel();

        contentPanel.add(motorbikesPanel, "MOTORBIKES");
        contentPanel.add(rentalsPanel,    "RENTALS");
        contentPanel.add(statsPanel,      "STATS");

        add(header,       BorderLayout.NORTH);
        add(sidebar,      BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Default active
        setActive(btnMotorbikes, "MOTORBIKES");

        // Nav actions
        btnMotorbikes.addActionListener(e -> setActive(btnMotorbikes, "MOTORBIKES"));
        btnRentals.addActionListener(e    -> { rentalsPanel.loadData(); setActive(btnRentals, "RENTALS"); });
        btnStats.addActionListener(e      -> { statsPanel.loadStats(); setActive(btnStats, "STATS"); });

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
