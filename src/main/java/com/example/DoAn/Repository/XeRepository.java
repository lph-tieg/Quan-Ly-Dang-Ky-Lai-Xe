package com.example.DoAn.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.DoAn.Model.XeTapLai;

public interface XeRepository extends JpaRepository<XeTapLai, Integer> {
	// Tìm xe theo tên không phân biệt chữ hoa, thường
	List<XeTapLai> findByTenXeContainingIgnoreCase(String tenXe);

	// Tìm xe theo loại xe không phân biệt chữ hoa, thường
	List<XeTapLai> findByLoaiXeContainingIgnoreCase(String loaiXe);

	// Tìm xe theo loại số xe không phân biệt chữ hoa, thường
	List<XeTapLai> findByLoaiSoXeContainingIgnoreCase(String loaiSoXe);

	// Tìm xe theo tên không phân biệt chữ hoa, thường cho phân trang
	Page<XeTapLai> findByTenXeContainingIgnoreCase(String tenXe, Pageable pageable);

	// Tìm xe theo loại xe không phân biệt chữ hoa, thường cho phân trang
	Page<XeTapLai> findByLoaiXeContainingIgnoreCase(String loaiXe, Pageable pageable);

	// Tìm xe theo loại số xe không phân biệt chữ hoa, thường cho phân trang
	Page<XeTapLai> findByLoaiSoXeContainingIgnoreCase(String loaiSoXe, Pageable pageable);

	// Tìm xe theo ID
	Optional<XeTapLai> findByXeID(Integer xeID);

	// Tìm xe theo hạng
	List<XeTapLai> findByHangHangID(Integer hangID);

	// Tìm xe theo loại xe không trùng lặp
	@Query("SELECT DISTINCT x.loaiXe FROM XeTapLai x ORDER BY x.loaiXe")
	List<String> findDistinctLoaiXe();

	// Tìm xe theo loại số xe không trùng lặp
	@Query("SELECT DISTINCT x.loaiSoXe FROM XeTapLai x ORDER BY x.loaiSoXe")
	List<String> findDistinctLoaiSoXe();

}
