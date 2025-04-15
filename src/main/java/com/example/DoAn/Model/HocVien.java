package com.example.DoAn.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

	@ManyToMany
	@JoinTable(name = "hocvien_lophoc", joinColumns = @JoinColumn(name = "hocvien_id"), inverseJoinColumns = @JoinColumn(name = "lophoc_id"))
	private List<LopHoc> lopHocs = new ArrayList<>();

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

	@Column(name = "TrangThai")
	private String trangThai; // Đang học / Đã hoàn thành

	@Column(name = "ThoiGianHoc")
	private Integer thoiGianHoc; // Tổng thời gian học của hạng (giờ)

	@Column(name = "ThoiGianDaHoc")
	private Double thoiGianDaHoc; // Thời gian đã học (giờ)

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public void setLopHocs(List<LopHoc> lopHocs) {
		this.lopHocs = lopHocs;
	}

	public void addLopHoc(LopHoc lopHoc) {
		if (!this.lopHocs.contains(lopHoc)) {
			this.lopHocs.add(lopHoc);
			if (!lopHoc.getHocViens().contains(this)) {
				lopHoc.getHocViens().add(this);
			}
		}
	}

	public void removeLopHoc(LopHoc lopHoc) {
		this.lopHocs.remove(lopHoc);
		lopHoc.getHocViens().remove(this);
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

	public List<LopHoc> getLopHocs() {
		return lopHocs;
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

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public Integer getThoiGianHoc() {
		return thoiGianHoc;
	}

	public void setThoiGianHoc(Integer thoiGianHoc) {
		this.thoiGianHoc = thoiGianHoc;
	}

	public Double getThoiGianDaHoc() {
		return thoiGianDaHoc;
	}

	public void setThoiGianDaHoc(Double thoiGianDaHoc) {
		this.thoiGianDaHoc = thoiGianDaHoc;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		HocVien other = (HocVien) obj;
		return Objects.equals(hocVienID, other.hocVienID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hocVienID);
	}

}
