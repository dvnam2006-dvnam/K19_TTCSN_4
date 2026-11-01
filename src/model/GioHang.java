package model;

import java.util.Date;

public class GioHang {
    private String maGioHang;
    private String maKhachHang;
    private Date ngayCapNhat;

    public GioHang() {}
    public GioHang(String maGioHang, String maKhachHang) {
        this.maGioHang = maGioHang;
        this.maKhachHang = maKhachHang;
    }

    public String getMaGioHang() { return maGioHang; }
    public void setMaGioHang(String v) { this.maGioHang = v; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String v) { this.maKhachHang = v; }
    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date v) { this.ngayCapNhat = v; }
}
