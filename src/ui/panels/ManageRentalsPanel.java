package ui.panels;

import dao.MotorbikeDAO;
import dao.RentalDAO;
import model.Rental;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageRentalsPanel extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color C_PRIMARY = new Color(44,  62,  80);
    private static final Color C_SUCCESS = new Color(39,  174, 96);
    private static final Color C_DANGER  = new Color(231, 76,  60);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_ACCENT  = new Color(52,  152, 219);
    private static final Color C_GRAY    = new Color(108, 117, 125);
    private static final Color C_BG      = new Color(245, 247, 250);

    // ── Trạng thái hợp lệ ────────────────────────────────────────────────────
    private static final String ST_PENDING = "Chờ xử lý";
    private static final String ST_PAID    = "Đã thanh toán";
    private static final String ST_CANCEL  = "Đã hủy";

    // ── Components ───────────────────────────────────────────────────────────
    private JTable  table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFilter;
    private JLabel  lblCount, lblStatus;

    // Các nút bị bật/tắt theo logic
    private JButton btnConfirm, btnCancel, btnDetail;

    private List<Rental> allData = new ArrayList<>();

    private final RentalDAO    rentalDAO = new RentalDAO();
    private final MotorbikeDAO motorDAO  = new MotorbikeDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public ManageRentalsPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        initUI();
        loadData();
    }

    // ── Khởi tạo UI ──────────────────────────────────────────────────────────
    private void initUI() {

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 221, 225)),
            new EmptyBorder(14, 20, 14, 20)));

        JLabel title = new JLabel("Quản Lý Đơn Thuê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);

        // Filter bên phải header
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterBar.setBackground(Color.WHITE);
        JLabel lblFilter = new JLabel("Lọc trạng thái:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFilter.setForeground(C_GRAY);
        cbFilter = new JComboBox<>(new String[]{"Tất cả", ST_PENDING, ST_PAID, ST_CANCEL});
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter.setPreferredSize(new Dimension(160, 30));
        filterBar.add(lblFilter);
        filterBar.add(cbFilter);

        header.add(title,     BorderLayout.WEST);
        header.add(filterBar, BorderLayout.EAST);

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(C_BG);
        toolbar.setBorder(new EmptyBorder(4, 16, 4, 16));

        btnConfirm = mkBtn("✔  Xác nhận trả xe", C_SUCCESS, Color.WHITE, 170);
        btnCancel  = mkBtn("✖  Hủy đơn",          C_DANGER,  Color.WHITE, 120);
        btnDetail  = mkBtn("📋  Xem chi tiết",     C_ACCENT,  Color.WHITE, 140);
        JButton btnRefresh = mkBtn("↻  Làm mới",   C_GRAY,    Color.WHITE, 110);

        lblCount  = new JLabel();
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setForeground(C_GRAY);

        lblStatus = new JLabel("  ← Chọn một đơn để thao tác");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(C_GRAY);

        toolbar.add(btnConfirm);
        toolbar.add(btnCancel);
        toolbar.add(btnDetail);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(lblCount);
        toolbar.add(lblStatus);

        // Ban đầu disable các nút cần chọn hàng
        updateButtonState(-1, null);

        // ===== BẢNG =====
        // cột ẩn 8 = motorbike_id, cột ẩn 9 = rent_date gốc (java.sql.Date)
        String[] cols = {"ID", "Khách thuê", "Xe", "Biển số",
                         "Ngày nhận", "Ngày trả", "Tổng tiền", "Trạng thái",
                         "bike_id", "rent_date_raw"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
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

        // Ẩn 2 cột cuối
        int[] widths = {40, 140, 120, 95, 88, 88, 120, 120, 0, 0};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (widths[i] == 0) {
                table.getColumnModel().getColumn(i).setMinWidth(0);
                table.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        // Renderer cột Trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                switch (s) {
                    case ST_PAID:    setForeground(C_SUCCESS); break;
                    case ST_PENDING: setForeground(C_WARN);    break;
                    case ST_CANCEL:  setForeground(C_DANGER);  break;
                    default:         setForeground(Color.BLACK);
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 221, 225)));

        // ===== CONTEXT MENU chuột phải =====
        JPopupMenu popup = buildContextMenu();
        table.setComponentPopupMenu(popup);

        // ===== GHÉP LAYOUT =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(header,  BorderLayout.NORTH);
        topBar.add(toolbar, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ===== SỰ KIỆN =====
        // Chọn hàng → cập nhật trạng thái nút
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                String st = row >= 0 ? (String) tableModel.getValueAt(row, 7) : null;
                updateButtonState(row, st);
            }
        });

        cbFilter.addActionListener(e -> applyFilter());

        btnRefresh.addActionListener(e -> loadData());
        btnConfirm.addActionListener(e -> handleConfirmReturn());
        btnCancel.addActionListener(e  -> handleCancelRental());
        btnDetail.addActionListener(e  -> showDetail());

        // Double-click → xem chi tiết
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) showDetail();
            }
        });
    }

    // ── Context Menu ─────────────────────────────────────────────────────────
    private JPopupMenu buildContextMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem miDetail  = new JMenuItem("📋  Xem chi tiết");
        JMenuItem miConfirm = new JMenuItem("✔  Xác nhận trả xe");
        JMenuItem miCancel  = new JMenuItem("✖  Hủy đơn");
        JSeparator sep = new JSeparator();

        for (JMenuItem item : new JMenuItem[]{miDetail, miConfirm, miCancel}) {
            item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        menu.add(miDetail);
        menu.add(sep);
        menu.add(miConfirm);
        menu.add(miCancel);

        // Bật/tắt item theo hàng đang chọn khi menu mở
        menu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                int row = table.getSelectedRow();
                String st = row >= 0 ? (String) tableModel.getValueAt(row, 7) : null;
                boolean canAct = ST_PENDING.equals(st);
                miDetail.setEnabled(row >= 0);
                miConfirm.setEnabled(canAct);
                miCancel.setEnabled(canAct);
                // Màu text theo trạng thái
                miConfirm.setForeground(canAct ? C_SUCCESS : C_GRAY);
                miCancel.setForeground(canAct  ? C_DANGER  : C_GRAY);
            }
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        miDetail.addActionListener(e  -> showDetail());
        miConfirm.addActionListener(e -> handleConfirmReturn());
        miCancel.addActionListener(e  -> handleCancelRental());
        return menu;
    }

    // ── Logic bật/tắt nút ────────────────────────────────────────────────────
    private void updateButtonState(int row, String status) {
        boolean rowSelected = row >= 0;
        boolean canChange   = ST_PENDING.equals(status);

        btnConfirm.setEnabled(canChange);
        btnCancel.setEnabled(canChange);
        btnDetail.setEnabled(rowSelected);

        // Màu mờ khi disabled
        btnConfirm.setBackground(canChange  ? C_SUCCESS : new Color(180, 200, 180));
        btnCancel.setBackground(canChange   ? C_DANGER  : new Color(200, 180, 180));
        btnDetail.setBackground(rowSelected ? C_ACCENT  : new Color(180, 190, 210));

        if (!rowSelected) {
            lblStatus.setText("  ← Chọn một đơn để thao tác");
            lblStatus.setForeground(C_GRAY);
        } else {
            switch (status != null ? status : "") {
                case ST_PENDING:
                    lblStatus.setText("  Đơn đang chờ — có thể xác nhận hoặc hủy");
                    lblStatus.setForeground(C_WARN); break;
                case ST_PAID:
                    lblStatus.setText("  Đơn đã hoàn tất — không thể thay đổi");
                    lblStatus.setForeground(C_SUCCESS); break;
                case ST_CANCEL:
                    lblStatus.setText("  Đơn đã hủy — không thể thay đổi");
                    lblStatus.setForeground(C_DANGER); break;
                default:
                    lblStatus.setText("");
            }
        }
    }

    // ── Xác nhận trả xe ──────────────────────────────────────────────────────
    private void handleConfirmReturn() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int  rentalId    = (int) tableModel.getValueAt(row, 0);
        int  motorbikeId = (int) tableModel.getValueAt(row, 8);
        Date rentDate    = (Date) tableModel.getValueAt(row, 9);

        // Cảnh báo nếu trả trước hạn
        LocalDate today = LocalDate.now();
        String extraMsg = "";
        if (rentDate != null && rentDate.toLocalDate().isAfter(today)) {
            long daysEarly = java.time.temporal.ChronoUnit.DAYS.between(today, rentDate.toLocalDate());
            extraMsg = "\n⚠ Lưu ý: còn " + daysEarly + " ngày mới đến ngày nhận xe theo hợp đồng.";
        }

        String khach = (String) tableModel.getValueAt(row, 1);
        String xe    = tableModel.getValueAt(row, 2) + " (" + tableModel.getValueAt(row, 3) + ")";

        int ok = JOptionPane.showConfirmDialog(this,
            "Xác nhận khách đã trả xe:\n" +
            "  Khách : " + khach + "\n" +
            "  Xe    : " + xe + "\n" +
            "  Đơn # : " + rentalId + extraMsg + "\n\n" +
            "Xe sẽ được chuyển về trạng thái Sẵn sàng.",
            "Xác Nhận Trả Xe", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (ok != JOptionPane.YES_OPTION) return;

        if (rentalDAO.updateStatus(rentalId, ST_PAID)) {
            motorDAO.updateStatus(motorbikeId, "Sẵn sàng");
            loadData();
            JOptionPane.showMessageDialog(this, "Đã xác nhận trả xe thành công!", "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Hủy đơn ──────────────────────────────────────────────────────────────
    private void handleCancelRental() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int  rentalId    = (int)    tableModel.getValueAt(row, 0);
        int  motorbikeId = (int)    tableModel.getValueAt(row, 8);
        String khach     = (String) tableModel.getValueAt(row, 1);
        String xe        = tableModel.getValueAt(row, 2) + " (" + tableModel.getValueAt(row, 3) + ")";

        int ok = JOptionPane.showConfirmDialog(this,
            "Hủy đơn thuê:\n" +
            "  Khách : " + khach + "\n" +
            "  Xe    : " + xe + "\n" +
            "  Đơn # : " + rentalId + "\n\n" +
            "Xe sẽ được chuyển về trạng thái Sẵn sàng.",
            "Xác Nhận Hủy Đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (ok != JOptionPane.YES_OPTION) return;

        if (rentalDAO.updateStatus(rentalId, ST_CANCEL)) {
            motorDAO.updateStatus(motorbikeId, "Sẵn sàng");
            loadData();
            JOptionPane.showMessageDialog(this, "Đã hủy đơn thuê #" + rentalId, "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Hủy đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Xem chi tiết ─────────────────────────────────────────────────────────
    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        String status  = (String) tableModel.getValueAt(row, 7);
        String icon    = ST_PAID.equals(status) ? "✔" : ST_CANCEL.equals(status) ? "✖" : "⏳";

        String msg =
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "  Đơn thuê #" + tableModel.getValueAt(row, 0) + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "  Khách    : " + tableModel.getValueAt(row, 1) + "\n" +
            "  Xe       : " + tableModel.getValueAt(row, 2) + "\n" +
            "  Biển số  : " + tableModel.getValueAt(row, 3) + "\n" +
            "  Ngày nhận: " + tableModel.getValueAt(row, 4) + "\n" +
            "  Ngày trả : " + tableModel.getValueAt(row, 5) + "\n" +
            "  Tổng tiền: " + tableModel.getValueAt(row, 6) + "\n" +
            "  Trạng thái: " + icon + " " + status + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

        Object[] options;
        if (ST_PENDING.equals(status)) {
            options = new Object[]{"✔ Xác nhận trả xe", "✖ Hủy đơn", "Đóng"};
            int choice = JOptionPane.showOptionDialog(this, msg, "Chi Tiết Đơn Thuê",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, "Đóng");
            if (choice == 0) handleConfirmReturn();
            else if (choice == 1) handleCancelRental();
        } else {
            JOptionPane.showMessageDialog(this, msg, "Chi Tiết Đơn Thuê", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Load & Filter dữ liệu ─────────────────────────────────────────────────
    public void loadData() {
        allData = rentalDAO.getAll();
        applyFilter();
    }

    private void applyFilter() {
        String filter = cbFilter.getSelectedItem() != null
            ? cbFilter.getSelectedItem().toString() : "Tất cả";
        tableModel.setRowCount(0);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        int count = 0;
        for (Rental r : allData) {
            if (!"Tất cả".equals(filter) && !filter.equals(r.getStatus())) continue;
            tableModel.addRow(new Object[]{
                r.getId(), r.getFullName(), r.getMotorbikeModel(), r.getLicensePlate(),
                r.getRentDate(), r.getReturnDate(),
                nf.format(r.getTotalPrice()) + " đ",
                r.getStatus(),
                r.getMotorbikeId(),
                r.getRentDate()       // cột ẩn raw Date
            });
            count++;
        }
        lblCount.setText("Hiển thị " + count + " / " + allData.size() + " đơn");
        updateButtonState(-1, null);
    }

    // ── Helper tạo nút ───────────────────────────────────────────────────────
    private JButton mkBtn(String text, Color bg, Color fg, int width) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(width, 36));
        return b;
    }
}
