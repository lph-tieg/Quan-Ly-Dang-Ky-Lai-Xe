package com.example.DoAn.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lich_su_hoat_dong")
public class LichSuHoatDong {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "nguoiThucHien")
	private String nguoiThucHien;
	@Column(name = "loaiHoatDong")
	private String loaiHoatDong;
	@Column(name = "doiTuong")
	private String doiTuong;
	@Column(name = "noiDung")
	private String noiDung;
	@Column(name = "thoiGian")
	private LocalDateTime thoiGian;

	public LichSuHoatDong() {
	}

	public LichSuHoatDong(Integer id, String nguoiThucHien, String loaiHoatDong, String doiTuong, String noiDung,
			LocalDateTime thoiGian) {
		super();
		this.id = id;
		this.nguoiThucHien = nguoiThucHien;
		this.loaiHoatDong = loaiHoatDong;
		this.doiTuong = doiTuong;
		this.noiDung = noiDung;
		this.thoiGian = LocalDateTime.now();
	}

	public LichSuHoatDong(String nguoiThucHien, String loaiHoatDong, String doiTuong, String noiDung) {

		this.nguoiThucHien = nguoiThucHien;
		this.loaiHoatDong = loaiHoatDong;
		this.doiTuong = doiTuong;
		this.noiDung = noiDung;
		this.thoiGian = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNguoiThucHien() {
		return nguoiThucHien;
	}

	public void setNguoiThucHien(String nguoiThucHien) {
		this.nguoiThucHien = nguoiThucHien;
	}

	public String getLoaiHoatDong() {
		return loaiHoatDong;
	}

	public void setLoaiHoatDong(String loaiHoatDong) {
		this.loaiHoatDong = loaiHoatDong;
	}

	public String getDoiTuong() {
		return doiTuong;
	}

	public void setDoiTuong(String doiTuong) {
		this.doiTuong = doiTuong;
	}

	public String getNoiDung() {
		return noiDung;
	}

	public void setNoiDung(String noiDung) {
		this.noiDung = noiDung;
	}

	public LocalDateTime getThoiGian() {
		return thoiGian;
	}

	public void setThoiGian(LocalDateTime thoiGian) {
		this.thoiGian = thoiGian;
	}

}
