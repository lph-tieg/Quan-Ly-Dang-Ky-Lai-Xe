package com.example.DoAn.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.XeTapLai;

public interface XeService {

	// Trả về danh sách xe
	public List<XeTapLai> findAllXe();

	// Xoá xe, lưu lại lịch sử hoạt động
	public void deleteXe(Integer xeID, String nguoiThucHien);

	// Tìm xe theo ID
	public XeTapLai findByID(Integer xeID);

	// Cập nhật xe, lưu lại lịch sử hoạt động
	public XeTapLai updateXe(XeTapLai xe, String nguoiThucHien);

	// Thêm xe mới, lưu lại lịch sử hoạt động
	public void createXe(XeTapLai xe, String nguoiThucHien);

	// Tìm xe theo tên không phân biệt chữ hoạ, thường cho phân trang
	public Page<XeTapLai> findByTenXeContainingIgnoreCase(String tenXe, Pageable pageable);

	// Tìm xe theo loại xe không phân biệt chữ hoạ, thường cho phân trang
	public Page<XeTapLai> findByLoaiXeContainingIgnoreCase(String loaiXe, Pageable pageable);

	// Tìm xe theo loại số xe không phân biệt chữ hoạ, thường cho phân trang
	public Page<XeTapLai> findByLoaiSoXeContainingIgnoreCase(String loaiSoXe, Pageable pageable);

	// Trả về danh sách xe cho phân trang
	public Page<XeTapLai> findAllListXe(Integer pageNo);

	// Tìm xe theo ID
	Optional<XeTapLai> findByXeID(Integer xeID);

	// Tìm xe theo loại xe
	List<XeTapLai> findByLoaiXe(String loaiXe);

	// Tìm xe theo hạng
	List<XeTapLai> findByHangID(Integer hangID);

	// Trả về danh sách loại xe
	List<String> findAllLoaiXe();

	// Trả về danh sách loại số xe
	List<String> findAllLoaiSoXe();

	// Cập nhật số lượng xe mỗi khi được chọn (ở trang đăng ký khoá học) hoặc cập
	// nhật xe
	void updateSoLuongXe(XeTapLai xeTapLai);

}
