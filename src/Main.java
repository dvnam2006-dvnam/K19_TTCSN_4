import dao.DBConnection;
import session.UserSession;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện Look and Feel theo hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        System.out.println("==================================================");
        System.out.println("  KHỞI ĐỘNG HỆ THỐNG HAUI COURSE APP - KHOA CNTT  ");
        System.out.println("==================================================");

        // 1. Kiểm tra kết nối CSDL SQL Server (HaUICourseDB)
        System.out.println("Đang kiểm tra kết nối CSDL SQL Server...");
        boolean dbOk = DBConnection.testConnection();
        if (dbOk) {
            System.out.println("=> [OK] Đã kết nối thành công CSDL: " + DBConnection.getStatusMessage());
        } else {
            System.out.println("=> [THÔNG BÁO] " + DBConnection.getStatusMessage());
            System.out.println("=> Đang tự động kích hoạt chế độ Dữ liệu mẫu (Mock Mode).");
        }

        // 2. Mặc định chưa đăng nhập để mở trực tiếp màn hình Đăng nhập khi khởi động
        UserSession.getInstance().logout();

        // 3. Khởi chạy giao diện chính MainFrame (Mặc định tại màn hình Đăng nhập)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
