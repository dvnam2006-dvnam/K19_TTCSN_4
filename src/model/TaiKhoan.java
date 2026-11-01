package model;

import java.util.Date;

public class TaiKhoan {
    private String maKhachHang;
    private String hoTen;
    private String email;
    private String matKhau;
    private String soDienThoai;
    private String maSinhVien;
    private String trangThai;
    private Date ngayTao;

    public TaiKhoan() {}

    public TaiKhoan(String maKhachHang, String hoTen, String email, String matKhau,
                    String soDienThoai, String maSinhVien, String trangThai, Date ngayTao) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.maSinhVien = maSinhVien;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String v) { this.maKhachHang = v; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String v) { this.hoTen = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String v) { this.matKhau = v; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String v) { this.soDienThoai = v; }
    public String getMaSinhVien() { return maSinhVien; }
    public void setMaSinhVien(String v) { this.maSinhVien = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { this.trangThai = v; }
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date v) { this.ngayTao = v; }
}
