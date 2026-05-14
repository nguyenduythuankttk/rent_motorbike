package ui.panels;

import dao.MotorbikeDAO;
import dao.RentalDAO;
import model.Motorbike;
import model.Rental;
import session.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class RentMotorbikePanel extends JPanel {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_SUCCESS = new Color(39, 174, 96);
    private static final Color C_ACCENT = new Color(52, 152, 219);
    private static final Color C_MUTED = new Color(149, 165, 166);

    private JComboBox<Motorbike> cbMotorbike;
    private JTextField txtRentDate, txtReturnDate;
    private JLabel lblTotal, lblDays, lblPricePerDay;

    private final MotorbikeDAO bikeDAO = new MotorbikeDAO();
    private final RentalDAO rentalDAO = new RentalDAO();

    public RentMotorbikePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
    }

    private void initUI() {
        // ---- Tiêu đề ----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(245, 247, 250));
        titleBar.setBorder(new EmptyBorder(16, 20, 8, 20));
        JLabel title = new JLabel("Đặt Thuê Xe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);

        // ---- Form card (GridBagLayout) ----
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225)),
                new EmptyBorder(24, 36, 24, 36)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;

        // -- Chọn xe --
        g.gridy = 0;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(sectionLabel("Chọn xe"), g);

        cbMotorbike = new JComboBox<>();
        cbMotorbike.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbMotorbike.setPreferredSize(new Dimension(400, 36));
        g.gridy = 1;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(cbMotorbike, g);

        // Giá/ngày hint
        lblPricePerDay = new JLabel(" ");
        lblPricePerDay.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblPricePerDay.setForeground(C_ACCENT);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 16, 0);
        card.add(lblPricePerDay, g);

        // -- Ngày nhận --
        g.gridy = 3;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(sectionLabel("Ngày nhận xe (yyyy-MM-dd)"), g);
        txtRentDate = styledField(LocalDate.now().toString());
        g.gridy = 4;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(txtRentDate, g);

        // -- Ngày trả --
        g.gridy = 5;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(sectionLabel("Ngày trả xe (yyyy-MM-dd)"), g);
        txtReturnDate = styledField(LocalDate.now().plusDays(1).toString());
        g.gridy = 6;
        g.insets = new Insets(0, 0, 20, 0);
        card.add(txtReturnDate, g);

        // -- Tổng kết --
        JPanel summaryBox = new JPanel(new GridLayout(2, 2, 8, 4));
        summaryBox.setBackground(new Color(240, 248, 255));
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 215, 245)),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lDays = new JLabel("Số ngày:");
        lDays.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lDays.setForeground(C_PRIMARY);
        lblDays = new JLabel("—");
        lblDays.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lTotal = new JLabel("Tổng tiền:");
        lTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTotal.setForeground(C_PRIMARY);
        lblTotal = new JLabel("—");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(C_SUCCESS);

        summaryBox.add(lDays);
        summaryBox.add(lblDays);
        summaryBox.add(lTotal);
        summaryBox.add(lblTotal);
        g.gridy = 7;
        g.insets = new Insets(0, 0, 20, 0);
        card.add(summaryBox, g);

        // -- Nút hành động --
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(Color.WHITE);

        JButton btnCalc = actionBtn("🔢 Tính tiền", C_ACCENT, Color.WHITE);
        JButton btnConfirm = actionBtn("✔ Xác nhận đặt xe", C_SUCCESS, Color.WHITE);
        btnRow.add(btnCalc);
        btnRow.add(btnConfirm);
        g.gridy = 8;
        g.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, g);

        // ---- Wrapper để căn card ----
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        GridBagConstraints wc = new GridBagConstraints();
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.weightx = 1.0;
        wc.weighty = 0;
        wc.anchor = GridBagConstraints.NORTH;
        wc.gridy = 0;
        wrapper.add(card, wc);
        // Filler để đẩy card lên trên
        wc.gridy = 1;
        wc.weighty = 1.0;
        wc.fill = GridBagConstraints.BOTH;
        wrapper.add(Box.createGlue(), wc);

        add(titleBar, BorderLayout.NORTH);
        add(new JScrollPane(wrapper, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        loadMotorbikes();

        // Sự kiện
        cbMotorbike.addActionListener(e -> updatePriceHint());
        btnCalc.addActionListener(e -> calculateTotal());
        btnConfirm.addActionListener(e -> handleBooking());

        // Tự động tính khi thay đổi ngày
        txtRentDate.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                calculateTotal();
            }
        });
        txtReturnDate.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                calculateTotal();
            }
        });
    }

    /** Gọi từ UserDashboard khi user nhấn "Thuê xe này" từ danh sách */
    public void selectMotorbike(Motorbike m) {
        loadMotorbikes();
        for (int i = 0; i < cbMotorbike.getItemCount(); i++) {
            if (cbMotorbike.getItemAt(i).getId() == m.getId()) {
                cbMotorbike.setSelectedIndex(i);
                break;
            }
        }
        calculateTotal();
    }

    public void loadMotorbikes() {
        cbMotorbike.removeAllItems();
        List<Motorbike> available = bikeDAO.getAvailable();
        for (Motorbike m : available)
            cbMotorbike.addItem(m);
        updatePriceHint();
        calculateTotal();
    }

    private void updatePriceHint() {
        Motorbike m = (Motorbike) cbMotorbike.getSelectedItem();
        if (m == null) {
            lblPricePerDay.setText(" ");
            return;
        }
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblPricePerDay.setText("Giá thuê: " + nf.format(m.getPricePerDay()) + " VNĐ/ngày");
    }

    private void calculateTotal() {
        Motorbike m = (Motorbike) cbMotorbike.getSelectedItem();
        if (m == null) {
            lblDays.setText("—");
            lblTotal.setText("—");
            return;
        }
        try {
            String rentStr = txtRentDate.getText().trim();
            String returnStr = txtReturnDate.getText().trim();
            Date rent = Date.valueOf(rentStr);
            Date ret = Date.valueOf(returnStr);
            long days = (ret.getTime() - rent.getTime()) / (1000L * 60 * 60 * 24);
            if (days <= 0) {
                lblDays.setText("Ngày không hợp lệ");
                lblTotal.setText("—");
                return;
            }
            long total = days * m.getPricePerDay();
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            lblDays.setText(days + " ngày");
            lblTotal.setText(nf.format(total) + " VNĐ");
        } catch (Exception ex) {
            lblDays.setText("Định dạng sai");
            lblTotal.setText("—");
        }
    }

    private void handleBooking() {
        Motorbike m = (Motorbike) cbMotorbike.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Không có xe sẵn sàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date rentDate, returnDate;
        try {
            rentDate = Date.valueOf(txtRentDate.getText().trim());
            returnDate = Date.valueOf(txtReturnDate.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!\nDùng định dạng: yyyy-MM-dd (VD: 2026-05-20)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rentDate.toLocalDate().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày nhận xe không được là ngày trong quá khứ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long days = (returnDate.getTime() - rentDate.getTime()) / (1000L * 60 * 60 * 24);
        if (days <= 0) {
            JOptionPane.showMessageDialog(this, "Ngày trả phải sau ngày nhận xe!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long total = days * m.getPricePerDay();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        String summary = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  Xe     : %s %s\n" +
                        "  Biển số: %s\n" +
                        "  Nhận   : %s\n" +
                        "  Trả    : %s\n" +
                        "  Số ngày: %d ngày\n" +
                        "  Tổng   : %s VNĐ\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                m.getBrand(), m.getModel(), m.getLicensePlate(),
                rentDate, returnDate, days, nf.format(total));

        int choice = JOptionPane.showConfirmDialog(this, summary, "Xác Nhận Đặt Xe",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION)
            return;

        Rental r = new Rental();
        r.setUserId(Session.getCurrentUser().getId());
        r.setMotorbikeId(m.getId());
        r.setRentDate(rentDate);
        r.setReturnDate(returnDate);
        r.setTotalPrice(total);

        if (rentalDAO.addAndLockBike(r)) {
            JOptionPane.showMessageDialog(this,
                    "Đặt xe thành công!\nĐơn #" + m.getModel() + " đang chờ xác nhận.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadMotorbikes();
            txtRentDate.setText(LocalDate.now().toString());
            txtReturnDate.setText(LocalDate.now().plusDays(1).toString());
            calculateTotal();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đặt xe thất bại! Xe có thể vừa được đặt bởi người khác. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            loadMotorbikes();
        }
    }

    private JTextField styledField(String text) {
        JTextField f = new JTextField(text);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                new EmptyBorder(7, 10, 7, 10)));
        f.setPreferredSize(new Dimension(400, 36));
        return f;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(C_PRIMARY);
        return l;
    }

    private JButton actionBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 42));
        b.setMinimumSize(new Dimension(180, 42));
        return b;
    }
}
