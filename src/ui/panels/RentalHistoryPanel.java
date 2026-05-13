package ui.panels;

import dao.MotorbikeDAO;
import dao.RentalDAO;
import model.Rental;
import session.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentalHistoryPanel extends JPanel {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_SUCCESS = new Color(39, 174, 96);
    private static final Color C_DANGER = new Color(231, 76, 60);
    private static final Color C_WARN = new Color(230, 126, 34);

    private JTable table;
    private DefaultTableModel model;
    private List<Rental> currentData = new ArrayList<>();

    private final RentalDAO rentalDAO = new RentalDAO();
    private final MotorbikeDAO motorDAO = new MotorbikeDAO();

    public RentalHistoryPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        // ----- Header -----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225)),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel title = new JLabel("Lịch Sử Thuê Xe Của Bạn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(C_PRIMARY);

        JButton btnRefresh = smallBtn("Làm mới", new Color(52, 152, 219), Color.WHITE, 100);
        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        // ----- Bảng (cột ẩn index 7 = motorbike_id) -----
        String[] cols = { "ID", "Xe", "Biển số", "Ngày nhận", "Ngày trả", "Tổng tiền", "Trạng thái", "bike_id" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(C_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(213, 232, 252));
        table.setGridColor(new Color(220, 221, 225));

        int[] widths = { 40, 140, 100, 95, 95, 130, 120, 0 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (widths[i] == 0) {
                table.getColumnModel().getColumn(i).setMinWidth(0);
                table.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        // Màu cột Trạng thái
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v != null ? v.toString() : "";
                switch (s) {
                    case "Đã thanh toán":
                        setForeground(C_SUCCESS);
                        break;
                    case "Chờ xử lý":
                        setForeground(C_WARN);
                        break;
                    case "Đã hủy":
                        setForeground(C_DANGER);
                        break;
                    default:
                        setForeground(Color.BLACK);
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225)));

        // ----- Nút thao tác -----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton btnCancel = smallBtn("✖ Hủy đơn", C_DANGER, Color.WHITE, 120);

        JLabel hint = new JLabel("  (Chỉ hủy được khi đơn 'Chờ xử lý' và chưa đến ngày nhận xe)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(149, 165, 166));

        btnPanel.add(btnCancel);
        btnPanel.add(hint);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());
        btnCancel.addActionListener(e -> handleCancel());
    }

    private void handleCancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn thuê muốn hủy!", "Chưa chọn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rentalId = (int) model.getValueAt(row, 0);
        String status = (String) model.getValueAt(row, 6);
        Date rentDate = (Date) model.getValueAt(row, 3);
        int motorbikeId = (int) model.getValueAt(row, 7); // cột ẩn

        // Kiểm tra trạng thái
        if (!"Chờ xử lý".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hủy đơn đang ở trạng thái \"" + status + "\".",
                    "Không thể hủy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra ngày: chỉ hủy được nếu chưa đến ngày nhận
        LocalDate today = LocalDate.now();
        LocalDate rentLocal = rentDate.toLocalDate();
        if (!rentLocal.isAfter(today)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hủy — ngày nhận xe (" + rentDate
                            + ") đã đến hoặc đã qua.\nVui lòng liên hệ admin để được hỗ trợ.",
                    "Không thể hủy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy đơn thuê #" + rentalId + "?\n(Ngày nhận: " + rentDate + ")",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        if (rentalDAO.updateStatus(rentalId, "Đã hủy")) {
            motorDAO.updateStatus(motorbikeId, "Sẵn sàng");
            loadData();
            JOptionPane.showMessageDialog(this, "Đã hủy đơn thuê thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Hủy đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadData() {
        model.setRowCount(0);
        currentData = rentalDAO.getByUser(Session.getCurrentUser().getId());
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        for (Rental r : currentData) {
            model.addRow(new Object[] {
                    r.getId(),
                    r.getMotorbikeModel(),
                    r.getLicensePlate(),
                    r.getRentDate(),
                    r.getReturnDate(),
                    nf.format(r.getTotalPrice()) + " đ",
                    r.getStatus(),
                    r.getMotorbikeId() // cột ẩn index 7
            });
        }
    }

    private JButton smallBtn(String text, Color bg, Color fg, int width) {
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
