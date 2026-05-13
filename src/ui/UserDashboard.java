package ui;

import session.Session;
import ui.panels.RentMotorbikePanel;
import ui.panels.RentalHistoryPanel;
import ui.panels.ViewMotorbikesPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private UIStyles.SidebarButton activeBtn;

    private ViewMotorbikesPanel viewPanel;
    private RentMotorbikePanel rentPanel;
    private RentalHistoryPanel historyPanel;

    public UserDashboard() {
        setTitle("Thuê Xe Máy - Hệ Thống RENTAL MOTO");
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

        UIStyles.SidebarButton btnView = new UIStyles.SidebarButton("🏍  Xem xe sẵn có");
        UIStyles.SidebarButton btnRent = new UIStyles.SidebarButton("📅  Thuê xe");
        UIStyles.SidebarButton btnHistory = new UIStyles.SidebarButton("📋  Lịch sử thuê");

        sidebar.add(lblLogo);
        sidebar.add(btnView);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnRent);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnHistory);
        sidebar.add(Box.createVerticalGlue());

        UIStyles.SidebarButton btnLogout = new UIStyles.SidebarButton("🚪  Đăng xuất");
        sidebar.add(btnLogout);

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.WHITE);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER),
                new EmptyBorder(0, 30, 0, 30)));

        JLabel lblTitle = new JLabel("Bảng Điều Khiển");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(UIStyles.TEXT_MAIN);

        String name = Session.getCurrentUser().getFullName();
        JLabel lblUser = new JLabel("Xin chào, " + name);
        lblUser.setFont(UIStyles.FONT_SUBTITLE);
        lblUser.setForeground(UIStyles.TEXT_MUTED);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        // ---- Content ----
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        viewPanel = new ViewMotorbikesPanel();
        rentPanel = new RentMotorbikePanel();
        historyPanel = new RentalHistoryPanel();

        viewPanel.setOnRentAction(motorbike -> {
            rentPanel.selectMotorbike(motorbike);
            setActive(btnRent, "RENT");
        });

        contentPanel.add(viewPanel, "VIEW");
        contentPanel.add(rentPanel, "RENT");
        contentPanel.add(historyPanel, "HISTORY");

        add(sidebar, BorderLayout.WEST);
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Actions
        btnView.addActionListener(e -> {
            viewPanel.loadData(new dao.MotorbikeDAO().getAvailable());
            setActive(btnView, "VIEW");
        });
        btnRent.addActionListener(e -> {
            rentPanel.loadMotorbikes();
            setActive(btnRent, "RENT");
        });
        btnHistory.addActionListener(e -> {
            historyPanel.loadData();
            setActive(btnHistory, "HISTORY");
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

        setActive(btnView, "VIEW");
    }

    private void setActive(UIStyles.SidebarButton btn, String card) {
        if (activeBtn != null)
            activeBtn.setActive(false);
        btn.setActive(true);
        activeBtn = btn;
        cardLayout.show(contentPanel, card);
    }
}
