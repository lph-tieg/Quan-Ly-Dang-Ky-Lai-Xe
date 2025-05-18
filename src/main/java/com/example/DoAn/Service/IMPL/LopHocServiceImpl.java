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
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.KhoaHoc;
import com.example.DoAn.Model.LopHoc;
import com.example.DoAn.Repository.LopHocRepository;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.LichSuHoatDongService;
import com.example.DoAn.Service.LopHocService;

@Service
@Transactional
public class LopHocServiceImpl implements LopHocService {

	@Autowired
	private LopHocRepository lopHocRepository;

	@Autowired
	@Lazy
	private HocVienService hocVienService;

	@Autowired
	@Lazy
	private GiangVienService giangVienService;

	@Autowired
	private LichSuHoatDongService lichSuService;

	// Tìm lớp theo tên không phân biệt chữ hoa chữ thường khi phân trang
	@Override
	public Page<LopHoc> findByTenLopContainingIgnoreCase(String tenLop, Pageable pageable) {
		return lopHocRepository.findByTenLopContainingIgnoreCase(tenLop, pageable);
	}

	// Tìm lớp theo hạng
	@Override
	public List<LopHoc> findByHang(Hang hang) {
		return lopHocRepository.findByHang(hang);
	}

	// Tìm lớp theo buổi học
	@Override
	public List<LopHoc> findByBuoiHoc(String buoiHoc) {
		return lopHocRepository.findByBuoiHoc(buoiHoc);
	}

	// Tìm lớp theo lịch học
	@Override
	public List<LopHoc> findByLichHoc(String lichHoc) {
		return lopHocRepository.findByLichHoc(lichHoc);
	}

	// Lấy danh sách lớp học khi phân trang
	@Override
	public Page<LopHoc> findALlLopPage(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return lopHocRepository.findAll(pageable);
	}

	// Lấy danh sách các lớp học
	@Override
	public List<LopHoc> findAllLopHoc() {
		return lopHocRepository.findAll();
	}

	// Xoá lớp học và lưu lại lịch sử hoạt động
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
				hocVien.removeLopHoc(lopHoc);
				hocVienService.updateHocVien(hocVien, nguoiThucHien);
			}

			// 4. Xóa lớp học
			lopHocRepository.delete(lopHoc);

			String noiDung = "Xoá lớp học " + lopHoc.getTenLop();
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Lớp Học", noiDung);

			System.out.print("Đã xóa thành công lớp học có ID: {}" + lopHocID);
		} catch (Exception e) {
			System.out.print("Lỗi khi xóa lớp học có ID {}: {}" + lopHocID + e.getMessage());
			throw new RuntimeException("Không thể xóa lớp học: " + e.getMessage());
		}
	}

	// Tìm lớp học theo ID
	@Override
	public LopHoc findByLopHocID(Integer lopHocID) {
		if (lopHocID == null) {
			throw new IllegalArgumentException("ID lớp học không được để trống");
		}
		return lopHocRepository.findById(lopHocID)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ID: " + lopHocID));
	}

	// Thêm lớp học và lưu lại lịch sử hoạt động
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
			System.out.println("Đã tạo thành công lớp học với ID: " + savedLopHoc.getLopHocID());
			String noiDung = "Thêm mới lớp học" + lopHoc.getTenLop();
			lichSuService.themLichSu(nguoiThucHien, "Thêm Mới", "Lớp Học", noiDung);
			return savedLopHoc;
		} catch (Exception e) {
			System.out.println("Lỗi khi tạo lớp học: " + e.getMessage());
			throw new RuntimeException("Không thể tạo lớp học: " + e.getMessage());
		}
	}

	// Tìm lớp học theo lịch học, buổi học, hạng
	@Override
	public List<LopHoc> findByFilter(String lichHoc, String buoiHoc, Hang hang) {
		return lopHocRepository.findAll().stream()
				.filter(lop -> lop.getLichHoc().equalsIgnoreCase(lichHoc) && lop.getBuoiHoc().equalsIgnoreCase(buoiHoc)
						&& lop.getHang().equals(hang))
				.collect(Collectors.toList());
	}

	// Tìm lớp học theo khoá học
	@Override
	public LopHoc findByKhoaHoc(KhoaHoc khoaHoc) {
		return lopHocRepository.findByKhoaHoc(khoaHoc);
	}

	// Tìm lớp học theo tên (xác nhận tên lớp đã tồn tại hay chưa)
	@Override
	public boolean existsByTenLop(String tenLop) {
		return lopHocRepository.existsByTenLop(tenLop);
	}

	// Tìm lớp học theo ID (ID duy nhất trong lớp)
	@Override
	public Optional<LopHoc> findById(Integer lopHocID) {
		return lopHocRepository.findById(lopHocID);
	}

	// Tìm lớp học theo tên (Tên duy nhất trong lớp)
	@Override
	public Optional<LopHoc> findByTenLop(String tenLop) {
		return lopHocRepository.findByTenLop(tenLop);
	}

	// Phân loại nội dung thay đổi trong lớp học
	private String getChangeType(String change) {
		if (change.contains("thay đổi hạng"))
			return "HANG";
		if (change.contains("thay đổi lịch học"))
			return "LICH_HOC";
		if (change.contains("thay đổi buổi học"))
			return "BUOI_HOC";
		if (change.contains("thêm GV"))
			return "THEM_GIANG_VIEN";
		if (change.contains("xóa GV"))
			return "XOA_GIANG_VIEN";
		return "KHAC";
	}

	// Tách nội dung log thành nhiều phần nếu độ dài vượt quá 255 ký tự
	private List<String> splitLogEntry(String noiDung) {
		List<String> result = new ArrayList<>();
		String prefix = "Lớp " + noiDung.substring(noiDung.indexOf("Lớp ") + 4, noiDung.indexOf(":")) + ": ";

		// Tách các thay đổi bằng dấu chấm phẩy
		String[] changes = noiDung.substring(noiDung.indexOf(":") + 1).trim().split(";");

		StringBuilder currentEntry = new StringBuilder(prefix);

		for (String change : changes) {
			change = change.trim();
			// Nếu thêm change hiện tại sẽ vượt quá 255 ký tự
			if (currentEntry.length() + change.length() + 2 > 252) {
				// Thêm entry hiện tại vào kết quả
				result.add(currentEntry.toString());
				// Bắt đầu entry mới
				currentEntry = new StringBuilder(prefix);
			}

			// Thêm dấu chấm phẩy nếu không phải là phần tử đầu tiên của entry
			if (currentEntry.length() > prefix.length()) {
				currentEntry.append("; ");
			}
			currentEntry.append(change);
		}

		// Thêm entry cuối cùng vào kết quả
		if (currentEntry.length() > prefix.length()) {
			result.add(currentEntry.toString());
		}

		return result;
	}

	// Cập nhật lớp học và lưu lại lịch sử hoạt động
	@Override
	@Transactional(noRollbackFor = { IllegalArgumentException.class, RuntimeException.class })
	public void updateLopHoc(LopHoc lopHoc, List<Integer> giangVienMoiID, String nguoiThucHien) {
		try {
			if (lopHoc == null || lopHoc.getLopHocID() == null) {
				throw new IllegalArgumentException("Thông tin lớp học không hợp lệ");
			}

			LopHoc lopHocHienTai = lopHocRepository.findById(lopHoc.getLopHocID())
					.orElseThrow(() -> new RuntimeException("Lớp học không tồn tại"));

			// Kiểm tra tên lớp mới
			if (!lopHocHienTai.getTenLop().equals(lopHoc.getTenLop()) && existsByTenLop(lopHoc.getTenLop())) {
				throw new RuntimeException("Tên lớp đã tồn tại");
			}

			List<String> changes = new ArrayList<>();

			// Xử lý thay đổi hạng
			if (!lopHocHienTai.getHang().getHangID().equals(lopHoc.getHang().getHangID())) {
				// Tìm các giảng viên không đủ hạng
				List<GiangVien> giangVienKhongDuHang = lopHocHienTai.getListGiangVien().stream()
						.filter(gv -> gv.getHang().getCapBac() < lopHoc.getHang().getCapBac())
						.collect(Collectors.toList());

				if (!giangVienKhongDuHang.isEmpty()) {
					String danhSachGV = giangVienKhongDuHang.stream()
							.map(gv -> gv.getHoTenGV() + " (Hạng " + gv.getHang().getTenHang() + ")")
							.collect(Collectors.joining(", "));

					throw new RuntimeException(
							"Không thể cập nhật hạng lớp vì các giảng viên sau có hạng thấp hơn: " + danhSachGV);
				}

				changes.add(String.format("thay đổi hạng từ %s sang %s", lopHocHienTai.getHang().getTenHang(),
						lopHoc.getHang().getTenHang()));
			}

			// Ghi nhận các thay đổi khác
			if (!lopHocHienTai.getLichHoc().equals(lopHoc.getLichHoc())) {
				changes.add(String.format("thay đổi lịch học từ %s sang %s", lopHocHienTai.getLichHoc(),
						lopHoc.getLichHoc()));
			}

			if (!lopHocHienTai.getBuoiHoc().equals(lopHoc.getBuoiHoc())) {
				changes.add(String.format("thay đổi buổi học từ %s sang %s", lopHocHienTai.getBuoiHoc(),
						lopHoc.getBuoiHoc()));
			}

			// Cập nhật thông tin cơ bản
			lopHocHienTai.setTenLop(lopHoc.getTenLop());
			lopHocHienTai.setLichHoc(lopHoc.getLichHoc());
			lopHocHienTai.setBuoiHoc(lopHoc.getBuoiHoc());
			lopHocHienTai.setHang(lopHoc.getHang());
			lopHocHienTai.setKhoaHoc(lopHoc.getKhoaHoc());
			lopHocHienTai.setThoiLuongHoc(lopHoc.getThoiLuongHoc());

			// Xử lý danh sách giảng viên mới (nếu có)
			List<GiangVien> giangVienMoi = new ArrayList<>();
			if (giangVienMoiID != null && !giangVienMoiID.isEmpty()) {
				giangVienMoi = giangVienService.findAllByID(giangVienMoiID);

				// Kiểm tra hạng của giảng viên mới
				List<GiangVien> giangVienMoiKhongDuHang = giangVienMoi.stream()
						.filter(gv -> gv.getHang().getCapBac() < lopHoc.getHang().getCapBac())
						.collect(Collectors.toList());

				if (!giangVienMoiKhongDuHang.isEmpty()) {
					String danhSachGV = giangVienMoiKhongDuHang.stream().map(gv -> gv.getHoTenGV() + " (Hạng "
							+ gv.getHang().getTenHang() + " - Cấp bậc " + gv.getHang().getCapBac() + ")")
							.collect(Collectors.joining(", "));
					throw new RuntimeException("Không thể thêm các giảng viên sau vì có hạng thấp hơn hạng lớp "
							+ lopHoc.getHang().getTenHang() + " (Cấp bậc " + lopHoc.getHang().getCapBac() + "): "
							+ danhSachGV);
				}
			}

			// Xử lý thay đổi giảng viên
			List<GiangVien> giangVienBiXoa = new ArrayList<>(lopHocHienTai.getListGiangVien());
			giangVienBiXoa.removeAll(giangVienMoi);

			List<GiangVien> giangVienDuocThem = new ArrayList<>(giangVienMoi);
			giangVienDuocThem.removeAll(lopHocHienTai.getListGiangVien());

			// Xóa mối quan hệ với giảng viên cũ
			for (GiangVien gv : giangVienBiXoa) {
				gv.getLopHocs().remove(lopHocHienTai);
				giangVienService.updateGiangVien(gv, nguoiThucHien);
			}

			// Thêm mối quan hệ với giảng viên mới
			for (GiangVien gv : giangVienDuocThem) {
				gv.getLopHocs().add(lopHocHienTai);
				giangVienService.updateGiangVien(gv, nguoiThucHien);
			}

			// Cập nhật danh sách giảng viên của lớp
			lopHocHienTai.getListGiangVien().clear();
			lopHocHienTai.getListGiangVien().addAll(giangVienMoi);

			// Lưu lớp học
			lopHocRepository.save(lopHocHienTai);

			// Ghi log thay đổi giảng viên
			if (!giangVienBiXoa.isEmpty() || !giangVienDuocThem.isEmpty()) {
				StringBuilder gvChanges = new StringBuilder();

				if (!giangVienDuocThem.isEmpty()) {
					gvChanges.append("thêm GV ");
					gvChanges.append(
							giangVienDuocThem.stream().map(gv -> gv.getHoTenGV() + "-" + gv.getHang().getTenHang())
									.collect(Collectors.joining(", ")));
				}

				if (!giangVienBiXoa.isEmpty()) {
					if (gvChanges.length() > 0) {
						gvChanges.append("; ");
					}
					gvChanges.append("xóa GV ");
					gvChanges
							.append(giangVienBiXoa.stream().map(gv -> gv.getHoTenGV() + "-" + gv.getHang().getTenHang())
									.collect(Collectors.joining(", ")));
				}

				changes.add(gvChanges.toString());
			}

			// Ghi log các thay đổi
			if (!changes.isEmpty()) {
				String noiDung = "Lớp " + lopHoc.getTenLop() + ": " + String.join("; ", changes);
				if (noiDung.length() > 255) {
					List<String> splitEntries = splitLogEntry(noiDung);
					for (String entry : splitEntries) {
						lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Lớp Học", entry);
					}
				} else {
					lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Lớp Học", noiDung);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Không thể cập nhật lớp học: " + e.getMessage());
		}
	}

	// Tìm lớp theo ID hạng trong phân trang
	@Override
	public Page<LopHoc> findByHangHangID(Integer hangID, Pageable pageable) {
		return lopHocRepository.findByHangHangID(hangID, pageable);
	}

	// Tìm lớp theo buổi học trong phân trang
	@Override
	public Page<LopHoc> findByBuoiHoc(String buoiHoc, Pageable pageable) {
		return lopHocRepository.findByBuoiHoc(buoiHoc, pageable);
	}
}