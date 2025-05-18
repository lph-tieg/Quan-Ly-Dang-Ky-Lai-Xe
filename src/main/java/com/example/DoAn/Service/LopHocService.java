package com.example.DoAn.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.KhoaHoc;
import com.example.DoAn.Model.LopHoc;

public interface LopHocService {

	// Tìm lớp học theo tên không phân biệt chữ hoa, thường cho phân trang
	public Page<LopHoc> findByTenLopContainingIgnoreCase(String tenLop, Pageable pageable);

	// Tìm lớp học theo hạng
	public List<LopHoc> findByHang(Hang hang);

	// Tìm lớp học theo buổi học
	public List<LopHoc> findByBuoiHoc(String buoiHoc);

	// Tìm lớp học theo lịch học
	public List<LopHoc> findByLichHoc(String lichHoc);

	// Trả về danh sách lớp học cho phân trang
	public Page<LopHoc> findALlLopPage(Integer pageNo);

	// Tìm lớp học theo tên lớp
	Optional<LopHoc> findByTenLop(String tenLop);

	// Trả về danh sách lớp học
	public List<LopHoc> findAllLopHoc();

	// Xoá lớp học, lưu lại lịch sử hoạt động
	public void deleteLop(Integer lopHocID, String nguoiThucHien);

	// Kiểm tra tên lớp đã tồn tại hay chưa
	public boolean existsByTenLop(String tenLop);

	// Tìm lớp học dựa trên ID
	public LopHoc findByLopHocID(Integer lopHocID);

	// Cập nhật lớp học và danh sách giảng viên trong lớp học, lưu lại lịch sử hoạt động
	public void updateLopHoc(LopHoc lopHoc, List<Integer> giangVienMoiID, String nguoiThucHien);

	// Thêm lớp học, lưu lại lịch sử hoạt động
	public LopHoc createLopHoc(LopHoc lopHoc, String nguoiThucHien);

	// Lọc lớp học theo lịch học, buổi học, hạng
	List<LopHoc> findByFilter(String lichHoc, String buoiHoc, Hang hang);

	// Tìm lớp học theo hạng
	Optional<LopHoc> findById(Integer lopHocID);

	// Tìm lớp học theo khoá học
	public LopHoc findByKhoaHoc(KhoaHoc khoaHoc);

	// Tìm lớp học theo hạng cho phân trang
	Page<LopHoc> findByHangHangID(Integer hangID, Pageable pageable);

	// Tìm lớp học theo buổi học cho phân trang
	Page<LopHoc> findByBuoiHoc(String buoiHoc, Pageable pageable);
}