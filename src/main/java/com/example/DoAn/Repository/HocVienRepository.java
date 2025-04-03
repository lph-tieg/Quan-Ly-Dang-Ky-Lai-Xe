package com.example.DoAn.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.LopHoc;

public interface HocVienRepository extends JpaRepository<HocVien, Integer> {
//	List<HocVien> findByHoTenContainingIgnoreCase(String hoTen);
//
//	List<HocVien> findBySdtContaining(String sdt);

//	List<HocVien> findByEmailContainingIgnoreCase(String email);
//
//	List<HocVien> findByHangDKContainingIgnoreCase(String hangDK);
//
//	List<HocVien> findByLoaiXeDKContainingIgnoreCase(String loaiXeDK);

	// Tìm học viên theo email không phân biệt chữ hoa, thường cho phân trang
	Page<HocVien> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Tìm học viên theo hạng không phân biệt chữ hoa, thường cho phân trang
	Page<HocVien> findByHangDKContainingIgnoreCase(String hangDK, Pageable pageable);

	// Tìm học viên theo loại xe không phân biệt chữ hoa, thường cho phân trang
	Page<HocVien> findByLoaiXeDKContainingIgnoreCase(String loaiXeDK, Pageable pageable);

	// Tìm học viên theo tên không phân biệt chữ hoa, thường cho phân trang
	Page<HocVien> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable);

	// Tìm học viên theo tên không phân biệt chữ hoa, thường
	List<HocVien> findByHoTenContainingIgnoreCase(String hoTen);

	// Tìm học viên theo sdt gần giống
	Page<HocVien> findBySdtContaining(String sdt, Pageable pageable);

	// Tìm học viên theo hạng cho phân trang
	Page<HocVien> findByHangDK(String hang, Pageable pageable);

	// Tìm học viên theo buổi học cho phân trang
	Page<HocVien> findByBuoiHoc(String buoiHoc, Pageable pageable);

	// Tìm học viên theo lịch học cho phân trang
	Page<HocVien> findByLichHoc(String lichHoc, Pageable pageable);

	// Tìm học viên theo lịch học không trùng lăp
	@Query("SELECT DISTINCT h.lichHoc FROM HocVien h WHERE h.lichHoc IS NOT NULL ORDER BY h.lichHoc")
	List<String> findDistinctLichHoc();

	// Tìm học viên theo ID lớp học
	@Query("SELECT h FROM HocVien h JOIN h.lopHocs l WHERE l.lopHocID = :lopHocID")
	List<HocVien> findByLopHocID(@Param("lopHocID") Integer lopHocID);

	// Tìm học viên theo lớp học
	@Query("SELECT h FROM HocVien h JOIN h.lopHocs l WHERE l = :lopHoc")
	List<HocVien> findByLopHoc(@Param("lopHoc") LopHoc lopHoc);

	// Tìm tất cả học viên chưa có trong lớp học hiện tại
	@Query("SELECT h FROM HocVien h WHERE NOT EXISTS (SELECT 1 FROM h.lopHocs l WHERE l.lopHocID = :lopHocID)")
	List<HocVien> findHocVienNotInLopHoc(@Param("lopHocID") Integer lopHocID);

	// Cập nhật giảng viên dạy cho học viên
	@Modifying
	@Transactional
	@Query("UPDATE HocVien hv SET hv.giangVienChinh = NULL WHERE hv.giangVienChinh = :giangVien")
	void updateGiangVienChinh(@Param("giangVien") GiangVien giangVien);

	// Kiểm tra email đã tồn tại hay chưa
	boolean existsByEmail(String email);

	// Kiểm tra sdt đã tồn tại hay chưa
	boolean existsBySdt(String sdt);

	// Kiểm tra email đã tồn tại hay chưa nhưng loại bỏ học viên có ID
	boolean existsByEmailAndHocVienIDNot(String email, Integer hocVienID);

	// Kiểm tra sdt đã tồn tại hay chưa nhưng loại bỏ học viên có ID
	boolean existsBySdtAndHocVienIDNot(String sdt, Integer hocVienID);
}
