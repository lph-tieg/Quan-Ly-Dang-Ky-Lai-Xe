package com.example.DoAn.Model;

import java.util.Date;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tuVan")
public class HocVienTuVan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tuVanID;

	@Column(name = "name", length = 30)
	private String name;
	@Column(name = "sdt", length = 12)
	private String sdt;
	@Column(name = "email", length = 30)
	private String email;
	@Column(name = "hangDk", length = 30)
	private String hangDK;

	@Column(name = "diaChi", length = 30)
	private String diaChi;

	@ManyToOne
	@JoinColumn(name = "hang_id")
	private Hang hang;

	@Column(nullable = false)
	private boolean trangThai = false;

	@Column(name = "ngayTuVan")
	private Date ngayTuVan;

	public Date getNgayTuVan() {
		return ngayTuVan;
	}

	public void setNgayTuVan(Date ngayTuVan) {
		this.ngayTuVan = ngayTuVan;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	public Hang getHang() {
		return hang;
	}

	public void setHang(Hang hang) {
		this.hang = hang;
	}

	public Integer getTuVanID() {
		return tuVanID;
	}

	public void setTuVanID(Integer tuVanID) {
		this.tuVanID = tuVanID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getHangDK() {
		return hangDK;
	}

	public void setHangDK(String hangDK) {
		this.hangDK = hangDK;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

}
