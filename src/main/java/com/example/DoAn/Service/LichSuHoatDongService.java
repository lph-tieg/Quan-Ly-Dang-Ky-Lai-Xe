package com.example.DoAn.Service;

import java.util.List;

import com.example.DoAn.Model.LichSuHoatDong;

public interface LichSuHoatDongService {

	// Lưu lại lịch sử hoạt động
	public void themLichSu(String nguoiThucHien, String loaiHoatDong, String doiTuong, String noiDung);

	// Trả về danh sách các lịch sử hoạt động
	List<LichSuHoatDong> findAllLichSuHoatDong();

	// Xoá tất cả lịch sử hoạt động
	public void deleteAll(String nguoiThucHien);
}
