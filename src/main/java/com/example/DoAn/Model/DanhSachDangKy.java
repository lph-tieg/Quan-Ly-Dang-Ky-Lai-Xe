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
@Table(name = "danhSachDangKy")
public class DanhSachDangKy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "hoTen", nullable = false)
	private String hoTen;

	@Column(name = "sdt", nullable = false, unique = true)
	private String sdt;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "diaChi", nullable = false)
	private String diaChi;

	@Column(name = "hangDK", nullable = false)
	private String hangDK;

	@Column(name = "loaiXeDK", nullable = false)
	private String loaiXeDK;

	@Column(name = "lichHoc")
	private String lichHoc;

	@Column(name = "buoiHoc")
	private String buoiHoc;

	@Column(name = "thucHanh")
	private String loaiThucHanh;

	@Column(name = "ghiChu")
	private String ghiChu;

	@Column(name = "giangVienChinh")
	private String GVChinh;

	@Column(name = "ngayDK", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date ngayDKKH;

	@ManyToOne
	@JoinColumn(name = "khoaHocID")
	private KhoaHoc khoaHoc;

	@ManyToOne
	@JoinColumn(name = "xeID")
	private XeTapLai xeTapLai;

	@ManyToOne
	@JoinColumn(name = "giangVienChinhID")
	private GiangVien giangVienChinh;

	@ManyToOne
	@JoinColumn(name = "hang_id")
	private Hang hang;

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getLichHoc() {
		return lichHoc;
	}

	public void setLichHoc(String lichHoc) {
		this.lichHoc = lichHoc;
	}

	public String getBuoiHoc() {
		return buoiHoc;
	}

	public void setBuoiHoc(String buoiHoc) {
		this.buoiHoc = buoiHoc;
	}

	public String getLoaiThucHanh() {
		return loaiThucHanh;
	}

	public void setLoaiThucHanh(String loaiThucHanh) {
		this.loaiThucHanh = loaiThucHanh;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public String getGVChinh() {
		return GVChinh;
	}

	public void setGVChinh(String gVChinh) {
		GVChinh = gVChinh;
	}

	public Date getNgayDKKH() {
		return ngayDKKH;
	}

	public void setNgayDKKH(Date ngayDKKH) {
		this.ngayDKKH = ngayDKKH;
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

	public GiangVien getGiangVienChinh() {
		return giangVienChinh;
	}

	public void setGiangVienChinh(GiangVien giangVienChinh) {
		this.giangVienChinh = giangVienChinh;
	}

}
