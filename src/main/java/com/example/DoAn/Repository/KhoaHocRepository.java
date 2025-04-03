package com.example.DoAn.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DoAn.Model.KhoaHoc;

public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Integer> {
	// Tìm khoá học theo tên không phân biệt chữ hoạ, thường cho phân trang
	Page<KhoaHoc> findByTenKhoaHocContainingIgnoreCase(String tenKhoaHoc, Pageable pageable);

	// Tìm khoá học theo tên
	List<KhoaHoc> findByTenKhoaHoc(String tenKhoaHoc);

//	@Query("SELECT k FROM KhoaHoc k JOIN BuoiThucHanh b ON k.lichHoc = b.buoiHoc "
//			+ "WHERE b.buoiHoc = :buoiHoc AND k.lichHoc = :lichHoc")
//	List<KhoaHoc> findKhoaHocByLichAndBuoi(@Param("lichHoc") String lichHoc, @Param("buoiHoc") String buoiHoc);

}
