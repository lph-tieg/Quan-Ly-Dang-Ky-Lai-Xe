package com.example.DoAn.Model;

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
@Table(name = "buoiThucHanh")
public class BuoiThucHanh {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;

	@Column(name = "buoiHoc", nullable = false)
	private String buoiHoc;
	@Column(name = "thoiGian", nullable = false)
	private String thoiGian;
	@Column(name = "diaDiem", nullable = false)
	private String diaDiem;
	@Column(name = "loaiThucHanh", nullable = false)
	private String loaiThucHanh;

	@ManyToOne
	@JoinColumn(name = "khoaHocID")
	private KhoaHoc khoaHoc;

	public Integer getId() {
		return Id;
	}

	public KhoaHoc getKhoaHoc() {
		return khoaHoc;
	}

	public void setKhoaHoc(KhoaHoc khoaHoc) {
		this.khoaHoc = khoaHoc;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getBuoiHoc() {
		return buoiHoc;
	}

	public void setBuoiHoc(String buoiHoc) {
		this.buoiHoc = buoiHoc;
	}

	public String getThoiGian() {
		return thoiGian;
	}

	public void setThoiGian(String thoiGian) {
		this.thoiGian = thoiGian;
	}

	public String getDiaDiem() {
		return diaDiem;
	}

	public void setDiaDiem(String diaDiem) {
		this.diaDiem = diaDiem;
	}

	public String getLoaiThucHanh() {
		return loaiThucHanh;
	}

	public void setLoaiThucHanh(String loaiThucHanh) {
		this.loaiThucHanh = loaiThucHanh;
	}

}
