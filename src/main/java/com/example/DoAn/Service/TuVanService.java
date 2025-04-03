package com.example.DoAn.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.HocVienTuVan;

public interface TuVanService {

	// Lưu lại người dùng khi đăng ký tư vấn vào database
	public void addTuVan(HocVienTuVan hocVienTuVan);

	// Xoá người dùng đăng ký tư vấn
	public void deleteTuVan(Integer tuVanID);

	// Tìm người dùng đăng ký tư vấn theo ID
	public HocVienTuVan findByIdTuVan(Integer tuVanID);

	// Trả về danh người dùng sách đăng ký tư vấn cho phân trang
	public Page<HocVienTuVan> findAllTuVan(Integer pageable);

//	public List<HocVienTuVan> findByNameContainingIgnoreCase(String name);
//
//	public List<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt);
//
//	public List<HocVienTuVan> findByEmailContainingIgnoreCase(String email);

	// Tìm người dùng đăng ký tư vấn theo tên không phân biệt chữ hoa, thường cho
	// phân trang
	public Page<HocVienTuVan> findByNameContainingIgnoreCase(String name, Pageable pageable);

	// Tìm người dùng đăng ký tư vấn theo sdt không phân biệt chữ hoa, thường cho
	// phân trang
	public Page<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt, Pageable pageable);

	// Tìm người dùng đăng ký tư vấn theo email không phân biệt chữ hoa, thường cho
	// phân trang
	public Page<HocVienTuVan> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	// Tìm người dùng đăng ký tư vấn theo ID
	Optional<HocVienTuVan> findByID(Integer tuVanID);

	// Kiểm tra email đã tồn tại hay chưa
	boolean isEmailExists(String email);

	// Kiểm tra sdt đã tồn tại hay chưa
	boolean isPhoneExists(String phone);

	// Kiểm tra email đã tồn tại hay chưa trong trang người dùng
	boolean existsByEmail(String email);

	// Kiểm tra dts đã tồn tại hay chưa trong trang người dùng
	boolean existsBySdt(String sdt);

	// Trả về danh sách hạng không bị trùng lặp
	List<String> findDistinctHangDK();

	// Trả về danh sách học viên tư vấn
	public List<HocVienTuVan> findAllTuVan();
}
