package dao;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Quản lý kết nối CSDL SQL Server (HaUICourseDB)
 * Tự động nạp JDBC Driver từ thư mục lib/ và kết nối trực tiếp tới SQL Server.
 */
public class DBConnection {

    // =====================================================
    // CẤU HÌNH KẾT NỐI SQL SERVER
    // =====================================================
    private static final String SERVER   = "localhost";
    private static final String PORT     = "1433";
    private static final String DATABASE = "HaUICourseDB";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";
    // =====================================================

    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ":" + PORT + ";databaseName=" + DATABASE
        + ";encrypt=false;trustServerCertificate=true;loginTimeout=3";

    private static boolean driverLoaded = false;
    private static boolean mockMode = false;
    private static String statusMessage = "";

    public static boolean isMockMode() {
        return mockMode;
    }

    public static void setMockMode(boolean mock) {
        mockMode = mock;
    }

    public static String getStatusMessage() {
        return statusMessage;
    }

    private static synchronized void ensureDriverLoaded() throws ClassNotFoundException {
        if (driverLoaded) return;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            driverLoaded = true;
            return;
        } catch (ClassNotFoundException ignored) {
            // Thử tự động nạp file jar từ thư mục lib/ nếu chạy trực tiếp chưa gắn classpath
            try {
                File jarFile = new File("lib/mssql-jdbc-12.4.2.jre11.jar");
                if (!jarFile.exists()) {
                    jarFile = new File("HaUI_CourseApp/lib/mssql-jdbc-12.4.2.jre11.jar");
                }
                if (jarFile.exists()) {
                    URLClassLoader loader = new URLClassLoader(
                        new URL[]{jarFile.toURI().toURL()},
                        DBConnection.class.getClassLoader()
                    );
                    Class<?> clazz = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver", true, loader);
                    Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
                    DriverManager.registerDriver(new DriverShim(driver));
                    driverLoaded = true;
                    return;
                }
            } catch (Exception e) {
                throw new ClassNotFoundException("Không thể nạp SQLServerDriver từ lib/", e);
            }
        }
        throw new ClassNotFoundException("Không tìm thấy SQLServerDriver.");
    }

    public static Connection getConnection() throws SQLException {
        if (mockMode) {
            return null;
        }
        try {
            ensureDriverLoaded();
            DriverManager.setLoginTimeout(3);
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statusMessage = "SQL Server (" + SERVER + ":" + PORT + "/" + DATABASE + ")";
            mockMode = false;
            return conn;
        } catch (ClassNotFoundException e) {
            mockMode = true;
            statusMessage = "Chế độ Dữ liệu mẫu (Không có Driver SQL)";
            throw new SQLException("Không tìm thấy JDBC Driver.", e);
        } catch (SQLException e) {
            mockMode = true;
            statusMessage = "Chế độ Dữ liệu mẫu (Chưa kết nối SQL Server)";
            throw e;
        }
    }

    public static void closeConnection() {
        // Mỗi kết nối được đóng tự động qua try-with-resources tại tầng DAO
    }

    /** Kiểm tra kết nối CSDL khi khởi động */
    public static boolean testConnection() {
        mockMode = false;
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            mockMode = true;
            statusMessage = "Chế độ Dữ liệu mẫu (" + e.getMessage() + ")";
            return false;
        }
    }

    /** Lớp bọc Driver giúp DriverManager nhận diện Driver nạp động từ URLClassLoader */
    private static class DriverShim implements Driver {
        private final Driver driver;
        DriverShim(Driver d) { this.driver = d; }
        @Override public boolean acceptsURL(String u) throws SQLException { return this.driver.acceptsURL(u); }
        @Override public Connection connect(String u, Properties p) throws SQLException { return this.driver.connect(u, p); }
        @Override public int getMajorVersion() { return this.driver.getMajorVersion(); }
        @Override public int getMinorVersion() { return this.driver.getMinorVersion(); }
        @Override public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException { return this.driver.getPropertyInfo(u, p); }
        @Override public boolean jdbcCompliant() { return this.driver.jdbcCompliant(); }
        @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { return this.driver.getParentLogger(); }
    }
}
