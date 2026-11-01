package utils;

import javax.swing.*;
import java.awt.*;

public class AppUtils {

    /** Format tiền VND: 1.200.000 đ */
    public static String formatTien(long amount) {
        return String.format("%,d đ", amount).replace(",", ".");
    }

    /** Hiển thị thông báo lỗi */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /** Hiển thị thông báo thành công */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Hiển thị xác nhận Yes/No */
    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /** Validate email */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /** Validate số điện thoại VN */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(0[3|5|7|8|9])[0-9]{8}$");
    }

    /** Truncate text */
    public static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    /** Màu chủ đạo của ứng dụng */
    public static final Color PRIMARY_COLOR    = new Color(0x2563EB);  // Blue
    public static final Color SECONDARY_COLOR  = new Color(0x7C3AED);  // Purple
    public static final Color SUCCESS_COLOR    = new Color(0x16A34A);  // Green
    public static final Color DANGER_COLOR     = new Color(0xDC2626);  // Red
    public static final Color WARNING_COLOR    = new Color(0xD97706);  // Orange
    public static final Color BG_COLOR         = new Color(0xF8FAFC);  // Light gray
    public static final Color CARD_COLOR       = Color.WHITE;
    public static final Color BORDER_COLOR     = new Color(0xE2E8F0);
    public static final Color TEXT_PRIMARY     = new Color(0x1E293B);
    public static final Color TEXT_SECONDARY   = new Color(0x64748B);

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 13);

    /** Tạo nút bấm màu xanh chủ đạo */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    /** Nút outline */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY_COLOR);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Tạo label tiêu đề */
    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Tạo JTextField đẹp */
    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tf.setPreferredSize(new Dimension(250, 36));
        return tf;
    }

    /** Tạo JPasswordField đẹp */
    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        pf.setPreferredSize(new Dimension(250, 36));
        return pf;
    }

    /** Gradient panel */
    public static JPanel createGradientPanel(Color c1, Color c2) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
}
