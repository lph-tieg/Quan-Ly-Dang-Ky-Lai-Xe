package com.example.DoAn.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.LichSuHoatDong;

@Repository
public interface LichSuHoatDongRepository extends JpaRepository<LichSuHoatDong, Integer> {
	// Trả về danh sách lịch sử hoạt động theo thứ tự mới nhất trước, cũ nhất sau
	List<LichSuHoatDong> findAllByOrderByThoiGianDesc();
}
