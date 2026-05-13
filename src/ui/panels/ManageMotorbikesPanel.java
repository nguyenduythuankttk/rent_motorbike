package ui.panels;

import dao.MotorbikeDAO;
import model.Motorbike;

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

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_ACCENT  = new Color(52, 152, 219);
    private static final Color C_SUCCESS = new Color(39, 174, 96);
    private static final Color C_DANGER  = new Color(231, 76, 60);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_GRAY    = new Color(108, 117, 125);
    private static final Color C_BG      = new Color(245, 247, 250);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JLabel lblCount;
    private final MotorbikeDAO dao = new MotorbikeDAO();

    public ManageMotorbikesPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        initUI();
        loadData(dao.getAll());
    }

    private void initUI() {
        // ========== HEADER ==========
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 221, 225)),
            new EmptyBorder(14, 20, 14, 20)));

        JLabel title = new JLabel("Quản Lý Xe Máy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);

        // Thanh tìm kiếm bên phải header
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            new EmptyBorder(5, 10, 5, 10)));
        txtSearch.setPreferredSize(new Dimension(220, 32));

        JButton btnSearch = toolBtn("🔍 Tìm kiếm", C_ACCENT, Color.WHITE);
        JButton btnReset  = toolBtn("✕ Xóa lọc",   C_GRAY,   Color.WHITE);

        searchBar.add(new JLabel("Tìm: ") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_GRAY);
        }});
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnReset);

        header.add(title,     BorderLayout.WEST);
        header.add(searchBar, BorderLayout.EAST);

        // ========== TOOLBAR (nút CRUD) ==========
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(C_BG);
        toolbar.setBorder(new EmptyBorder(4, 16, 4, 16));

        JButton btnAdd    = crudBtn("＋  Thêm xe mới", C_SUCCESS, Color.WHITE);
        JButton btnEdit   = crudBtn("✎  Sửa thông tin", C_ACCENT, Color.WHITE);
        JButton btnDelete = crudBtn("✖  Xóa xe",        C_DANGER, Color.WHITE);
        JButton btnStatus = crudBtn("⟳  Đổi trạng thái", C_WARN,  Color.WHITE);
        JButton btnRefresh= crudBtn("↻  Làm mới",        C_GRAY,  Color.WHITE);

        lblCount = new JLabel();
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setForeground(C_GRAY);

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnStatus);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(lblCount);

        // ========== BẢNG ==========
        String[] cols = {"ID", "Biển số", "Tên xe", "Hãng", "Giá/ngày (VNĐ)", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(C_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setGridColor(new Color(230, 232, 235));
        table.setShowVerticalLines(true);
        table.setRowSelectionAllowed(true);

        int[] widths = {45, 110, 160, 100, 140, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Căn phải giá
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        // Màu cột Trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                switch (s) {
                    case "Sẵn sàng":  setForeground(C_SUCCESS); break;
                    case "Đang thuê": setForeground(C_ACCENT);  break;
                    case "Bảo trì":   setForeground(C_WARN);    break;
                    default:          setForeground(Color.BLACK);
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 221, 225)));

        // ========== GHÉP LAYOUT ==========
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(header,  BorderLayout.NORTH);
        topBar.add(toolbar, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ========== SỰ KIỆN ==========
        Runnable doSearch = () -> {
            String kw = txtSearch.getText().trim();
            List<Motorbike> result = kw.isEmpty() ? dao.getAll() : dao.search(kw);
            loadData(result);
        };

        btnSearch.addActionListener(e -> doSearch.run());
        btnReset.addActionListener(e  -> { txtSearch.setText(""); loadData(dao.getAll()); });
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doSearch.run();
            }
        });

        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadData(dao.getAll()); });

        btnAdd.addActionListener(e -> {
            MotorbikeDialog dlg = new MotorbikeDialog(SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadData(dao.getAll());
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Vui lòng chọn xe cần sửa!"); return; }
            Motorbike m = dao.getById((int) model.getValueAt(row, 0));
            if (m != null) {
                MotorbikeDialog dlg = new MotorbikeDialog(SwingUtilities.getWindowAncestor(this), m);
                dlg.setVisible(true);
                if (dlg.isSaved()) loadData(dao.getAll());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Vui lòng chọn xe cần xóa!"); return; }
            int    id   = (int)    model.getValueAt(row, 0);
            String name = model.getValueAt(row, 2) + " (" + model.getValueAt(row, 1) + ")";
            int ok = JOptionPane.showConfirmDialog(this,
                "Xóa xe: " + name + "?\nHành động này không thể hoàn tác.",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) { loadData(dao.getAll()); }
                else showErr("Không thể xóa xe đang được thuê hoặc có đơn liên quan!");
            }
        });

        btnStatus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Vui lòng chọn xe cần đổi trạng thái!"); return; }
            int    id     = (int)    model.getValueAt(row, 0);
            String curSt  = (String) model.getValueAt(row, 5);
            String[] opts = {"Sẵn sàng", "Đang thuê", "Bảo trì"};
            String choice = (String) JOptionPane.showInputDialog(this,
                "Chọn trạng thái mới cho xe:", "Đổi Trạng Thái",
                JOptionPane.PLAIN_MESSAGE, null, opts, curSt);
            if (choice != null && !choice.equals(curSt)) {
                dao.updateStatus(id, choice);
                loadData(dao.getAll());
            }
        });

        // Double-click → sửa
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) btnEdit.doClick();
            }
        });
    }

    private void loadData(List<Motorbike> list) {
        model.setRowCount(0);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        for (Motorbike m : list) {
            model.addRow(new Object[]{
                m.getId(), m.getLicensePlate(), m.getModel(),
                m.getBrand(), nf.format(m.getPricePerDay()) + " đ", m.getStatus()
            });
        }
        lblCount.setText("Hiển thị " + list.size() + " xe");
    }

    private JButton toolBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 32));
        return b;
    }

    private JButton crudBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 36));
        return b;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Chưa chọn", JOptionPane.WARNING_MESSAGE);
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
