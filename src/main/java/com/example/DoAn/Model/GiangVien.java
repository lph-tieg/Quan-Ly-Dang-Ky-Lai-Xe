package com.example.DoAn.Model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "giangVien")
public class GiangVien {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer giangVienID;

	@Column(name = "hoTenGV", length = 50)
	private String hoTenGV;

	@Column(name = "sdtGV", length = 12)
	private String sdtGV;

	@Column(name = "emailGV", length = 30)
	private String emailGV;

	@Column(name = "lichDay")
	private String lichDay;

	@Column(name = "buoiDay")
	private String buoiDay;

	@ManyToOne
	@JoinColumn(name = "khoaHoc_id")
	private KhoaHoc khoaHoc;

	@OneToMany(mappedBy = "giangVienChinh")
	private List<HocVien> hocVienChinh; // Học viên có giảng viên chính này

	@OneToMany(mappedBy = "giangVienPhu")
	private List<HocVien> hocVienPhu; // Học viên có giảng viên phụ này

	@ManyToOne
	@JoinColumn(name = "hang_id")
	private Hang hang;

//	@OneToMany(mappedBy = "giangVien", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<KhoaHoc> khoaHoc = new ArrayList<>();
//	
//	@ManyToMany(mappedBy = "giangViens")
//	private List<KhoaHoc> khoaHocs = new ArrayList<>();

	@JsonIgnore
	@ManyToMany(mappedBy = "listGiangVien")
	private List<LopHoc> lopHocs;

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public List<LopHoc> getLopHocs() {
		return lopHocs;
	}

	public void setLopHocs(List<LopHoc> lopHocs) {
		this.lopHocs = lopHocs;
	}

	public String getSdtGV() {
		return sdtGV;
	}

	public KhoaHoc getKhoaHoc() {
		return khoaHoc;
	}

	public void setKhoaHoc(KhoaHoc khoaHoc) {
		this.khoaHoc = khoaHoc;
	}

	public List<HocVien> getHocVienChinh() {
		return hocVienChinh;
	}

	public void setHocVienChinh(List<HocVien> hocVienChinh) {
		this.hocVienChinh = hocVienChinh;
	}

	public List<HocVien> getHocVienPhu() {
		return hocVienPhu;
	}

	public void setHocVienPhu(List<HocVien> hocVienPhu) {
		this.hocVienPhu = hocVienPhu;
	}

	public String getLichDay() {
		return lichDay;
	}

	public void setLichDay(String lichDay) {
		this.lichDay = lichDay;
	}

	public String getBuoiDay() {
		return buoiDay;
	}

	public void setBuoiDay(String buoiDay) {
		this.buoiDay = buoiDay;
	}

	public void setSdtGV(String sdtGV) {
		this.sdtGV = sdtGV;
	}

	public String getEmailGV() {
		return emailGV;
	}

	public void setEmailGV(String emailGV) {
		this.emailGV = emailGV;
	}

	public Integer getGiangVienID() {
		return giangVienID;
	}

	public void setGiangVienID(Integer giangVienID) {
		this.giangVienID = giangVienID;
	}

	public String getHoTenGV() {
		return hoTenGV;
	}

	public void setHoTenGV(String hoTenGV) {
		this.hoTenGV = hoTenGV;
	}

	@Override
	public String toString() {
		return hoTenGV;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GiangVien giangVien = (GiangVien) o;
		return Objects.equals(giangVienID, giangVien.giangVienID) &&
				Objects.equals(hoTenGV, giangVien.hoTenGV);
	}

	@Override
	public int hashCode() {
		return Objects.hash(giangVienID, hoTenGV);
	}

//	public List<KhoaHoc> getKhoaHoc() {
//		return khoaHoc;
//	}
//
//	public void setKhoaHoc(List<KhoaHoc> khoaHoc) {
//		this.khoaHoc = khoaHoc;
//	}
//
//	public List<KhoaHoc> getKhoaHocs() {
//		return khoaHocs;
//	}
//
//	public void setKhoaHocs(List<KhoaHoc> khoaHocs) {
//		this.khoaHocs = khoaHocs;
//	}

}
