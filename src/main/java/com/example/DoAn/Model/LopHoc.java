package com.example.DoAn.Model;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "lopHoc")
public class LopHoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer lopHocID;

	@Column(name = "tenLop")
	private String tenLop;

	@Column(name = "lichHoc")
	private String lichHoc;

	@Column(name = "buoiHoc")
	private String buoiHoc;

	@Column(name = "soLuong")
	private Long soLuong = 0L;

	@ManyToMany(mappedBy = "lopHocs")
	private List<HocVien> hocViens = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "lopHoc_giangVien", joinColumns = @JoinColumn(name = "lopHoc_id"), inverseJoinColumns = @JoinColumn(name = "giangVien_id"))
	private List<GiangVien> listGiangVien;

	@ManyToOne
	@JoinColumn(name = "khoaHocID")
	private KhoaHoc khoaHoc;

	@ManyToOne
	@JoinColumn(name = "hang_id")
	private Hang hang;

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public List<HocVien> getHocViens() {
		return hocViens;
	}

	public void setHocViens(List<HocVien> hocViens) {
		this.hocViens = hocViens;
	}

	public List<GiangVien> getListGiangVien() {
		return listGiangVien;
	}

	public void setListGiangVien(List<GiangVien> listGiangVien) {
		this.listGiangVien = listGiangVien;
	}

	public Integer getLopHocID() {
		return lopHocID;
	}

	public void setLopHocID(Integer lopHocID) {
		this.lopHocID = lopHocID;
	}

	public String getTenLop() {
		return tenLop;
	}

	public void setTenLop(String tenLop) {
		this.tenLop = tenLop;
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

	public Long getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(Long soLuong) {
		this.soLuong = soLuong;
	}

	public KhoaHoc getKhoaHoc() {
		return khoaHoc;
	}

	public void setKhoaHoc(KhoaHoc khoaHoc) {
		this.khoaHoc = khoaHoc;
	}

	public void tangSoLuong() {
		if (this.soLuong == null) {
			this.soLuong = 0L;
		}
		this.soLuong++;
	}

	public void giamSoLuong() {
		if (this.soLuong != null && this.soLuong > 0) {
			this.soLuong--;
		}
	}

	public void capNhatSoLuong() {
		this.soLuong = (long) this.hocViens.size();
	}

	@Override
	public String toString() {
		return tenLop;
	}

}
