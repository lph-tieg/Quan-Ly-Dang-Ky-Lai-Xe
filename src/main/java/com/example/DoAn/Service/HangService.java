package com.example.DoAn.Service;

import java.util.List;
import java.util.Optional;

import com.example.DoAn.Model.Hang;

public interface HangService {

	// Lấy danh sách hạng trong database
	List<Hang> findAll();

	// Tìm hạng theo ID
	Optional<Hang> findByHangID(Integer hangID);

	// Tìm hạng theo tên
	Optional<Hang> findByTenHang(String tenHang);

	// Tìm hạng theo tên gần giống
	List<Hang> findByTenHangContaining(String tenHang);

	// Tìm hạng theo ID
	Hang findByID(Integer hangID);

	// Thêm hạng
	void createHang(Hang hang);

	// Trả về danh sách hạng sắp xếp theo tên hạng
	List<Hang> findAllOrderByTenHang();
}
