package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_ACCENT  = new Color(39, 174, 96);

    private JTextField txtUsername, txtFullName, txtPhone;
    private JPasswordField txtPassword, txtConfirm;
    private final UserDAO userDAO = new UserDAO();

    public RegisterDialog(Frame owner) {
        super(owner, "Đăng Ký Tài Khoản", true);
        setSize(420, 500);
        setLocationRelativeTo(owner);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Tạo tài khoản mới", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(10, 1, 0, 6));
        form.setBackground(Color.WHITE);
        form.setAlignmentX(CENTER_ALIGNMENT);
        form.setMaximumSize(new Dimension(320, 360));

        txtUsername = new JTextField();
        txtFullName = new JTextField();
        txtPhone    = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirm  = new JPasswordField();

        form.add(label("Tên đăng nhập *")); form.add(styled(txtUsername));
        form.add(label("Họ và tên *"));     form.add(styled(txtFullName));
        form.add(label("Số điện thoại"));   form.add(styled(txtPhone));
        form.add(label("Mật khẩu *"));      form.add(styled(txtPassword));
        form.add(label("Nhập lại mật khẩu *")); form.add(styled(txtConfirm));

        JButton btnReg = new JButton("ĐĂNG KÝ");
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReg.setBackground(C_ACCENT);
        btnReg.setForeground(Color.WHITE);
        btnReg.setFocusPainted(false);
        btnReg.setBorderPainted(false);
        btnReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReg.setAlignmentX(CENTER_ALIGNMENT);
        btnReg.setMaximumSize(new Dimension(320, 42));

        main.add(title);
        main.add(Box.createVerticalStrut(20));
        main.add(form);
        main.add(Box.createVerticalStrut(18));
        main.add(btnReg);

        setContentPane(main);
        btnReg.addActionListener(e -> handleRegister());
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_PRIMARY);
        return l;
    }

    private <T extends JTextField> T styled(T f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone    = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm  = new String(txtConfirm.getPassword()).trim();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (*)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập phải có ít nhất 4 ký tự!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập chỉ gồm chữ, số và dấu gạch dưới (_)!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!phone.isEmpty() && !phone.matches("^0[0-9]{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! (VD: 0901234567)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setFullName(fullName);
        u.setPhone(phone);
        if (userDAO.register(u)) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
