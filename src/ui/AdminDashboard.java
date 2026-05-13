package ui;

import session.Session;
import ui.panels.ManageMotorbikesPanel;
import ui.panels.ManageRentalsPanel;
import ui.panels.StatisticsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private UIStyles.SidebarButton activeBtn;

    private ManageMotorbikesPanel motorbikesPanel;
    private ManageRentalsPanel rentalsPanel;
    private StatisticsPanel statsPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Hệ Thống Thuê Xe Máy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- Sidebar ----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIStyles.SECONDARY);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel lblLogo = new JLabel("  RENTAL MOTO");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(UIStyles.WHITE);
        lblLogo.setBorder(new EmptyBorder(0, 24, 40, 0));
        lblLogo.setAlignmentX(LEFT_ALIGNMENT);

        UIStyles.SidebarButton btnMotorbikes = new UIStyles.SidebarButton("🏍  Quản lý Xe");
        UIStyles.SidebarButton btnRentals = new UIStyles.SidebarButton("📋  Quản lý Thuê");
        UIStyles.SidebarButton btnStats = new UIStyles.SidebarButton("📊  Thống kê");

        sidebar.add(lblLogo);
        sidebar.add(btnMotorbikes);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnRentals);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStats);
        sidebar.add(Box.createVerticalGlue());

        // Logout in sidebar bottom
        UIStyles.SidebarButton btnLogout = new UIStyles.SidebarButton("🚪  Đăng xuất");
        sidebar.add(btnLogout);

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.WHITE);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER),
                new EmptyBorder(0, 30, 0, 30)));

        JLabel lblTitle = new JLabel("Trang Quản Trị");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(UIStyles.TEXT_MAIN);

        String name = Session.getCurrentUser().getFullName();
        JLabel lblUser = new JLabel("Xin chào, " + name + " (Admin)");
        lblUser.setFont(UIStyles.FONT_SUBTITLE);
        lblUser.setForeground(UIStyles.TEXT_MUTED);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        // ---- Content ----
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        motorbikesPanel = new ManageMotorbikesPanel();
        rentalsPanel = new ManageRentalsPanel();
        statsPanel = new StatisticsPanel();

        contentPanel.add(motorbikesPanel, "MOTORBIKES");
        contentPanel.add(rentalsPanel, "RENTALS");
        contentPanel.add(statsPanel, "STATS");

        add(sidebar, BorderLayout.WEST);
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Actions
        btnMotorbikes.addActionListener(e -> setActive(btnMotorbikes, "MOTORBIKES"));
        btnRentals.addActionListener(e -> {
            rentalsPanel.loadData();
            setActive(btnRentals, "RENTALS");
        });
        btnStats.addActionListener(e -> {
            statsPanel.loadStats();
            setActive(btnStats, "STATS");
        });

        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                Session.logout();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        // Default active
        setActive(btnMotorbikes, "MOTORBIKES");
    }

    private void setActive(UIStyles.SidebarButton btn, String card) {
        if (activeBtn != null)
            activeBtn.setActive(false);
        btn.setActive(true);
        activeBtn = btn;
        cardLayout.show(contentPanel, card);
    }
}
