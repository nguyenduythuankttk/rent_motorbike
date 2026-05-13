package ui.panels;

import dao.UserDAO;
import model.User;
import session.Session;
import ui.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblCount;
    private final UserDAO dao = new UserDAO();

    public ManageUsersPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyles.BACKGROUND);
        initUI();
        loadData();
    }

    private void initUI() {
        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Quản lý người dùng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIStyles.TEXT_MAIN);

        header.add(title, BorderLayout.WEST);

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIStyles.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        toolbar.setOpaque(false);

        UIStyles.ModernButton btnDelete = new UIStyles.ModernButton("✖ Xoá người dùng");
        btnDelete.setPreferredSize(new Dimension(170, 40));

        UIStyles.ModernButton btnRefresh = new UIStyles.ModernButton("↻ Làm mới");
        btnRefresh.setPreferredSize(new Dimension(130, 40));

        lblCount = new JLabel();
        lblCount.setFont(UIStyles.FONT_SUBTITLE);
        lblCount.setForeground(UIStyles.TEXT_MUTED);

        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(lblCount);

        // ===== BẢNG =====
        String[] cols = { "ID", "Tên đăng nhập", "Họ và tên", "Số điện thoại", "Vai trò" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIStyles.styleTable(table);

        int[] widths = { 50, 150, 200, 130, 100 };
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Màu cột Vai trò
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                if (!sel) {
                    setForeground("Admin".equals(s) ? new Color(79, 70, 229) : new Color(16, 185, 129));
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIStyles.WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIStyles.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        // ===== GHÉP LAYOUT =====
        add(header, BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 20));
        centerArea.setBackground(UIStyles.BACKGROUND);
        centerArea.add(toolbar, BorderLayout.NORTH);
        centerArea.add(tableWrapper, BorderLayout.CENTER);

        add(centerArea, BorderLayout.CENTER);

        // ===== SỰ KIỆN =====
        btnRefresh.addActionListener(e -> loadData());

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng muốn xoá!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            String role = (String) model.getValueAt(row, 4);
            String name = model.getValueAt(row, 2) + " (" + model.getValueAt(row, 1) + ")";

            if ("Admin".equals(role)) {
                JOptionPane.showMessageDialog(this, "Không thể xoá tài khoản Admin!", "Không thể xoá", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (Session.getCurrentUser() != null && Session.getCurrentUser().getId() == id) {
                JOptionPane.showMessageDialog(this, "Không thể xoá tài khoản đang đăng nhập!", "Không thể xoá", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xoá người dùng:\n  " + name + "?\nDữ liệu thuê xe liên quan có thể bị ảnh hưởng.",
                    "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (dao.delete(id)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã xoá người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xoá thất bại! Người dùng có thể đang có đơn thuê.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void loadData() {
        model.setRowCount(0);
        List<User> list = dao.getAll();
        for (User u : list) {
            model.addRow(new Object[] {
                    u.getId(), u.getUsername(), u.getFullName(),
                    u.getPhone() != null ? u.getPhone() : "—",
                    u.getRole()
            });
        }
        lblCount.setText("Tổng cộng: " + list.size() + " người dùng");
    }
}
