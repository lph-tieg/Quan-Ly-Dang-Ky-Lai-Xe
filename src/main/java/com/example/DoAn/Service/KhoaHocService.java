package com.example.DoAn.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.DoAn.Model.KhoaHoc;

public interface KhoaHocService {

	// Trả về danh sách khoá học
	public List<KhoaHoc> findAllKhoaHoc();

	// Tìm danh sách khoá học theo loại bằng lái
	public List<KhoaHoc> findAllLoaiBangLai(String loaiBangLai);

	// Xoá khoá học
	public void deleteKhoaHoc(Integer khoaHocID);

	// Cập nhật khoá học
	public KhoaHoc updateKhoaHoc(KhoaHoc khoaHoc);

	// Tìm khoá học theo ID
	public KhoaHoc findByIDKhoaHoc(Integer khoaHocID);

	// Thêm khoá học
	public KhoaHoc createKhoaHoc(KhoaHoc khoaHoc);

	// Tìm khoá học theo tên không phân biệt chữ hoa, thường cho phân trang
	public Page<KhoaHoc> findByTenKhoaHocContainingIgnoreCase(String tenKhoaHoc, Pageable pageable);

	// Trả về danh sách khoá học cho phân trang
	public Page<KhoaHoc> findAllKhoaHocList(Integer pageNo);

	// Tìm danh sách khoá học theo tên khoá học
	public List<KhoaHoc> findByTenKhoaHoc(String tenKhoaHoc);

}
