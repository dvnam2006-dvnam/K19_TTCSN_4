package session;

import model.TaiKhoan;

/**
 * Singleton Session - quản lý phiên đăng nhập toàn ứng dụng
 */
public class UserSession {
    private static UserSession instance;
    private TaiKhoan currentUser;
    private String maGioHang;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(TaiKhoan user, String maGioHang) {
        this.currentUser = user;
        this.maGioHang = maGioHang;
    }

    public void logout() {
        this.currentUser = null;
        this.maGioHang = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public TaiKhoan getCurrentUser() { return currentUser; }
    public String getMaGioHang() { return maGioHang; }
    public void setMaGioHang(String maGioHang) { this.maGioHang = maGioHang; }
}
