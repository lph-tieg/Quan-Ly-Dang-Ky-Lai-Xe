package com.example.DoAn.Service.IMPL;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.LopHoc;
import com.example.DoAn.Repository.HocVienRepository;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.LichSuHoatDongService;

@Service
@Transactional
public class HocVienServiceImp implements HocVienService {

	@Autowired
	private HocVienRepository hocVienRepo;

	@Autowired
	private LichSuHoatDongService lichSuService;

	// Xoá học viên
	@Override
	public void deleteHocVien(Integer hocVienID) {
		try {
			HocVien hocVien = hocVienRepo.findById(hocVienID)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + hocVienID));
			hocVienRepo.delete(hocVien);
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi xóa học viên: " + e.getMessage());
		}
	}

	// Tìm học viên theo ID
	@Override
	public HocVien findByID(Integer hocVienID) {
		return hocVienRepo.findById(hocVienID)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + hocVienID));
	}

	/**
	 * Xoá học viên Lưu lịch sử hoạt động
	 */
	@Override
	public void deleteHocVienByID(Integer hocVienID, String nguoiThucHien) {
		try {
			HocVien hocVien = hocVienRepo.findById(hocVienID)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + hocVienID));

			// Lưu thông tin học viên trước khi xóa để ghi log
			StringBuilder noiDung = new StringBuilder();
			noiDung.append(String.format("Xóa học viên: %s - Email: %s", hocVien.getHoTen(), hocVien.getEmail()));

			// Xóa học viên khỏi tất cả các lớp và cập nhật số lượng
			List<LopHoc> lopHocs = new ArrayList<>(hocVien.getLopHocs());
			for (LopHoc lopHoc : lopHocs) {
				hocVien.removeLopHoc(lopHoc);
				noiDung.append("\nXóa khỏi lớp: ").append(lopHoc.getTenLop());
			}

			// Xóa học viên
			hocVienRepo.deleteById(hocVienID);

			// Ghi log sau khi xóa thành công
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Học Viên", noiDung.toString());

		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi xóa học viên: " + e.getMessage());
		}
	}

	/**
	 * Cập nhật học viên Lưu lịch sử hoạt động
	 */
	@Override
	public HocVien updateHocVien(HocVien hocVien, String nguoiThucHien) {
		try {
			// Kiểm tra học viên tồn tại
			HocVien existingHocVien = hocVienRepo.findById(hocVien.getHocVienID())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên"));

			// Cập nhật thông tin cơ bản
			existingHocVien.setHoTen(hocVien.getHoTen());
			existingHocVien.setEmail(hocVien.getEmail());
			existingHocVien.setSdt(hocVien.getSdt());
			existingHocVien.setDiaChi(hocVien.getDiaChi());
			existingHocVien.setHangDK(hocVien.getHangDK());
			existingHocVien.setBuoiHoc(hocVien.getBuoiHoc());
			existingHocVien.setLichHoc(hocVien.getLichHoc());
			existingHocVien.setLoaiXeDK(hocVien.getLoaiXeDK());
			existingHocVien.setLoaiThucHanh(hocVien.getLoaiThucHanh());
			existingHocVien.setGiangVienChinh(hocVien.getGiangVienChinh());
			existingHocVien.setGhiChu(hocVien.getGhiChu());

			// Lưu thay đổi
			return hocVienRepo.save(existingHocVien);
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi cập nhật học viên: " + e.getMessage());
		}
	}

	/**
	 * Thêm học viên mới (từ danh sách đăng ký khi phân lớp) Lưu lại lịch sử hoạt
	 * động
	 */
	@Override
	public HocVien createHocVien(HocVien hocVien, String nguoiThucHien) {
		try {
			if (hocVien.getHocVienID() != null) {
				throw new RuntimeException("ID học viên phải để trống khi tạo mới");
			}

			// Validate required fields
			if (hocVien.getHoTen() == null || hocVien.getHoTen().trim().isEmpty()) {
				throw new RuntimeException("Họ tên học viên không được để trống");
			}
			if (hocVien.getSdt() == null || hocVien.getSdt().trim().isEmpty()) {
				throw new RuntimeException("Số điện thoại không được để trống");
			}
			if (hocVien.getEmail() == null || hocVien.getEmail().trim().isEmpty()) {
				throw new RuntimeException("Email không được để trống");
			}
			if (hocVien.getLopHocs() == null || hocVien.getLopHocs().isEmpty()) {
				throw new RuntimeException("Lớp học không được để trống");
			}
			if (hocVien.getGiangVienChinh() == null) {
				throw new RuntimeException("Giảng viên chính không được để trống");
			}

			HocVien savedHocVien = hocVienRepo.save(hocVien);

			if (savedHocVien == null || savedHocVien.getHocVienID() == null) {
				throw new RuntimeException("Không thể lưu thông tin học viên");
			}

			// Ghi log sau khi tạo thành công
			StringBuilder lopHocInfo = new StringBuilder();
			for (LopHoc lopHoc : savedHocVien.getLopHocs()) {
				if (lopHocInfo.length() > 0) {
					lopHocInfo.append(", ");
				}
				lopHocInfo.append(lopHoc.getTenLop());
			}

			String noiDung = String.format("Thêm học viên mới: %s - Email: %s - SĐT: %s - Hạng: %s - Lớp: %s",
					savedHocVien.getHoTen(), savedHocVien.getEmail(), savedHocVien.getSdt(), savedHocVien.getHangDK(),
					lopHocInfo.toString());

			lichSuService.themLichSu(nguoiThucHien, "Thêm Mới", "Học Viên", noiDung);

			return savedHocVien;

		} catch (Exception e) {
			System.out.println("Lỗi khi tạo học viên mới: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Lỗi khi tạo học viên mới: " + e.getMessage());
		}
	}

	// Tìm học viên theo email không phân biệt chữ hoa chữ thường.
	@Override
	public Page<HocVien> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByEmailContainingIgnoreCase(email, pageable);
	}

	// Tìm học viên theo hạng học viên đăng ký không phân biệt chữ hoa chữ thường.
	@Override
	public Page<HocVien> findByHangDKContainingIgnoreCase(String hangDK, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByHangDKContainingIgnoreCase(hangDK, pageable);
	}

	// Tìm học viên theo loại xe không phân biệt chữ hoa chữ thường.
	@Override
	public Page<HocVien> findByLoaiXeDKContainingIgnoreCase(String loaiXeDK, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByLoaiXeDKContainingIgnoreCase(loaiXeDK, pageable);
	}

	// Tìm học viên theo họ tên không phân biệt chữ hoa chữ thường.
	@Override
	public Page<HocVien> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByHoTenContainingIgnoreCase(hoTen, pageable);
	}

	// Tìm học viên theo sdt gần giống
	@Override
	public Page<HocVien> findBySdtContaining(String sdt, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findBySdtContaining(sdt, pageable);
	}

	// Lấy danh sách tất cả học viên khi phân trang
	@Override
	public Page<HocVien> findAllHocVien(Integer pageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return hocVienRepo.findAll(pageable);
	}

	// Lấy danh sách tất cả học viên
	@Override
	public List<HocVien> findAllHocVienList() {
		// TODO Auto-generated method stub
		return hocVienRepo.findAll();
	}

	// Cập nhật giảng viên của học viên
	@Override
	public void updateGiangVienChinh(GiangVien giangVien) {
		// TODO Auto-generated method stub
		hocVienRepo.updateGiangVienChinh(giangVien);

	}

	// Tìm học viên theo ID lớp học
	@Override
	public List<HocVien> findByLopHocID(Integer lopHocID) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByLopHocID(lopHocID);
	}

	// Tìm học viên theo lớp học
	@Override
	public List<HocVien> findByLopHoc(LopHoc lopHoc) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByLopHoc(lopHoc);
	}

	// Tìm học viên theo hạng đăng ký khi phân trang
	@Override
	public Page<HocVien> findByHangDK(String hang, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByHangDK(hang, pageable);
	}

	// Tìm học viên theo buổi học khi phân trang
	@Override
	public Page<HocVien> findByBuoiHoc(String buoiHoc, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByBuoiHoc(buoiHoc, pageable);
	}

	// Tìm học viên theo lịch học khi phân trang
	@Override
	public Page<HocVien> findByLichHoc(String lichHoc, Pageable pageable) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByLichHoc(lichHoc, pageable);
	}

	// Tìm lịch học duy nhất từ học viên, lọc ra các giá trị null và sắp xếp theo
	// thứ tự tăng dần (repository)
	@Override
	public List<String> findDistinctLichHoc() {
		// TODO Auto-generated method stub
		return hocVienRepo.findDistinctLichHoc();
	}

	// Tìm học viên theo email
	@Override
	public boolean existsByEmail(String email) {
		return hocVienRepo.existsByEmail(email);
	}

	// Tìm học viên theo sdt
	@Override
	public boolean existsBySdt(String sdt) {
		return hocVienRepo.existsBySdt(sdt);
	}

	// Tìm học viên theo email và ID của học viên
	@Override
	public boolean existsByEmailAndHocVienIDNot(String email, Integer hocVienID) {
		return hocVienRepo.existsByEmailAndHocVienIDNot(email, hocVienID);
	}

	// Tìm học viên theo sdt và ID của học viên
	@Override
	public boolean existsBySdtAndHocVienIDNot(String sdt, Integer hocVienID) {
		return hocVienRepo.existsBySdtAndHocVienIDNot(sdt, hocVienID);
	}

	// Tìm danh sách học viên theo ID lớp học
	@Override
	public List<HocVien> findHocVienByLopHoc(Integer lopHocID) {
		try {
			// Find all students by class ID
			List<HocVien> listHocVien = hocVienRepo.findByLopHocID(lopHocID);

			// If no students found, return empty list
			if (listHocVien == null) {
				return new ArrayList<>();
			}

			return listHocVien;
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi lấy danh sách học viên theo lớp học: " + e.getMessage());
		}
	}

	@Override
	public List<HocVien> findByHoTenContainingIgnoreCase(String keyword) {
		// TODO Auto-generated method stub
		return hocVienRepo.findByHoTenContainingIgnoreCase(keyword);
	}

	@Override
	public List<HocVien> findAllHocVien() {
		try {
			List<HocVien> listHocVien = hocVienRepo.findAll();
			if (listHocVien == null) {
				return new ArrayList<>();
			}
			return listHocVien;
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi lấy danh sách học viên: " + e.getMessage());
		}
	}

	@Override
	public List<HocVien> findHocVienNotInLopHoc(Integer lopHocID) {
		try {
			List<HocVien> listHocVien = hocVienRepo.findHocVienNotInLopHoc(lopHocID);
			if (listHocVien == null) {
				return new ArrayList<>();
			}
			return listHocVien;
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi lấy danh sách học viên chưa có trong lớp: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updateThoiGian(Integer hocVienID, Double thoiGianDaHoc, String nguoiThucHien) {
		HocVien hocVien = hocVienRepo.findById(hocVienID)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên"));

		// Cập nhật thời gian đã học
		hocVien.setThoiGianDaHoc(thoiGianDaHoc);

		// Cập nhật trạng thái nếu cần
		if (thoiGianDaHoc >= hocVien.getThoiGianHoc()) {
			hocVien.setTrangThai("Đã hoàn thành");
		} else {
			hocVien.setTrangThai("Đang học");
		}

		// Lưu vào cơ sở dữ liệu
		hocVienRepo.save(hocVien);
		String noiDung = "Thời gian đã học: " + thoiGianDaHoc + " giờ";
		// Ghi log
		lichSuService.themLichSu(nguoiThucHien, "Cập nhật thời gian học", "Học Viên", noiDung);
	}

}
