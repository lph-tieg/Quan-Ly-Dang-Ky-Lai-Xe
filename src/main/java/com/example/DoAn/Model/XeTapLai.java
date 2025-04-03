package com.example.DoAn.Model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "xe")
public class XeTapLai {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer xeID;

	@Column(name = "loaiXe", length = 50)
	private String loaiXe;

	@Column(name = "soLuong")
	private Integer soLuong;

	@Column(name = "soLuongConLai")
	private Integer soLuongConLai;

	@Column(name = "tenXe", length = 30)
	private String tenXe;

	@Column(name = "loaiSoXe", length = 30)
	private String loaiSoXe;

	@Column(name = "giaThue")
	private Long giaThue;

	@OneToMany(mappedBy = "xeTapLai", cascade = CascadeType.ALL)
	private List<HocVien> hocViens;

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

	public Long getGiaThue() {
		return giaThue;
	}

	public void setGiaThue(Long giaThue) {
		this.giaThue = giaThue;
	}

	public Integer getXeID() {
		return xeID;
	}

	public void setXeID(Integer xeID) {
		this.xeID = xeID;
	}

	public String getLoaiXe() {
		return loaiXe;
	}

	public void setLoaiXe(String loaiXe) {
		this.loaiXe = loaiXe;
	}

	public Integer getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(Integer soLuong) {
		this.soLuong = soLuong;
		this.soLuongConLai = soLuong; // Khi set số lượng, cập nhật luôn số lượng còn lại
	}

	public Integer getSoLuongConLai() {
		return soLuongConLai;
	}

	public void setSoLuongConLai(Integer soLuongConLai) {
		this.soLuongConLai = soLuongConLai;
	}

	public String getTenXe() {
		return tenXe;
	}

	public void setTenXe(String tenXe) {
		this.tenXe = tenXe;
	}

	public String getLoaiSoXe() {
		return loaiSoXe;
	}

	public void setLoaiSoXe(String loaiSoXe) {
		this.loaiSoXe = loaiSoXe;
	}

	public void giamSoLuongConLai() {
		if (this.soLuongConLai > 0) {
			this.soLuongConLai--;
		}
	}

	public void tangSoLuongConLai() {
		if (this.soLuongConLai < this.soLuong) {
			this.soLuongConLai++;
		}
	}
}
