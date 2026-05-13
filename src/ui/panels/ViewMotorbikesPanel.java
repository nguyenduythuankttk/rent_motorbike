package ui.panels;

import dao.MotorbikeDAO;
import model.Motorbike;
import ui.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ViewMotorbikesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch, txtMaxPrice;
    private List<Motorbike> currentData = new ArrayList<>();
    private final MotorbikeDAO dao = new MotorbikeDAO();
    private Consumer<Motorbike> onRentAction;

    public ViewMotorbikesPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UIStyles.BACKGROUND);
        initUI();
        loadData(dao.getAvailable());
    }

    public void setOnRentAction(Consumer<Motorbike> action) {
        this.onRentAction = action;
    }

    private void initUI() {
        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Khám phá xe máy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIStyles.TEXT_MAIN);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterBar.setBackground(UIStyles.BACKGROUND);

        JLabel lblSearch = new JLabel("Tên xe:");
        lblSearch.setFont(UIStyles.FONT_LABEL);
        lblSearch.setForeground(UIStyles.TEXT_MUTED);

        txtSearch = UIStyles.createTextField();
        txtSearch.setPreferredSize(new Dimension(200, 40));

        JLabel lblPrice = new JLabel("Giá tối đa:");
        lblPrice.setFont(UIStyles.FONT_LABEL);
        lblPrice.setForeground(UIStyles.TEXT_MUTED);

        txtMaxPrice = UIStyles.createTextField();
        txtMaxPrice.setPreferredSize(new Dimension(150, 40));

        UIStyles.ModernButton btnSearch = new UIStyles.ModernButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 40));

        filterBar.add(lblSearch);
        filterBar.add(txtSearch);
        filterBar.add(lblPrice);
        filterBar.add(txtMaxPrice);
        filterBar.add(btnSearch);

        header.add(title, BorderLayout.WEST);
        header.add(filterBar, BorderLayout.EAST);

        // ---- Table ----
        String[] cols = {"ID", "Biển số", "Tên xe", "Hãng", "Giá/ngày", "raw_id"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIStyles.styleTable(table);

        int[] widths = {50, 120, 200, 120, 150, 0};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (widths[i] == 0) {
                table.getColumnModel().getColumn(i).setMinWidth(0);
                table.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlign);

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

        // ---- Footer Actions ----
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        footer.setBackground(UIStyles.BACKGROUND);

        UIStyles.ModernButton btnRentNow = new UIStyles.ModernButton("🏍 Thuê xe ngay");
        btnRentNow.setPreferredSize(new Dimension(160, 45));
        
        UIStyles.ModernButton btnRefresh = new UIStyles.ModernButton("↻ Làm mới");
        btnRefresh.setPreferredSize(new Dimension(130, 45));

        footer.add(btnRentNow);
        footer.add(btnRefresh);

        add(header, BorderLayout.NORTH);
        add(tableWrapper, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ---- Events ----
        Runnable doSearch = () -> {
            String kw = txtSearch.getText().trim();
            String priceStr = txtMaxPrice.getText().trim();
            List<Motorbike> result;
            if (!kw.isEmpty() || !priceStr.isEmpty()) {
                result = dao.getAvailable(); // Simplified logic for demo
                if (!kw.isEmpty()) result.removeIf(m -> !m.getModel().toLowerCase().contains(kw.toLowerCase()));
                if (!priceStr.isEmpty()) {
                    try {
                        long maxP = Long.parseLong(priceStr.replaceAll("[,.]", ""));
                        result.removeIf(m -> m.getPricePerDay() > maxP);
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                result = dao.getAvailable();
            }
            loadData(result);
        };

        btnSearch.addActionListener(e -> doSearch.run());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); txtMaxPrice.setText("");
            loadData(dao.getAvailable());
        });

        btnRentNow.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe muốn thuê!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Motorbike selected = currentData.get(row);
            if (onRentAction != null) onRentAction.accept(selected);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) btnRentNow.doClick();
            }
        });
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
}
