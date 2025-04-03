package com.example.DoAn.Service.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.DoAn.Entity.LopHoc;
import com.example.DoAn.Entity.GiangVien;
import com.example.DoAn.Entity.HocVien;
import com.example.DoAn.Repository.LopHocRepository;
import com.example.DoAn.Service.LopHocService;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.LichSuService;

@Service
public class LopHocServiceImpl implements LopHocService {

	@Autowired
	private LopHocRepository lopHocRepository;

	@Autowired
	private GiangVienService giangVienService;

	@Autowired
	private HocVienService hocVienService;

	@Autowired
	private LichSuService lichSuService;

	@Override
	public LopHoc createLopHoc(LopHoc lopHoc, String nguoiThucHien) {
		try {
			if (lopHoc == null) {
				throw new IllegalArgumentException("Thông tin lớp học không được để trống");
			}
			if (lopHoc.getTenLop() == null || lopHoc.getTenLop().trim().isEmpty()) {
				throw new IllegalArgumentException("Tên lớp không được để trống");
			}
			if (existsByTenLop(lopHoc.getTenLop())) {
				throw new RuntimeException("Tên lớp đã tồn tại");
			}

			System.out.println("Đang tạo lớp học mới: " + lopHoc.getTenLop());
			LopHoc savedLopHoc = lopHocRepository.save(lopHoc);
			
			// Lưu lịch sử hoạt động với thông tin chi tiết hơn
			String noiDung = String.format("Thêm lớp học mới: %s (Lịch học: %s, Buổi học: %s, Hạng: %s, Số lượng: %d)", 
				savedLopHoc.getTenLop(), 
				savedLopHoc.getLichHoc(),
				savedLopHoc.getBuoiHoc(),
				savedLopHoc.getHang().getTenHang(),
				savedLopHoc.getSoLuong());
			
			lichSuService.themLichSu(nguoiThucHien, "Thêm Mới", "Lớp Học", noiDung);
			
			System.out.println("Đã tạo thành công lớp học với ID: " + savedLopHoc.getLopHocID());
			return savedLopHoc;
		} catch (Exception e) {
			System.out.println("Lỗi khi tạo lớp học: " + e.getMessage());
			throw new RuntimeException("Không thể tạo lớp học: " + e.getMessage());
		}
	}

	@Transactional
	@Override
	public void deleteLop(Integer lopHocID, String nguoiThucHien) {
		try {
			// 1. Kiểm tra sự tồn tại của lớp học
			LopHoc lopHoc = lopHocRepository.findById(lopHocID)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ID: " + lopHocID));

			// 2. Xóa các mối quan hệ với giảng viên
			for (GiangVien giangVien : lopHoc.getListGiangVien()) {
				giangVien.getLopHocs().remove(lopHoc);
			}
			lopHoc.getListGiangVien().clear();

			// 3. Cập nhật trạng thái cho học viên trong lớp
			List<HocVien> hocViens = hocVienService.findByLopHoc(lopHoc);
			for (HocVien hocVien : hocViens) {
				hocVien.setLopHoc(null);
				hocVienService.updateHocVien(hocVien, nguoiThucHien);
			}

			String noiDung = String.format("Xoá lớp học: %s (Lịch học: %s, Buổi học: %s, Hạng: %s, Số lượng: %d)",
				lopHoc.getTenLop(),
				lopHoc.getLichHoc(),
				lopHoc.getBuoiHoc(),
				lopHoc.getHang().getTenHang(),
				lopHoc.getSoLuong());

			// 4. Xóa lớp học
			lopHocRepository.delete(lopHoc);

			// Lưu lịch sử hoạt động
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Lớp Học", noiDung);

			System.out.print("Đã xóa thành công lớp học có ID: " + lopHocID);
		} catch (Exception e) {
			System.out.print("Lỗi khi xóa lớp học có ID " + lopHocID + ": " + e.getMessage());
			throw new RuntimeException("Không thể xóa lớp học: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updateLopHoc(LopHoc lopHoc, List<Integer> giangVienMoiID, String nguoiThucHien) {
		try {
			if (lopHoc == null || lopHoc.getLopHocID() == null) {
				throw new IllegalArgumentException("Thông tin lớp học không hợp lệ");
			}

			Optional<LopHoc> lopHocOptional = lopHocRepository.findById(lopHoc.getLopHocID());
			if (lopHocOptional.isEmpty()) {
				throw new RuntimeException("Lớp học không tồn tại");
			}

			LopHoc lopHocHienTai = lopHocOptional.get();

			// Kiểm tra tên lớp mới
			if (!lopHocHienTai.getTenLop().equals(lopHoc.getTenLop()) && existsByTenLop(lopHoc.getTenLop())) {
				throw new RuntimeException("Tên lớp đã tồn tại");
			}

			List<GiangVien> giangVienCu = new ArrayList<>(lopHocHienTai.getListGiangVien());
			List<GiangVien> giangVienMoi = giangVienMoiID != null ? 
				giangVienService.findAllByID(giangVienMoiID) : new ArrayList<>();

			// Tìm giảng viên đã bị xóa
			List<GiangVien> giangVienBiXoa = new ArrayList<>(giangVienCu);
			giangVienBiXoa.removeAll(giangVienMoi);

			// Tìm giảng viên mới được thêm
			List<GiangVien> giangVienDuocThem = new ArrayList<>(giangVienMoi);
			giangVienDuocThem.removeAll(giangVienCu);

			StringBuilder changes = new StringBuilder();

			// Ghi nhận thay đổi về giảng viên
			if (!giangVienBiXoa.isEmpty()) {
				for (GiangVien gv : giangVienBiXoa) {
					changes.append("Lớp ").append(lopHocHienTai.getTenLop())
						.append(" xóa giảng viên ").append(gv.getHoTenGV());
				}
			}

			if (!giangVienDuocThem.isEmpty()) {
				if (changes.length() > 0) {
					changes.append("\n");
				}
				for (GiangVien gv : giangVienDuocThem) {
					changes.append("Lớp ").append(lopHocHienTai.getTenLop())
						.append(" thêm giảng viên ").append(gv.getHoTenGV());
				}
			}

			// Cập nhật thông tin
			lopHocHienTai.setTenLop(lopHoc.getTenLop());
			lopHocHienTai.setLichHoc(lopHoc.getLichHoc());
			lopHocHienTai.setBuoiHoc(lopHoc.getBuoiHoc());
			lopHocHienTai.setHang(lopHoc.getHang());
			lopHocHienTai.setSoLuong(lopHoc.getSoLuong());
			lopHocHienTai.setKhoaHoc(lopHoc.getKhoaHoc());
			lopHocHienTai.setListGiangVien(giangVienMoi);

			// Lưu vào database
			lopHocRepository.save(lopHocHienTai);

			// Tạo nội dung log
			String noiDung;
			if (changes.length() > 0) {
				noiDung = changes.toString().trim();
			} else {
				noiDung = "Cập nhật thông tin lớp " + lopHoc.getTenLop();
			}

			// Lưu lịch sử hoạt động
			lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Lớp Học", noiDung);

			System.out.println("Đã cập nhật thành công lớp học ID: " + lopHoc.getLopHocID());
		} catch (Exception e) {
			System.out.println("Lỗi khi cập nhật lớp học: " + e.getMessage());
			throw new RuntimeException("Không thể cập nhật lớp học: " + e.getMessage());
		}
	}

	private boolean existsByTenLop(String tenLop) {
		return lopHocRepository.existsByTenLop(tenLop);
	}
} 