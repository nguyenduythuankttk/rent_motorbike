package ui.panels;

import dao.MotorbikeDAO;
import model.Motorbike;
import ui.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManageMotorbikesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JLabel lblCount;
    private final MotorbikeDAO dao = new MotorbikeDAO();

    public ManageMotorbikesPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyles.BACKGROUND);
        initUI();
        loadData(dao.getAll());
    }

    private void initUI() {
        // ========== HEADER ==========
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Danh sách xe máy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIStyles.TEXT_MAIN);

        // Thanh tìm kiếm
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchBar.setBackground(UIStyles.BACKGROUND);

        JLabel lblSearch = new JLabel("Tên / Biển số:");
        lblSearch.setFont(UIStyles.FONT_LABEL);
        lblSearch.setForeground(UIStyles.TEXT_MUTED);

        txtSearch = UIStyles.createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 40));

        UIStyles.ModernButton btnSearch = new UIStyles.ModernButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 40));

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);

        header.add(title, BorderLayout.WEST);
        header.add(searchBar, BorderLayout.EAST);

        // ========== TOOLBAR ==========
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(UIStyles.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new UIStyles.RoundedPanel(15, UIStyles.WHITE).getBorder(), // Just using it as a reference for style
                new EmptyBorder(10, 15, 10, 15)));
        // Actually, let's just use a simple panel with rounded border manually for
        // simplicity
        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)) {
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

        UIStyles.ModernButton btnAdd = new UIStyles.ModernButton("＋ Thêm xe");
        btnAdd.setPreferredSize(new Dimension(130, 40));

        UIStyles.ModernButton btnEdit = new UIStyles.ModernButton("✎ Sửa xe");
        btnEdit.setPreferredSize(new Dimension(110, 40));

        UIStyles.ModernButton btnDelete = new UIStyles.ModernButton("✖ Xoá xe");
        btnDelete.setPreferredSize(new Dimension(110, 40));

        UIStyles.ModernButton btnRefresh = new UIStyles.ModernButton("↻ Làm mới");
        btnRefresh.setPreferredSize(new Dimension(130, 40));

        lblCount = new JLabel();
        lblCount.setFont(UIStyles.FONT_SUBTITLE);
        lblCount.setForeground(UIStyles.TEXT_MUTED);

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(lblCount);

        // ========== BẢNG ==========
        String[] cols = { "ID", "Biển số", "Tên xe", "Hãng", "Giá/ngày", "Trạng thái", "Ngày dự kiến trả" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyles.styleTable(table);

        int[] widths = { 50, 120, 200, 120, 150, 130, 150 };
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Căn phải giá
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        // Căn giữa ngày dự kiến trả
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        // Màu cột Trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String s = v != null ? v.toString() : "";
                if (sel) {
                    setForeground(UIStyles.PRIMARY);
                } else {
                    switch (s) {
                        case "Sẵn sàng":
                            setForeground(new Color(16, 185, 129));
                            break; // Emerald 500
                        case "Đang thuê":
                            setForeground(new Color(59, 130, 246));
                            break; // Blue 500
                        case "Bảo trì":
                            setForeground(new Color(245, 158, 11));
                            break; // Amber 500
                        default:
                            setForeground(UIStyles.TEXT_MAIN);
                    }
                }
                setFont(UIStyles.FONT_LABEL);
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

        // ========== GHÉP LAYOUT ==========
        add(header, BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 20));
        centerArea.setBackground(UIStyles.BACKGROUND);
        centerArea.add(toolbar, BorderLayout.NORTH);
        centerArea.add(tableWrapper, BorderLayout.CENTER);

        add(centerArea, BorderLayout.CENTER);

        // ========== SỰ KIỆN ==========
        Runnable doSearch = () -> {
            String kw = txtSearch.getText().trim();
            List<Motorbike> result = kw.isEmpty() ? dao.getAll() : dao.search(kw);
            loadData(result);
        };

        btnSearch.addActionListener(e -> doSearch.run());
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doSearch.run();
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData(dao.getAll());
        });

        btnAdd.addActionListener(e -> {
            MotorbikeDialog dlg = new MotorbikeDialog(SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isSaved())
                loadData(dao.getAll());
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe muốn sửa!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Motorbike m = dao.getById((int) model.getValueAt(row, 0));
            if (m != null) {
                MotorbikeDialog dlg = new MotorbikeDialog(SwingUtilities.getWindowAncestor(this), m);
                dlg.setVisible(true);
                if (dlg.isSaved()) loadData(dao.getAll());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe muốn xoá!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            String status = (String) model.getValueAt(row, 5);
            if (!"Sẵn sàng".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "Không thể xoá xe đang ở trạng thái \"" + status + "\".\nChỉ xoá được xe ở trạng thái Sẵn sàng.",
                        "Không thể xoá", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String name = model.getValueAt(row, 2) + " (" + model.getValueAt(row, 1) + ")";
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xoá xe:\n  " + name + "?\nHành động này không thể hoàn tác!",
                    "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            if (dao.delete(id)) {
                loadData(dao.getAll());
                JOptionPane.showMessageDialog(this, "Đã xoá xe thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xoá xe thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Double-click → sửa
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Motorbike m = dao.getById((int) model.getValueAt(row, 0));
                        if (m != null) {
                            MotorbikeDialog dlg = new MotorbikeDialog(
                                    SwingUtilities.getWindowAncestor(ManageMotorbikesPanel.this), m);
                            dlg.setVisible(true);
                            if (dlg.isSaved())
                                loadData(dao.getAll());
                        }
                    }
                }
            }
        });
    }

    private void loadData(List<Motorbike> list) {
        model.setRowCount(0);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        for (Motorbike m : list) {
            String expectedDate = m.getExpectedReturnDate() != null ? m.getExpectedReturnDate().toString() : "—";
            model.addRow(new Object[] {
                    m.getId(), m.getLicensePlate(), m.getModel(),
                    m.getBrand(), nf.format(m.getPricePerDay()) + " đ", m.getStatus(),
                    expectedDate
            });
        }
        lblCount.setText("Tổng cộng: " + list.size() + " xe");
    }
}
