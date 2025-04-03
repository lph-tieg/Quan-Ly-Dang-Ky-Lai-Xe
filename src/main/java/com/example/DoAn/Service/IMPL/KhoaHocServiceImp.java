package com.example.DoAn.Service.IMPL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.KhoaHoc;
import com.example.DoAn.Repository.KhoaHocRepository;
import com.example.DoAn.Service.KhoaHocService;

@Service
public class KhoaHocServiceImp implements KhoaHocService {

	@Autowired
	private KhoaHocRepository khoaHocRepo;

	// Lấy danh sách khoá học
	@Override
	public List<KhoaHoc> findAllKhoaHoc() {
		// TODO Auto-generated method stub
		return khoaHocRepo.findAll();
	}

	// Xoá khoá học
	@Override
	public void deleteKhoaHoc(Integer khoaHocID) {
		// TODO Auto-generated method stub
		KhoaHoc khoaHoc = khoaHocRepo.findById(khoaHocID).orElse(null);
		if (khoaHoc != null) {
			khoaHocRepo.delete(khoaHoc);
			;
		}
	}

	// Cập nhật khoá học
	@Override
	public KhoaHoc updateKhoaHoc(KhoaHoc khoaHoc) {
		// TODO Auto-generated method stub

		return khoaHocRepo.save(khoaHoc);
	}

	// Tìm khoá học theo ID
	@Override
	public KhoaHoc findByIDKhoaHoc(Integer khoaHocID) {
		// TODO Auto-generated method stub
		return khoaHocRepo.findById(khoaHocID).get();
	}

	// Thêm khoá học
	@Override
	public KhoaHoc createKhoaHoc(KhoaHoc khoaHoc) {

		return khoaHocRepo.save(khoaHoc);

	}

	// Tìm khoá học theo tên không phân biệt chữ hoa chữ thường.
	@Override
	public Page<KhoaHoc> findByTenKhoaHocContainingIgnoreCase(String tenKhoaHoc, Pageable pageable) {
		// TODO Auto-generated method stub
		return khoaHocRepo.findByTenKhoaHocContainingIgnoreCase(tenKhoaHoc, pageable);
	}

	// Lấy danh sách khoá học khi phân trang
	@Override
	public Page<KhoaHoc> findAllKhoaHocList(Integer pageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return khoaHocRepo.findAll(pageable);
	}

	// Tìm khoá học theo loại bằng lái
	@Override
	public List<KhoaHoc> findAllLoaiBangLai(String loaiBangLai) {
		// TODO Auto-generated method stub
		return khoaHocRepo.findAll();
	}

	// Tìm khoá học theo tên
	@Override
	public List<KhoaHoc> findByTenKhoaHoc(String tenKhoaHoc) {
		// TODO Auto-generated method stub
		return khoaHocRepo.findByTenKhoaHoc(tenKhoaHoc);
	}

}
