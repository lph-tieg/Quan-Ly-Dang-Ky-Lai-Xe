package com.example.DoAn.Service.IMPL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.LopHoc;
import com.example.DoAn.Repository.GiangVienRepository;
import com.example.DoAn.Repository.HocVienRepository;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.LichSuHoatDongService;
import com.example.DoAn.Service.LopHocService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class GiangVienServiceImp implements GiangVienService {

	@Autowired
	private GiangVienRepository giangVienRepo;

	@Autowired
	@Lazy
	private LopHocService lopHocService;

	@Autowired
	private HocVienRepository hocVienRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private LichSuHoatDongService lichSuService;

	// Lấy danh sách các giảng viên
	@Override
	public List<GiangVien> findAllGiangVien() {
		// TODO Auto-generated method stub
		return giangVienRepo.findAll();
	}

	/**
	 * Xoá giảng viên Khi xoá giảng viên sẽ xoá luôn giảng viên trong lớp học (nếu
	 * được phân lớp) và lớp học sẽ đưuọc cập nhật Lưu lại lịch sử hoạt động
	 */
	@Override
	@Transactional
	public void deleteGiangVien(Integer giangVienID, String nguoiThucHien) {
		GiangVien giangVien = giangVienRepo.findById(giangVienID).orElse(null);
		if (giangVien != null) {
			// 1. Xóa giảng viên khỏi các lớp học trước
			List<LopHoc> lopHocs = giangVien.getLopHocs();
			if (lopHocs != null && !lopHocs.isEmpty()) {
				for (LopHoc lopHoc : lopHocs) {

					String tenGiangVien = giangVien.getHoTenGV();
					String tenLop = lopHoc.getTenLop();

					lopHoc.getListGiangVien().remove(giangVien);
					lopHocService.updateLopHoc(lopHoc, lopHoc.getListGiangVien().stream().map(gv -> gv.getGiangVienID())
							.collect(Collectors.toList()), nguoiThucHien);

					String noiDungLopHoc = String.format("Xoá giảng viên %s khỏi lớp %s", tenGiangVien, tenLop);
					lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Lớp Học", noiDungLopHoc);
				}
			}

			// 2. Set giảng viên chính của học viên về null
			hocVienRepository.updateGiangVienChinh(giangVien);

			// 3. Xóa giảng viên
			giangVienRepo.deleteById(giangVienID);

			String noiDung = "Xoá Giảng Viên " + giangVien.getHoTenGV();
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Giảng Viên", noiDung);
		}
	}

	// Tìm giảng viên theo ID
	@Override
	public GiangVien findByIDGV(Integer giangVienID) {
		// TODO Auto-generated method stub
		return giangVienRepo.findById(giangVienID).get();
	}

	/**
	 * Cập nhật giảng viên Lưu lại lịch sử hoạt động
	 */
	@Override
	@Transactional
	public void updateGiangVien(GiangVien giangVien, String nguoiThucHien) {
		// Lấy thông tin giảng viên cũ trước khi cập nhật
		GiangVien oldGiangVien = giangVienRepo.findById(giangVien.getGiangVienID()).orElse(null);
		if (oldGiangVien != null) {
			StringBuilder changes = new StringBuilder();

			// So sánh và ghi nhận các thay đổi
			if (!oldGiangVien.getHoTenGV().equals(giangVien.getHoTenGV())) {
				changes.append("Họ tên: ").append(oldGiangVien.getHoTenGV()).append(" → ")
						.append(giangVien.getHoTenGV()).append("; ");
			}

			if (oldGiangVien.getHang() != null && giangVien.getHang() != null
					&& !oldGiangVien.getHang().getTenHang().equals(giangVien.getHang().getTenHang())) {
				changes.append("Hạng: ").append(oldGiangVien.getHang().getTenHang()).append(" → ")
						.append(giangVien.getHang().getTenHang()).append("; ");
			}

			if (!oldGiangVien.getSdtGV().equals(giangVien.getSdtGV())) {
				changes.append("Số điện thoại: ").append(oldGiangVien.getSdtGV()).append(" → ")
						.append(giangVien.getSdtGV()).append("; ");
			}

			if (!oldGiangVien.getEmailGV().equals(giangVien.getEmailGV())) {
				changes.append("Email: ").append(oldGiangVien.getEmailGV()).append(" → ").append(giangVien.getEmailGV())
						.append("; ");
			}

			if (!oldGiangVien.getBuoiDay().equals(giangVien.getBuoiDay())) {
				changes.append("Buổi dạy: ").append(oldGiangVien.getBuoiDay()).append(" → ")
						.append(giangVien.getBuoiDay()).append("; ");
			}

			if (!oldGiangVien.getLichDay().equals(giangVien.getLichDay())) {
				changes.append("Lịch dạy: ").append(oldGiangVien.getLichDay()).append(" → ")
						.append(giangVien.getLichDay()).append("; ");
			}

			// Lưu giảng viên mới
			giangVienRepo.save(giangVien);
			entityManager.flush();
			entityManager.detach(giangVien);

			// Tạo nội dung chi tiết cho lịch sử
			String noiDung;
			if (changes.length() > 0) {
				noiDung = "Cập nhật giảng viên " + giangVien.getHoTenGV() + ": " + changes.toString().trim();
			} else {
				noiDung = "Cập nhật giảng viên " + giangVien.getHoTenGV() + " (không có thay đổi thông tin)";
			}

			// Ghi lịch sử với thời gian hiện tại
			lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Giảng Viên", noiDung);
		}
	}

	/**
	 * Thêm giảng viên mới Lưu lại lịch sử hoạt động
	 */
	@Override
	@Transactional
	public void createGiangVien(GiangVien giangVien, Integer lopHocID, String nguoiThucHien) {
		// Lưu GiangVien trước
		giangVien = giangVienRepo.save(giangVien);
		String noiDung = "Thêm mới giảng viên: " + giangVien.getHoTenGV();
		if (lopHocID != null) {
			noiDung += "Vào lớp " + lopHocID;
		}
		lichSuService.themLichSu(nguoiThucHien, "Thêm Mới", "Giảng Viên", noiDung);
	}

	// Tìm giảng viên theo sdt
	@Override
	public List<GiangVien> findBySdtGV(String sdt) {
		// TODO Auto-generated method stub
		return giangVienRepo.findBySdtGV(sdt);
	}

	// Tìm giảng viên theo email
	@Override
	public List<GiangVien> findByEmailGV(String emailGV) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByEmailGV(emailGV);
	}

	// Tìm giảng viên theo học tên không phân biệt chữ hoa chữ thường.
	@Override
	public Page<GiangVien> findByHoTenGVContainingIgnoreCase(String hoTenGV, Pageable pageable) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByHoTenGVContainingIgnoreCase(hoTenGV, pageable);
	}

	// Tìm giảng viên theo email không phân biệt chữ hoa chữ thường.
	@Override
	public Page<GiangVien> findByEmailGVContainingIgnoreCase(String email, Pageable pageable) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByEmailGVContainingIgnoreCase(email, pageable);
	}

	// Lấy danh sách các giảng viên khi phân trang
	@Override
	public Page<GiangVien> findALlListGiangVien(Integer PageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(PageNo - 1, 5);
		return giangVienRepo.findAll(pageable);
	}

	// Tìm giảng viên theo sdt
	@Override
	public Page<GiangVien> findBySdtGVContaining(String sdt, Pageable pageable) {
		// TODO Auto-generated method stub
		return giangVienRepo.findBySdtGVContaining(sdt, pageable);
	}

	// Tìm giảng viên theo lịch dạy
	@Override
	public List<GiangVien> findByLichDay(String lichDay) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByLichDay(lichDay);
	}

	// Tìm giảng viên theo buổi dạy
	@Override
	public List<GiangVien> findByBuoiDay(String buoiDay) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByBuoiDay(buoiDay);
	}

	// Tìm giảng viên theo lịch dạy và buổi dạy
	@Override
	public List<GiangVien> findByLichDayAndBuoiDay(String lichDay, String buoiDay) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByLichDayAndBuoiDay(lichDay, buoiDay);
	}

	// Tìm giảng viên theo họ tên không phân biệt chữ hoa chữ thường.
	@Override
	public GiangVien findByHoTenGV(String hoTenGV) {
		// TODO Auto-generated method stub

		try {
			List<GiangVien> giangViens = giangVienRepo.findByHoTenGVContainingIgnoreCase(hoTenGV);
			if (giangViens.isEmpty()) {
				return null;
			}
			// Trả về giảng viên đầu tiên tìm thấy
			return giangViens.get(0);
		} catch (Exception e) {
			System.err.println("Lỗi khi tìm giảng viên theo tên: " + e.getMessage());
			return null;
		}
	}

	// Tìm giảng viên theo lịch dạy, buổi dạy, hạng
	@Override
	public List<GiangVien> findByGiangVienFilter(String lichDay, String buoiDay, Integer hangID) {
		// TODO Auto-generated method stub
		return giangVienRepo.findByLichDayAndBuoiDayAndHang_HangID(lichDay, buoiDay, hangID);
	}

	// Tìm giảng viên theo ID
	@Override
	@Transactional
	public Optional<GiangVien> findByID(Integer giangVienID) {
		// TODO Auto-generated method stub
		return giangVienRepo.findById(giangVienID);
	}

	// Lấy danh sách giảng viên cho phân trang trong lớp
	@Override
	@Transactional
	public Page<GiangVien> getAllGiangVien(int pageNo) {
		// TODO Auto-generated method stub
		return giangVienRepo.findAllWithLopHocs(PageRequest.of(pageNo - 1, 5));
	}

	// Tìm giảng viên theo lịch học, buổi học và hạng trong lớp
	@Override
	@Transactional
	public List<GiangVien> findGiangVienByLichHocBuoiHocHang(String lichHoc, String buoiHoc, String hang) {
		try {
			System.out.println("Searching for GiangVien with criteria:");
			System.out.println("Lich hoc: " + lichHoc);
			System.out.println("Buoi hoc: " + buoiHoc);
			System.out.println("Hang: " + hang);

			if (lichHoc == null || buoiHoc == null || hang == null) {
				return new ArrayList<>();
			}

			// Thực hiện tìm kiếm
			List<GiangVien> result = giangVienRepo.findGiangVienByLichHocBuoiHocHang(lichHoc, buoiHoc, hang);

			System.out.println("Found " + result.size() + " results");
			return result;
		} catch (Exception e) {
			System.err.println("Error in service layer: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// Tìm các giảng viên theo ID
	@Override
	public List<GiangVien> findAllByID(List<Integer> giangVienID) {
		// TODO Auto-generated method stub
		return giangVienRepo.findAllById(giangVienID);
	}

	// Tìm giảng viên theo email trong lớp
	@Override
	public boolean isEmailExists(String emailGV) {
		return giangVienRepo.existsByEmailGV(emailGV);
	}

	// Tìm giảng viên theo sdt trong lớp
	@Override
	public boolean isPhoneExists(String sdtGV) {
		return giangVienRepo.existsBySdtGV(sdtGV);
	}

	// Tìm giảng viên theo email và ID giảng viên
	@Override
	public boolean isEmailExistsForOtherTeacher(String email, Integer teacherId) {
		return giangVienRepo.existsByEmailGVAndGiangVienIDNot(email, teacherId);
	}

	// Tìm giảng viên theo sdt và ID giảng viên
	@Override
	public boolean isPhoneExistsForOtherTeacher(String phone, Integer teacherId) {
		return giangVienRepo.existsBySdtGVAndGiangVienIDNot(phone, teacherId);

	}
}
