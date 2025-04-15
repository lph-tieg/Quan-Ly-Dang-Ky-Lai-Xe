package com.example.DoAn.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.LopHoc;

public interface HocVienService {

	// Trả về danh sách học viên trong phân trang
	public Page<HocVien> findAllHocVien(Integer pageable);

	// Trả về danh sách học viên
	public List<HocVien> findAllHocVienList();

	// Xoá học viên
	public void deleteHocVien(Integer hocVienID);

	// Tìm học viên theo ID
	public HocVien findByID(Integer hocVienID);

	// Xoá học viên, lưu lại lịch sử hoạt động
	public void deleteHocVienByID(Integer hocVienID, String nguoiThucHien);

	// Câp nhật học viên, lưu lại lịch sử hoạt động
	public HocVien updateHocVien(HocVien hocVien, String nguoiThucHien);

	// Thêm học viên, lưu lại lịch sử hoạt động
	public HocVien createHocVien(HocVien hocVien, String nguoiThucHien);

//	public List<HocVien> findByHoTenContainingIgnoreCase(String hoTen);
//
//	public List<HocVien> findBySDTContaining(String sdt);
//
//	public List<HocVien> findByEmailContainingIgnoreCase(String email);
//
//	public List<HocVien> findByHangDKContainingIgnoreCase(String hangDK);
//
//	public List<HocVien> findByLoaiXeDKContainingIgnoreCase(String loaiXeDK);

	// Tìm học viên theo ID lớp học
	List<HocVien> findByLopHocID(Integer lopHocID);

	// Tìm học viên theo lớp học cụ thể
	List<HocVien> findByLopHoc(LopHoc lopHoc);

	// Tìm học viên theo hạng cho phân trang
	Page<HocVien> findByHangDK(String hang, Pageable pageable);

	// Tìm học viên theo buổi học cho phân trang
	Page<HocVien> findByBuoiHoc(String buoiHoc, Pageable pageable);

	// Tìm học viên theo lịch học cho phân trang
	Page<HocVien> findByLichHoc(String lichHoc, Pageable pageable);

	// Lấy danh sách lịch học không trùng lặp
	List<String> findDistinctLichHoc();

	// Tìm học viên theo email không phân biệt chữ hoa, thường cho phân trang
	public Page<HocVien> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Tìm học viên theo hạng không phân biệt chữ hoa, thường cho phân trang
	public Page<HocVien> findByHangDKContainingIgnoreCase(String hangDK, Pageable pageable);

	// Tìm học viên theo loại xe đăng ký không phân biệt chữ hoa, thường cho phân
	// trang
	public Page<HocVien> findByLoaiXeDKContainingIgnoreCase(String loaiXeDK, Pageable pageable);

	// Tìm học viên theo họ tên không phân biệt chữ hoa, thường cho phân trang
	public Page<HocVien> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable);

	// Tìm học viên theo sdt gần giống cho phân trang
	public Page<HocVien> findBySdtContaining(String sdt, Pageable pageable);

	// Cập nhật giảng viên dạy cho học viên
	public void updateGiangVienChinh(GiangVien giangVien);

	// Xác nhận email đã tồn tại hay chưa
	boolean existsByEmail(String email);

	// Xác nhận sdt đã tồn tại hay chưa
	boolean existsBySdt(String sdt);

	// Tìm email đã tồn tại hay chưa nhưng loại trừ các học viên có ID
	boolean existsByEmailAndHocVienIDNot(String email, Integer hocVienID);

	// Tìm sdt đã tồn tại hay chưa nhưng loại trừ các học viên có ID
	boolean existsBySdtAndHocVienIDNot(String sdt, Integer hocVienID);

	// Trả về danh sách học viên theo ID lớp học
	List<HocVien> findHocVienByLopHoc(Integer lopHocID);

	// Tìm học viên theo tên không phân biệt chữ hoa, thường
	public List<HocVien> findByHoTenContainingIgnoreCase(String keyword);

	List<HocVien> findAllHocVien();

	// Tìm học viên chưa có trong lớp học
	List<HocVien> findHocVienNotInLopHoc(Integer lopHocID);

	public void updateThoiGian(Integer hocVienID, Double thoiGianDaHoc, String nguoiThucHien);
}
