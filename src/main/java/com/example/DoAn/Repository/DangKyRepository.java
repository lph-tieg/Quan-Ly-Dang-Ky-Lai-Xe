package com.example.DoAn.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.DanhSachDangKy;

@Repository
public interface DangKyRepository extends JpaRepository<DanhSachDangKy, Integer> {
	// Tìm học viên đăng ký theo tên không phân biệt chữ hoa, thường cho phân trang
	Page<DanhSachDangKy> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable);

	// Tìm học viên đăng ký theo email không phân biệt chữ hoa, thường cho phân
	// trang
	Page<DanhSachDangKy> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Tìm học viên đăng ký theo hạng và giảng viên được chọn
	List<DanhSachDangKy> findByHangDKAndGVChinh(String hangDK, String GVChinh);

	// Tìm học viên đăng ký theo sdt gần giống
	List<DanhSachDangKy> findBySdtContaining(String sdt);

	// Tìm học viên đăng ký theo email gần giống
	List<DanhSachDangKy> findByEmailContaining(String email);
}
