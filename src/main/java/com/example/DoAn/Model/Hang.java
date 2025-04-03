package com.example.DoAn.Model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hang")
public class Hang {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer hangID;

	@Column(name = "tenHang")
	private String tenHang;

//	@ManyToOne
//	@JoinColumn(name = "khoaHoc_id")
//	private KhoaHoc khoaHoc;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<LopHoc> lopHocs;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<GiangVien> giangViens;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<HocVien> hocViens;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<DanhSachDangKy> dangKys;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<XeTapLai> xeTapLais;

	@OneToMany(mappedBy = "hang", cascade = CascadeType.ALL)
	private List<HocVienTuVan> tuVans;

	public List<HocVienTuVan> getTuVans() {
		return tuVans;
	}

	public void setTuVans(List<HocVienTuVan> tuVans) {
		this.tuVans = tuVans;
	}

	public Integer getHangID() {
		return hangID;
	}

	public void setHangID(Integer hangID) {
		this.hangID = hangID;
	}

	public String getTenHang() {
		return tenHang;
	}

	public void setTenHang(String tenHang) {
		this.tenHang = tenHang;
	}

//	public KhoaHoc getKhoaHoc() {
//		return khoaHoc;
//	}
//
//	public void setKhoaHoc(KhoaHoc khoaHoc) {
//		this.khoaHoc = khoaHoc;
//	}

	public List<LopHoc> getLopHocs() {
		return lopHocs;
	}

	public void setLopHocs(List<LopHoc> lopHocs) {
		this.lopHocs = lopHocs;
	}

	public List<GiangVien> getGiangViens() {
		return giangViens;
	}

	public void setGiangViens(List<GiangVien> giangViens) {
		this.giangViens = giangViens;
	}

	public List<HocVien> getHocViens() {
		return hocViens;
	}

	public void setHocViens(List<HocVien> hocViens) {
		this.hocViens = hocViens;
	}

	public List<DanhSachDangKy> getDangKys() {
		return dangKys;
	}

	public void setDangKys(List<DanhSachDangKy> dangKys) {
		this.dangKys = dangKys;
	}

	public List<XeTapLai> getXeTapLais() {
		return xeTapLais;
	}

	public void setXeTapLais(List<XeTapLai> xeTapLais) {
		this.xeTapLais = xeTapLais;
	}

	@Override
	public String toString() {
		return tenHang;
	}

}
