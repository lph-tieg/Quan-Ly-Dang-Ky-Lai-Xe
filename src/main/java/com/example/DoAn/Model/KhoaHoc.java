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
@Table(name = "khoaHoc")
public class KhoaHoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer khoaHocID;

	@Column(name = "tenKhoaHoc", length = 50, nullable = false)
	private String tenKhoaHoc;

	@OneToMany(mappedBy = "khoaHoc", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<LopHoc> lopHocs;

	@OneToMany(mappedBy = "tenHang", cascade = CascadeType.ALL)
	private List<Hang> hangs;

	public List<Hang> getHangs() {
		return hangs;
	}

	public void setHangs(List<Hang> hangs) {
		this.hangs = hangs;
	}

	public List<LopHoc> getLopHocs() {
		return lopHocs;
	}

	public void setLopHocs(List<LopHoc> lopHocs) {
		this.lopHocs = lopHocs;
	}

	public Integer getKhoaHocID() {
		return khoaHocID;
	}

	public void setKhoaHocID(Integer khoaHocID) {
		this.khoaHocID = khoaHocID;
	}

	public String getTenKhoaHoc() {
		return tenKhoaHoc;
	}

	public void setTenKhoaHoc(String tenKhoaHoc) {
		this.tenKhoaHoc = tenKhoaHoc;
	}

//	public List<HocVien> getHocVien() {
//		return hocVien;
//	}
//
//	public void setHocVien(List<HocVien> hocVien) {
//		this.hocVien = hocVien;
//	}
//
//	public GiangVien getGiangVien() {
//		return giangVien;
//	}
//
//	public void setGiangVien(GiangVien giangVien) {
//		this.giangVien = giangVien;
//	}
//
//	public List<GiangVien> getGiangViens() {
//		return giangViens;
//	}
//
//	public void setGiangViens(List<GiangVien> giangViens) {
//		this.giangViens = giangViens;
//	}
//

}
