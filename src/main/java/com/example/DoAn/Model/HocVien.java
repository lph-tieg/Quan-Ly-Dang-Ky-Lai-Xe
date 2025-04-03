package com.example.DoAn.Model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hocVien")
public class HocVien {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer hocVienID;

	@Column(name = "hoTen", length = 50)
	private String hoTen;

	@Column(name = "sdt", length = 11)
	private String sdt;

	@Column(name = "diaChi", length = 100)
	private String diaChi;

	@Column(name = "hangDK", length = 100)
	private String hangDK;

	@Column(name = "email", length = 30)
	private String email;

	@Column(name = "loaiXeDK", length = 100)
	private String loaiXeDK;

	@Column(name = "buoiHoc", length = 50)
	private String buoiHoc;

	@Column(name = "ghiChu")
	private String ghiChu;

	@Column(name = "lichHoc")
	private String lichHoc;

	@ManyToOne
	@JoinColumn(name = "lopHocID")
	private LopHoc lopHoc;

	@Column(name = "loaiThucHanh")
	private String loaiThucHanh;

	@Column(name = "giangVienChinh")
	private String GVChinh;

	@Column(name = "giangVienPhu")
	private String GVPhu;

	@Column(name = "ngayHoc")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date ngayBatDauHoc;

	@Column(name = "ngayDKKH")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date ngayDKKH = new Date();

	@Column(name = "role")
	private String role = "users";

	@ManyToOne
	@JoinColumn(name = "hang_id")
	private Hang hang;

	@ManyToOne
	@JoinColumn(name = "khoaHocID")
	private KhoaHoc khoaHoc;

	@ManyToOne
	@JoinColumn(name = "xeID")
	private XeTapLai xeTapLai;

	@ManyToOne
	@JoinColumn(name = "giangVienChinhID")
	private GiangVien giangVienChinh; // Mối quan hệ với giảng viên chính

	@ManyToOne
	@JoinColumn(name = "giangVienPhuID")
	private GiangVien giangVienPhu; // Mối quan hệ với giảng viên phụ

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public void setLopHoc(LopHoc lopHoc) {
		this.lopHoc = lopHoc;
	}

	public String getLoaiThucHanh() {
		return loaiThucHanh;
	}

	public void setLoaiThucHanh(String loaiThucHanh) {
		this.loaiThucHanh = loaiThucHanh;
	}

	public Date getNgayBatDauHoc() {
		return ngayBatDauHoc;
	}

	public void setNgayBatDauHoc(Date ngayBatDauHoc) {
		this.ngayBatDauHoc = ngayBatDauHoc;
	}

	public GiangVien getGiangVienChinh() {
		return giangVienChinh;
	}

	public void setGiangVienChinh(GiangVien giangVienChinh) {
		this.giangVienChinh = giangVienChinh;
	}

	public GiangVien getGiangVienPhu() {
		return giangVienPhu;
	}

	public void setGiangVienPhu(GiangVien giangVienPhu) {
		this.giangVienPhu = giangVienPhu;
	}

	public String getGVChinh() {
		return GVChinh;
	}

	public void setGVChinh(String gVChinh) {
		GVChinh = gVChinh;
	}

	public String getGVPhu() {
		return GVPhu;
	}

	public void setGVPhu(String gVPhu) {
		GVPhu = gVPhu;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public String getLichHoc() {
		return lichHoc;
	}

	public void setLichHoc(String lichHoc) {
		this.lichHoc = lichHoc;
	}

	public LopHoc getLopHoc() {
		return lopHoc;
	}

	public KhoaHoc getKhoaHoc() {
		return khoaHoc;
	}

	public void setKhoaHoc(KhoaHoc khoaHoc) {
		this.khoaHoc = khoaHoc;
	}

	public XeTapLai getXeTapLai() {
		return xeTapLai;
	}

	public void setXeTapLai(XeTapLai xeTapLai) {
		this.xeTapLai = xeTapLai;
	}

//	

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getHocVienID() {
		return hocVienID;
	}

	public void setHocVienID(Integer hocVienID) {
		this.hocVienID = hocVienID;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getNgayDKKH() {
		return ngayDKKH;
	}

	public void setNgayDKKH(Date ngayDKKH) {
		this.ngayDKKH = ngayDKKH;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getHangDK() {
		return hangDK;
	}

	public void setHangDK(String hangDK) {
		this.hangDK = hangDK;
	}

	public String getLoaiXeDK() {
		return loaiXeDK;
	}

	public void setLoaiXeDK(String loaiXeDK) {
		this.loaiXeDK = loaiXeDK;
	}

	public String getBuoiHoc() {
		return buoiHoc;
	}

	public void setBuoiHoc(String buoiHoc) {
		this.buoiHoc = buoiHoc;
	}

}
