package com.example.DoAn.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.DoAn.Model.HocVienTuVan;

public interface TuVanRepository extends JpaRepository<HocVienTuVan, Integer> {

	// Tìm người dùng đăng ký tư vấn theo tên không phân biệt chữ hoa, thường
	List<HocVienTuVan> findByNameContainingIgnoreCase(String name);

	// Tìm người dùng đăng ký tư vấn theo sdt không phân biệt chữ hoa, thường
	List<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt);

	// Tìm người dùng đăng ký tư vấn theo email không phân biệt chữ hoa, thường
	List<HocVienTuVan> findByEmailContainingIgnoreCase(String email);

	// Tìm người dùng đăng ký tư vấn theo tên không phân biệt chữ hoa, thường cho
	// phân trang
	Page<HocVienTuVan> findByNameContainingIgnoreCase(String name, Pageable pageable);

	// Tìm người dùng đăng ký tư vấn theo sdt không phân biệt chữ hoa, thường cho
	// phân trang
	Page<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt, Pageable pageable);

	// Tìm người dùng đăng ký tư vấn theo email không phân biệt chữ hoa, thường cho
	// phân trang
	Page<HocVienTuVan> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Tìm học viên theo ID
	Optional<HocVienTuVan> findById(Integer tuVanID);

	// Kiểm tra email đã tồn tại hay chưa
	boolean existsByEmail(String email);

	// Kiểm tra sdt đã tồn tại hay chưa
	boolean existsBySdt(String sdt);

	// Tìm người dùng đăng ký tư vấn theo hạng không trùng lặp
	@Query("SELECT DISTINCT h.hangDK FROM HocVienTuVan h ORDER BY h.hangDK")
	List<String> findDistinctHangDK();
}
