package com.example.DoAn.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.GiangVien;

public interface GiangVienService {

	// Lấy danh sách giảng viên
	public List<GiangVien> findAllGiangVien();

	// Xoá giảng viên và lưu lịch sử hoạt động
	public void deleteGiangVien(Integer giangVienID, String nguoiThucHien);

	// Tìm giảng viên theo ID
	public GiangVien findByIDGV(Integer giangVienID);

	// Cập nhật giảng viên và lưu lịch sử hoạt động
	public void updateGiangVien(GiangVien giangVien, String nguoiThucHien);

	// Thêm giảng viên, gán giảng viên vào lớp học và lưu lịch sử hoạt động
	public void createGiangVien(GiangVien giangVien, Integer lopHocID, String nguoiThucHien);

	// Tìm giảng viên theo sdt
	public List<GiangVien> findBySdtGV(String sdt);

	// Tìm giảng viên theo họ tên
	public GiangVien findByHoTenGV(String hoTenGV);

	// Tìm giảng viên theo email
	public List<GiangVien> findByEmailGV(String emailGV);

	// Tìm giảng viên theo tên không phân biệt chữ hoa, chữ thường cho phân trang
	public Page<GiangVien> findByHoTenGVContainingIgnoreCase(String hoTenGV, Pageable pageable);

	// Tìm giảng viên theo email không phân biệt chữ hoa, chữ thường cho phân trang
	public Page<GiangVien> findByEmailGVContainingIgnoreCase(String email, Pageable pageable);

	// Tìm giảng viên theo sdt gần giống cho phân trang
	public Page<GiangVien> findBySdtGVContaining(String sdt, Pageable pageable);

	// Lấy danh sách giảng viên cho phân trang
	public Page<GiangVien> findALlListGiangVien(Integer pageable);

	// Tìm giảng viên theo lịch dạy
	public List<GiangVien> findByLichDay(String lichDay);

	// Tìm giảng viên theo buổi dạy
	public List<GiangVien> findByBuoiDay(String buoiDay);

	// Tìm giảng viên theo lịch dạy và buổi dạy
	public List<GiangVien> findByLichDayAndBuoiDay(String lichDay, String buoiDay);

	// Lọc giảng viên theo lịch dạy, buổi dạy, hạng
	List<GiangVien> findByGiangVienFilter(String lichDay, String buoiDay, Integer hangID);

	// Tìm giảng viên theo ID
	Optional<GiangVien> findByID(Integer giangVienID);

	// Lấy danh sách giảng viên cho phân trang
	Page<GiangVien> getAllGiangVien(int pageNo);

	// Lấy danh sách giảng viên dựa trên danh sách ID
	List<GiangVien> findAllByID(List<Integer> giangVienID);

	// Tìm giảng viên theo lịch học, buổi học, hạng
	List<GiangVien> findGiangVienByLichHocBuoiHocHang(String lichHoc, String buoiHoc, String hang);

	// Kiểm tra email đã tồn tại hay chưa
	boolean isEmailExists(String emailGV);

	// Kiểm tra sdt đã tồn tại hay chưa
	boolean isPhoneExists(String sdtGV);

	// Kiểm tra email có thuộc về một giảng viên khác không.
	boolean isEmailExistsForOtherTeacher(String email, Integer teacherId);

	// Kiểm tra sdt có thuộc về một giảng viên khác không.
	boolean isPhoneExistsForOtherTeacher(String phone, Integer teacherId);

}
