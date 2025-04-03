package com.example.DoAn.Service.IMPL;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.HocVienTuVan;
import com.example.DoAn.Repository.TuVanRepository;
import com.example.DoAn.Service.TuVanService;

@Service
public class TuVanServiceImp implements TuVanService {

	@Autowired
	private TuVanRepository tuVanRepo;

	// Thêm tư vấn mới
	@Override
	public void addTuVan(HocVienTuVan hocVienTuVan) {
		// TODO Auto-generated method stub
		tuVanRepo.save(hocVienTuVan);
	}

	// Tìm người dùng theo ID trong tư vấn
	@Override
	public HocVienTuVan findByIdTuVan(Integer tuVanID) {
		// TODO Auto-generated method stub
		return tuVanRepo.findById(tuVanID).get();
	}

	// Xoá tư vấn
	@Override
	public void deleteTuVan(Integer tuVanID) {
		// TODO Auto-generated method stub
		HocVienTuVan tuVan = tuVanRepo.findById(tuVanID).orElse(null);
		if (tuVan != null) {
			tuVanRepo.deleteById(tuVanID);
			;
		}

	}

	// Tìm người dùng theo sdt không phân biệt chữ hoa chữ thường trong tư vấn khi
	// phân trang
	@Override
	public Page<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt, Pageable pageable) {
		// TODO Auto-generated method stub
		return tuVanRepo.findBySdtContainingIgnoreCase(sdt, pageable);
	}

	// Tìm người dùng theo email không phân biệt chữ hoa chữ thường trong tư vấn khi
	// phân trang
	@Override
	public Page<HocVienTuVan> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
		// TODO Auto-generated method stub
		return tuVanRepo.findByEmailContainingIgnoreCase(email, pageable);
	}

	// Tìm người dùng theo tên không phân biệt chữ hoa chữ thường trong tư vấn khi
	// phân trang
	@Override
	public Page<HocVienTuVan> findByNameContainingIgnoreCase(String name, Pageable pageable) {
		// TODO Auto-generated method stub
		return tuVanRepo.findByNameContainingIgnoreCase(name, pageable);
	}

	// Lấy danh sách tư vấn khi phân trang
	@Override
	public Page<HocVienTuVan> findAllTuVan(Integer pageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return tuVanRepo.findAll(pageable);
	}

	// Tìm người dùng tư vấn theo ID (ID duy nhất trong database)
	@Override
	public Optional<HocVienTuVan> findByID(Integer tuVanID) {
		// TODO Auto-generated method stub
		return tuVanRepo.findById(tuVanID);
	}

	// Dùng để xác nhận email đã tồn tại trong database hay chưa
	@Override
	public boolean isEmailExists(String email) {
		// TODO Auto-generated method stub
		return tuVanRepo.existsByEmail(email);
	}

	// Dùng để xác nhận sdt đã tồn tại trong database hay chưa
	@Override
	public boolean isPhoneExists(String phone) {
		// TODO Auto-generated method stub
		return tuVanRepo.existsBySdt(phone);
	}

	// Dùng để xác nhận email đã tồn tại hay chưa ở trang người dùng
	@Override
	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
		return tuVanRepo.existsByEmail(email);
	}

	// Dùng để xác nhận sdt đã tồn tại hay chưa ở trang người dùng
	@Override
	public boolean existsBySdt(String sdt) {
		// TODO Auto-generated method stub
		return tuVanRepo.existsBySdt(sdt);
	}

	// Tìm người dùng đăng ký tư vấn theo hạng
	@Override
	public List<String> findDistinctHangDK() {
		// TODO Auto-generated method stub
		return tuVanRepo.findDistinctHangDK();
	}

	// Lấy danh sách tất cả người dùng đăng ký tư vấn
	@Override
	public List<HocVienTuVan> findAllTuVan() {
		// TODO Auto-generated method stub
		return tuVanRepo.findAll();
	}

//	@Override
//	public List<HocVienTuVan> findByNameContainingIgnoreCase(String name) {
//		// TODO Auto-generated method stub
//		return tuVanRepo.findByNameContainingIgnoreCase(name);
//	}
//
//	@Override
//	public List<HocVienTuVan> findBySdtContainingIgnoreCase(String sdt) {
//		// TODO Auto-generated method stub
//		return tuVanRepo.findBySdtContainingIgnoreCase(sdt);
//	}
//
//	@Override
//	public List<HocVienTuVan> findByEmailContainingIgnoreCase(String email) {
//		// TODO Auto-generated method stub
//		return tuVanRepo.findByEmailContainingIgnoreCase(email);
//	}

}
