package com.example.DoAn.Service.IMPL;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DoAn.Model.LichSuHoatDong;
import com.example.DoAn.Repository.LichSuHoatDongRepository;
import com.example.DoAn.Service.LichSuHoatDongService;

@Service
public class LichSuHoatDongServiceImpl implements LichSuHoatDongService {

	@Autowired
	private LichSuHoatDongRepository lichSuRepo;

	// Thêm lịch sử hoạt động
	@Override
	@Transactional
	public void themLichSu(String nguoiThucHien, String loaiHoatDong, String doiTuong, String noiDung) {
		// TODO Auto-generated method stub

		LichSuHoatDong lichSu = new LichSuHoatDong();
		lichSu.setNguoiThucHien(nguoiThucHien);
		lichSu.setLoaiHoatDong(loaiHoatDong);
		lichSu.setDoiTuong(doiTuong);
		lichSu.setNoiDung(noiDung);
		lichSu.setThoiGian(LocalDateTime.now());
		lichSuRepo.save(lichSu);

	}

	// Lấy danh sách lịch sử hoạt động
	@Override
	public List<LichSuHoatDong> findAllLichSuHoatDong() {
		// TODO Auto-generated method stub
		return lichSuRepo.findAllByOrderByThoiGianDesc();
	}

	// Xoá tất cả lịch sử hoạt động
	@Override
	@Transactional
	public void deleteAll(String nguoiThucHien) {
		// Ghi lại hành động xóa tất cả
		LichSuHoatDong lichSu = new LichSuHoatDong();
		lichSu.setNguoiThucHien(nguoiThucHien);
		lichSu.setLoaiHoatDong("Xoá");
		lichSu.setDoiTuong("Lịch Sử");
		lichSu.setNoiDung("Xóa tất cả lịch sử hoạt động");
		lichSu.setThoiGian(LocalDateTime.now());

		// Xóa tất cả record cũ
		lichSuRepo.deleteAll();

		// Lưu record về việc xóa tất cả
		lichSuRepo.save(lichSu);
	}

}
