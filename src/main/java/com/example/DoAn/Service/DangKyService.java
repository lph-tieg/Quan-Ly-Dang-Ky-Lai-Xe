package com.example.DoAn.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.DanhSachDangKy;

public interface DangKyService {
	// Tìm danh sách đăng ký dựa trên họ tên không phân biệt chữ hoa chữ thường
	Page<DanhSachDangKy> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable);

	// Tìm danh sách đăng ký dựa trên email không phân biệt chữ hoa chữ thường
	Page<DanhSachDangKy> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Lấy danh sách đăng ký cho phân trang
	Page<DanhSachDangKy> findAllListDS(Integer pageNo);

	// Lấy danh sách đăng ký
	public List<DanhSachDangKy> findAllDanhSach();

	// Tìm danh sách đăng ký theo ID
	public DanhSachDangKy findById(Integer hocVienDkId);

	// Thêm học viên khi đăng ký vào database
	public DanhSachDangKy addHocVienDK(DanhSachDangKy dangKy);

	// Xoá học viên đã đăng ký trong database
	public void deleteHocVienDK(Integer hocVienDkId);

	// Xoá học viên đã đăng ký trong database
	public void deleteDK(Integer hocVienDkId, String nguoiThucHien);

	// Tìm danh sách học viên đăng ký theo hạng và giảng viên đã chọn
	public List<DanhSachDangKy> findByHangDKAndGVChinh(String hangDK, String GVChinh);

	// Tìm học viên đăng ký theo sdt gần giống
	List<DanhSachDangKy> findBySdtContaining(String sdt);

	// Tìm học viên đăng ký theo email gần giống
	List<DanhSachDangKy> findByEmailContaining(String email);

}
