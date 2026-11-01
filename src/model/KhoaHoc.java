package model;

import java.util.Date;

public class KhoaHoc {
    private String maKhoaHoc;
    private String maDanhMuc;
    private String tenKhoaHoc;
    private String moTa;
    private String giangVien;
    private long hocPhi;
    private long giaKhuyenMai;
    private String thoiLuong;
    private String capDo;
    private String trangThai;
    private String anhKhoaHoc;
    private Date ngayTao;

    public KhoaHoc() {}

    public KhoaHoc(String maKhoaHoc, String maDanhMuc, String tenKhoaHoc, String moTa,
                   String giangVien, long hocPhi, long giaKhuyenMai, String thoiLuong,
                   String capDo, String trangThai, String anhKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
        this.maDanhMuc = maDanhMuc;
        this.tenKhoaHoc = tenKhoaHoc;
        this.moTa = moTa;
        this.giangVien = giangVien;
        this.hocPhi = hocPhi;
        this.giaKhuyenMai = giaKhuyenMai;
        this.thoiLuong = thoiLuong;
        this.capDo = capDo;
        this.trangThai = trangThai;
        this.anhKhoaHoc = anhKhoaHoc;
    }

    /** Giá hiển thị: ưu tiên giá khuyến mãi */
    public long getGiaHienThi() {
        return giaKhuyenMai > 0 && giaKhuyenMai < hocPhi ? giaKhuyenMai : hocPhi;
    }

    public boolean coDang() { return "Đang mở".equals(trangThai); }

    public String getMaKhoaHoc() { return maKhoaHoc; }
    public void setMaKhoaHoc(String v) { this.maKhoaHoc = v; }
    public String getMaDanhMuc() { return maDanhMuc; }
    public void setMaDanhMuc(String v) { this.maDanhMuc = v; }
    public String getTenKhoaHoc() { return tenKhoaHoc; }
    public void setTenKhoaHoc(String v) { this.tenKhoaHoc = v; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String v) { this.moTa = v; }
    public String getGiangVien() { return giangVien; }
    public void setGiangVien(String v) { this.giangVien = v; }
    public long getHocPhi() { return hocPhi; }
    public void setHocPhi(long v) { this.hocPhi = v; }
    public long getGiaKhuyenMai() { return giaKhuyenMai; }
    public void setGiaKhuyenMai(long v) { this.giaKhuyenMai = v; }
    public String getThoiLuong() { return thoiLuong; }
    public void setThoiLuong(String v) { this.thoiLuong = v; }
    public String getCapDo() { return capDo; }
    public void setCapDo(String v) { this.capDo = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { this.trangThai = v; }
    public String getAnhKhoaHoc() { return anhKhoaHoc; }
    public void setAnhKhoaHoc(String v) { this.anhKhoaHoc = v; }
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date v) { this.ngayTao = v; }
}
