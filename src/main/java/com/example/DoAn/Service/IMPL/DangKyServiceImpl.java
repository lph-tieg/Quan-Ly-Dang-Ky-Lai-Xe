package com.example.DoAn.Service.IMPL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.DanhSachDangKy;
import com.example.DoAn.Repository.DangKyRepository;
import com.example.DoAn.Service.DangKyService;
import com.example.DoAn.Service.LichSuHoatDongService;

@Service
public class DangKyServiceImpl implements DangKyService {

	@Autowired
	private DangKyRepository hocVienDKRepository;

	@Autowired
	private LichSuHoatDongService lichSuService;

	// Tìm học viên đăng ký theo tên không phân biệt chữ hoa chữ thường.
	@Override
	public Page<DanhSachDangKy> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findByHoTenContainingIgnoreCase(hoTen, pageable);
	}

	// Tìm học viên đăng ký theo email không phân biệt chữ hoa chữ thường.
	@Override
	public Page<DanhSachDangKy> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findByEmailContainingIgnoreCase(email, pageable);
	}

	// Lấy danh sách học viên đăng ký khi phân trang
	@Override
	public Page<DanhSachDangKy> findAllListDS(Integer pageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return hocVienDKRepository.findAll(pageable);
	}

	// Lấy danh sách học viên đăng ký
	@Override
	public List<DanhSachDangKy> findAllDanhSach() {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findAll();
	}

	// Tìm học viên đăng ký theo ID
	@Override
	public DanhSachDangKy findById(Integer hocVienDkId) {
		try {
			return hocVienDKRepository.findById(hocVienDkId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên đăng ký với ID: " + hocVienDkId));
		} catch (Exception e) {
			System.out.println("Lỗi khi tìm học viên đăng ký: " + e.getMessage());
			return null;
		}
	}

	// Thêm học viên
	@Override
	public DanhSachDangKy addHocVienDK(DanhSachDangKy dangKy) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.save(dangKy);

	}

	// Xoá học viên
	@Override
	public void deleteHocVienDK(Integer hocVienDkId) {
		// TODO Auto-generated method stub
		DanhSachDangKy hocVienDK = hocVienDKRepository.findById(hocVienDkId).orElse(null);

		if (hocVienDK != null)
			hocVienDKRepository.deleteById(hocVienDkId);

		;

	}

	// Tìm học viên theo hạng đăng ký và giảng viên mà học viên chọn
	@Override
	public List<DanhSachDangKy> findByHangDKAndGVChinh(String hangDK, String GVChinh) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findByHangDKAndGVChinh(hangDK, GVChinh);
	}

	// Tìm học viên theo sdt
	@Override
	public List<DanhSachDangKy> findBySdtContaining(String sdt) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findBySdtContaining(sdt);
	}

	// Tìm học viên theo email
	@Override
	public List<DanhSachDangKy> findByEmailContaining(String email) {
		// TODO Auto-generated method stub
		return hocVienDKRepository.findByEmailContaining(email);
	}

	@Override
	public void deleteDK(Integer hocVienDkId, String nguoiThucHien) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		DanhSachDangKy hocVienDK = hocVienDKRepository.findById(hocVienDkId).orElse(null);
		String noiDung = "Xoá học viên đăng ký";
		if (hocVienDK != null)
			hocVienDKRepository.deleteById(hocVienDkId);
		lichSuService.themLichSu(nguoiThucHien, "Xoá", "Người Đăng Ký", noiDung);
		;
	}

}
