package ui.panels;

import dao.MotorbikeDAO;
import dao.RentalDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StatisticsPanel extends JPanel {

    private static final Color C_PRIMARY = new Color(44, 62, 80);

    private JLabel lblRevenue, lblTotal, lblPending, lblPaid, lblCancelled, lblBikes;
    private final RentalDAO rentalDAO = new RentalDAO();
    private final MotorbikeDAO bikeDAO = new MotorbikeDAO();

    public StatisticsPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
        loadStats();
    }

    private void initUI() {
        JLabel title = new JLabel("Thống Kê & Báo Cáo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);

        // Cards grid
        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 16));
        cards.setBackground(new Color(245, 247, 250));

        lblRevenue = createCard(cards, "Tổng Doanh Thu", new Color(52, 152, 219));
        lblTotal = createCard(cards, "Tổng Đơn Thuê", new Color(155, 89, 182));
        lblPending = createCard(cards, "Đang Chờ Xử Lý", new Color(230, 126, 34));
        lblPaid = createCard(cards, "Đã Thanh Toán", new Color(39, 174, 96));
        lblCancelled = createCard(cards, "Đã Hủy", new Color(231, 76, 60));
        lblBikes = createCard(cards, "Xe Sẵn Sàng", new Color(26, 188, 156));

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottom.setBackground(new Color(245, 247, 250));
        JButton btnRefresh = actionBtn("Làm mới", new Color(52, 152, 219));
        JButton btnExport = actionBtn("Xuất báo cáo (.txt)", new Color(39, 174, 96));
        bottom.add(btnRefresh);
        bottom.add(btnExport);

        add(title, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadStats());
        btnExport.addActionListener(e -> exportReport());
    }

    private JLabel createCard(JPanel parent, String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225)),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel top = new JPanel();
        top.setBackground(color);
        top.setPreferredSize(new Dimension(0, 6));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(100, 110, 120));

        JLabel lblValue = new JLabel("...");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);

        JPanel body = new JPanel(new GridLayout(2, 1, 0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(12, 0, 0, 0));
        body.add(lblTitle);
        body.add(lblValue);

        card.add(top, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        parent.add(card);
        return lblValue;
    }

    public void loadStats() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        long revenue = rentalDAO.getTotalRevenue();
        lblRevenue.setText(nf.format(revenue) + " đ");
        lblTotal.setText(String.valueOf(rentalDAO.countAll()));
        lblPending.setText(String.valueOf(rentalDAO.countByStatus("Chờ xử lý")));
        lblPaid.setText(String.valueOf(rentalDAO.countByStatus("Đã thanh toán")));
        lblCancelled.setText(String.valueOf(rentalDAO.countByStatus("Đã hủy")));
        lblBikes.setText(String.valueOf(bikeDAO.getAvailable().size()));
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("BaoCao_ThuexeMay.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            pw.println("========================================");
            pw.println("    BÁO CÁO HỆ THỐNG THUÊ XE MÁY");
            pw.println("========================================");
            pw.println("Thời gian xuất: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            pw.println("----------------------------------------");
            pw.println("Tổng doanh thu (đã TT) : " + nf.format(rentalDAO.getTotalRevenue()) + " VNĐ");
            pw.println("Tổng số đơn thuê       : " + rentalDAO.countAll());
            pw.println("  - Chờ xử lý          : " + rentalDAO.countByStatus("Chờ xử lý"));
            pw.println("  - Đã thanh toán       : " + rentalDAO.countByStatus("Đã thanh toán"));
            pw.println("  - Đã hủy              : " + rentalDAO.countByStatus("Đã hủy"));
            pw.println("Xe đang sẵn sàng        : " + bikeDAO.getAvailable().size());
            pw.println("========================================");
            JOptionPane.showMessageDialog(this,
                    "Xuất báo cáo thành công!\n" + chooser.getSelectedFile().getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 38));
        return b;
    }
}
