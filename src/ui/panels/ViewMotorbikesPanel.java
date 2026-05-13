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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ViewMotorbikesPanel extends JPanel {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_ACCENT  = new Color(52, 152, 219);
    private static final Color C_SUCCESS = new Color(39, 174, 96);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch, txtMaxPrice;
    private List<Motorbike> currentData = new ArrayList<>();

    private final MotorbikeDAO dao = new MotorbikeDAO();

    // Callback từ UserDashboard: chuyển sang tab Thuê xe với xe đã chọn
    private Consumer<Motorbike> onRentAction;

    public ViewMotorbikesPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        initUI();
        loadData(dao.getAvailable());
    }

    public void setOnRentAction(Consumer<Motorbike> action) {
        this.onRentAction = action;
    }

    private void initUI() {
        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225)),
            new EmptyBorder(10, 16, 10, 16)));

        JLabel title = new JLabel("Xe Máy Sẵn Có");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(C_PRIMARY);

        // Thanh tìm kiếm + lọc giá
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterBar.setBackground(Color.WHITE);

        txtSearch = styledField("Tìm tên, hãng, biển số...", 180);
        txtMaxPrice = styledField("Giá tối đa (VNĐ)", 130);

        JButton btnSearch = btn("Tìm kiếm", C_ACCENT,                   Color.WHITE, 100);
        JButton btnReset  = btn("Tất cả",   new Color(149, 165, 166),    Color.WHITE, 80);

        filterBar.add(txtSearch);
        filterBar.add(txtMaxPrice);
        filterBar.add(btnSearch);
        filterBar.add(btnReset);

        header.add(title,     BorderLayout.WEST);
        header.add(filterBar, BorderLayout.EAST);

        // ---- Bảng (cột ẩn index 5 = motorbike id) ----
        String[] cols = {"ID", "Biển số", "Tên xe", "Hãng", "Giá/ngày (VNĐ)", "raw_id"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(C_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(213, 232, 252));
        table.setGridColor(new Color(220, 221, 225));
        table.setShowVerticalLines(true);

        int[] widths = {40, 110, 160, 100, 140, 0};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (widths[i] == 0) {
                table.getColumnModel().getColumn(i).setMinWidth(0);
                table.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        // Căn phải cột giá
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlign);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225)));

        // ---- Thanh nút phía dưới ----
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnBar.setBackground(new Color(245, 247, 250));

        JButton btnDetail  = btn("📋 Xem chi tiết", new Color(52, 152, 219),  Color.WHITE, 140);
        JButton btnRentNow = btn("🏍 Thuê xe này",   C_SUCCESS,                Color.WHITE, 140);
        JButton btnRefresh = btn("↻ Làm mới",        new Color(149, 165, 166), Color.WHITE, 100);

        btnBar.add(btnDetail);
        btnBar.add(btnRentNow);
        btnBar.add(btnRefresh);

        // Label đếm kết quả
        JLabel lblCount = new JLabel();
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCount.setForeground(new Color(100, 110, 120));
        btnBar.add(Box.createHorizontalStrut(10));
        btnBar.add(lblCount);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnBar, BorderLayout.SOUTH);

        // ---- Sự kiện ----
        Runnable doSearch = () -> {
            String kw       = txtSearch.getText().trim();
            String priceStr = txtMaxPrice.getText().trim();

            List<Motorbike> result;

            if (!kw.isEmpty() && !priceStr.isEmpty()) {
                // Tìm theo từ khóa, sau đó lọc thêm theo giá
                try {
                    long maxP = Long.parseLong(priceStr.replaceAll("[,.]", ""));
                    result = dao.search(kw);
                    result.removeIf(m -> m.getPricePerDay() > maxP || !"Sẵn sàng".equals(m.getStatus()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá tối đa phải là số!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else if (!kw.isEmpty()) {
                result = dao.search(kw);
                result.removeIf(m -> !"Sẵn sàng".equals(m.getStatus()));
            } else if (!priceStr.isEmpty()) {
                try {
                    long maxP = Long.parseLong(priceStr.replaceAll("[,.]", ""));
                    result = dao.searchAvailableByMaxPrice(maxP);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá tối đa phải là số!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                result = dao.getAvailable();
            }

            loadData(result);
            lblCount.setText("Tìm thấy " + result.size() + " xe");
        };

        btnSearch.addActionListener(e -> doSearch.run());
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            txtMaxPrice.setText("");
            loadData(dao.getAvailable());
            lblCount.setText("");
        });

        // Nhấn Enter trong ô tìm kiếm
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doSearch.run();
            }
        });
        txtMaxPrice.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doSearch.run();
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); txtMaxPrice.setText("");
            loadData(dao.getAvailable());
            lblCount.setText("");
        });

        btnDetail.addActionListener(e -> showDetail());

        btnRentNow.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe muốn thuê!", "Chưa chọn xe", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Motorbike selected = currentData.get(row);
            if (onRentAction != null) onRentAction.accept(selected);
        });

        // Double-click → xem chi tiết
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showDetail();
            }
        });
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một xe!"); return; }
        Motorbike m = currentData.get(row);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String msg = String.format(
            "Biển số  : %s\n" +
            "Tên xe   : %s\n" +
            "Hãng     : %s\n" +
            "Giá/ngày : %s VNĐ\n" +
            "Trạng thái: %s",
            m.getLicensePlate(), m.getModel(), m.getBrand(),
            nf.format(m.getPricePerDay()), m.getStatus());
        int choice = JOptionPane.showOptionDialog(this, msg, "Chi Tiết Xe",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
            new String[]{"Thuê xe này", "Đóng"}, "Đóng");
        if (choice == 0 && onRentAction != null) onRentAction.accept(m);
    }

    public void loadData(List<Motorbike> list) {
        model.setRowCount(0);
        currentData = new ArrayList<>(list);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        for (Motorbike m : list) {
            model.addRow(new Object[]{
                m.getId(), m.getLicensePlate(), m.getModel(),
                m.getBrand(), nf.format(m.getPricePerDay()) + " đ", m.getId()
            });
        }
    }

    private JTextField styledField(String placeholder, int width) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            new EmptyBorder(5, 8, 5, 8)));
        f.setPreferredSize(new Dimension(width, 32));
        f.setForeground(new Color(149, 165, 166));
        f.setText(placeholder);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(new Color(149, 165, 166)); }
            }
        });
        return f;
    }

    private JButton btn(String text, Color bg, Color fg, int width) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(width, 34));
        return b;
    }
}
