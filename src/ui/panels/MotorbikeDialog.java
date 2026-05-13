package ui.panels;

import dao.MotorbikeDAO;
import model.Motorbike;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MotorbikeDialog extends JDialog {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_ACCENT  = new Color(52, 152, 219);

    private JTextField txtPlate, txtModel, txtBrand, txtPrice;
    private JComboBox<String> cbStatus;
    private boolean saved = false;
    private final Motorbike existing;
    private final MotorbikeDAO dao = new MotorbikeDAO();

    public MotorbikeDialog(Window owner, Motorbike m) {
        super(owner, m == null ? "Thêm Xe Mới" : "Sửa Thông Tin Xe", ModalityType.APPLICATION_MODAL);
        this.existing = m;
        setSize(400, 420);
        setLocationRelativeTo(owner);
        setResizable(false);
        initUI();
        if (m != null) fillData(m);
    }

    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel(existing == null ? "Thêm xe mới" : "Cập nhật thông tin xe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(C_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(10, 1, 0, 6));
        form.setBackground(Color.WHITE);
        form.setAlignmentX(CENTER_ALIGNMENT);
        form.setMaximumSize(new Dimension(320, 330));

        txtPlate  = new JTextField();
        txtModel  = new JTextField();
        txtBrand  = new JTextField();
        txtPrice  = new JTextField();
        cbStatus  = new JComboBox<>(new String[]{"Sẵn sàng", "Đang thuê", "Bảo trì"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        form.add(lbl("Biển số xe *"));   form.add(styled(txtPlate));
        form.add(lbl("Tên xe *"));       form.add(styled(txtModel));
        form.add(lbl("Hãng xe *"));      form.add(styled(txtBrand));
        form.add(lbl("Giá thuê/ngày *")); form.add(styled(txtPrice));
        form.add(lbl("Trạng thái"));     form.add(cbStatus);

        JButton btnSave = new JButton(existing == null ? "THÊM XE" : "CẬP NHẬT");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(C_ACCENT);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setAlignmentX(CENTER_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(320, 42));

        main.add(title);
        main.add(Box.createVerticalStrut(16));
        main.add(form);
        main.add(Box.createVerticalStrut(16));
        main.add(btnSave);

        setContentPane(main);
        btnSave.addActionListener(e -> handleSave());
    }

    private void fillData(Motorbike m) {
        txtPlate.setText(m.getLicensePlate());
        txtModel.setText(m.getModel());
        txtBrand.setText(m.getBrand());
        txtPrice.setText(String.valueOf(m.getPricePerDay()));
        cbStatus.setSelectedItem(m.getStatus());
    }

    private void handleSave() {
        String plate = txtPlate.getText().trim();
        String model = txtModel.getText().trim();
        String brand = txtBrand.getText().trim();
        String priceStr = txtPrice.getText().trim();

        if (plate.isEmpty() || model.isEmpty() || brand.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long price;
        try {
            price = Long.parseLong(priceStr.replaceAll("[,.]", ""));
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá thuê phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Motorbike m = existing != null ? existing : new Motorbike();
        m.setLicensePlate(plate);
        m.setModel(model);
        m.setBrand(brand);
        m.setPricePerDay(price);
        m.setStatus((String) cbStatus.getSelectedItem());

        boolean ok = existing == null ? dao.add(m) : dao.update(m);
        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, existing == null ? "Thêm xe thành công!" : "Cập nhật thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thất bại! Biển số có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_PRIMARY);
        return l;
    }

    private JTextField styled(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }
}
