package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.LopHoc;
import com.example.DoAn.Model.XeTapLai;
import com.example.DoAn.Service.AdminService;
import com.example.DoAn.Service.DangKyService;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.HangService;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.KhoaHocService;
import com.example.DoAn.Service.LichSuHoatDongService;
import com.example.DoAn.Service.LopHocService;
import com.example.DoAn.Service.TuVanService;
import com.example.DoAn.Service.XeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/hoc_vien")
public class HocVienController {
	@Autowired
	private HocVienService hocVienService;

	@Autowired
	private GiangVienService giangVienService;

	@Autowired
	private XeService xeService;

	@Autowired
	private TuVanService tuVanService;

	@Autowired
	private KhoaHocService khoaHocService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private HttpSession session;

	@Autowired
	private DangKyService dangKyService;

	@Autowired
	private LopHocService lopHocService;

	@Autowired
	private HangService hangService;

	@Autowired
	private LichSuHoatDongService lichSuService;

	/**
	 * Hiển thị danh sách học viên Các chức năng lọc, tìm kiếm
	 */
	@GetMapping("")
	public String getHocVien(Model model, @RequestParam(value = "keyword", required = false) String keyWord,
			@RequestParam(value = "hang", required = false) String hang,
			@RequestParam(value = "buoiHoc", required = false) String buoiHoc,
			@RequestParam(value = "lichHoc", required = false) String lichHoc,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Authentication authentication) {
		try {
			Page<HocVien> listHocVien;
			int pageSize = 5;

			if (keyWord != null && !keyWord.isEmpty()) {
				List<HocVien> filteredList = new ArrayList<>();

				// Tìm theo họ tên
				Page<HocVien> nameResults = hocVienService.findByHoTenContainingIgnoreCase(keyWord,
						PageRequest.of(0, Integer.MAX_VALUE));
				filteredList.addAll(nameResults.getContent());

				// Tìm theo email nếu không tìm thấy theo tên
				if (filteredList.isEmpty()) {
					Page<HocVien> emailResults = hocVienService.findByEmailContainingIgnoreCase(keyWord,
							PageRequest.of(0, Integer.MAX_VALUE));
					filteredList.addAll(emailResults.getContent());
				}

				// Tìm theo số điện thoại nếu vẫn không tìm thấy
				if (filteredList.isEmpty()) {
					Page<HocVien> phoneResults = hocVienService.findBySdtContaining(keyWord,
							PageRequest.of(0, Integer.MAX_VALUE));
					filteredList.addAll(phoneResults.getContent());
				}

				if (filteredList.isEmpty()) {
					model.addAttribute("message", "Không tìm thấy học viên nào phù hợp với từ khóa: " + keyWord);
				}

				// Phân trang
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<HocVien> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listHocVien = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());

				// Lọc theo hạng, buổi học, lịch học
			} else if (hang != null || buoiHoc != null || lichHoc != null) {
				List<HocVien> filteredList = hocVienService.findAllHocVienList();
				// Lọc theo Hạng
				if (hang != null && !hang.isEmpty()) {
					filteredList = filteredList.stream().filter(hv -> hang.equals(hv.getHangDK()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào thuộc hạng " + hang);
					}
				}
				// Lọc theo Buổi Học
				if (buoiHoc != null && !buoiHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(hv -> buoiHoc.equals(hv.getBuoiHoc()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào học vào buổi " + buoiHoc);
					}
				}
				// Lọc theo Lịch Học
				if (lichHoc != null && !lichHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(hv -> lichHoc.equals(hv.getLichHoc()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào có lịch học " + lichHoc);
					}
				}
				// Phân trang
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<HocVien> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listHocVien = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			} else {
				// Trả về tất cả học viên trong danh sách nếu không lọc hoặc tìm kiếm
				listHocVien = hocVienService.findAllHocVien(pageNo);
			}

			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm dữ liệu cho các Dropdown
			List<Hang> listHang = hangService.findAll();
			model.addAttribute("listHang", listHang);

			// Hiển thị lịch học duy nhất
			List<String> listLichHoc = hocVienService.findDistinctLichHoc();
			model.addAttribute("listLichHoc", listLichHoc);

			// Thêm các giá trị đã chọn vào model
			model.addAttribute("selectedHang", hang);
			model.addAttribute("selectedBuoiHoc", buoiHoc);
			model.addAttribute("selectedLichHoc", lichHoc);

			// Truyền dữ liệu sang view
			model.addAttribute("xeTapLai", xeService.findAllXe());
			model.addAttribute("khoaHoc", khoaHocService.findAllKhoaHoc());
			model.addAttribute("pageHocVien", listHocVien);
			model.addAttribute("pageTitle", "Danh Sách Học Viên");
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPage", listHocVien.getTotalPages());
			model.addAttribute("keyword", keyWord);

			// Cập nhật thời gian học và trạng thái cho mỗi học viên
			for (HocVien hocVien : listHocVien.getContent()) {
				// Lấy thời gian học từ hạng của học viên
				if (hocVien.getHang() != null) {
					hocVien.setThoiGianHoc(hocVien.getHang().getThoiGianHoc());
				}

				// Cập nhật trạng thái dựa trên thời gian đã học
				if (hocVien.getThoiGianDaHoc() != null && hocVien.getThoiGianHoc() != null) {
					if (hocVien.getThoiGianDaHoc() >= hocVien.getThoiGianHoc()) {
						hocVien.setTrangThai("Đã hoàn thành");
					} else {
						hocVien.setTrangThai("Đang học");
					}
				} else {
					hocVien.setTrangThai("Chưa bắt đầu");
				}
			}

			return "admin/hocVien/hocVien";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách học viên: " + e.getMessage());
			return "admin/hocVien/hocVien";
		}
	}

	// Cập nhật học viên theo ID
	@GetMapping("/{id}")
	public String updateHocVienByID(@PathVariable("id") Integer hocVienID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		HocVien hocVien = hocVienService.findByID(hocVienID);
		if (hocVien == null) {
			return "redirect:/admin/hoc_vien";
		}

		List<Hang> listHang = hangService.findAll();
		List<LopHoc> listLopHoc = lopHocService.findAllLopHoc();
		List<GiangVien> listGiangVien = giangVienService.findAllGiangVien();

		model.addAttribute("listHang", listHang);
		model.addAttribute("listLopHoc", listLopHoc);
		model.addAttribute("listGiangVien", listGiangVien);
		model.addAttribute("hocVien", hocVienService.findByID(hocVienID));
		model.addAttribute("listKhoaHoc", khoaHocService.findAllKhoaHoc());
		model.addAttribute("listXe", xeService.findAllXe());
		model.addAttribute("pageTitle", "Học Viên/Cập Nhật");
		return ("/admin/hocVien/addUpateHocVien");
	}

	// Thông tin chi tiết học viên
	@GetMapping("chi_tiet/{id}")
	public String getHocVienDetail(@PathVariable("id") Integer hocVienID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		HocVien hocVien = hocVienService.findByID(hocVienID);
		if (hocVien == null) {
			throw new RuntimeException("Không tìm thấy học viên với ID: " + hocVienID);
		}

		// Tạo danh sách giảng viên không trùng lặp
		Set<GiangVien> uniqueGiangViens = new HashSet<>();
		for (LopHoc lopHoc : hocVien.getLopHocs()) {
			System.out.println("Lớp học: " + lopHoc.getTenLop());
			System.out.println("Số giảng viên trong lớp: "
					+ (lopHoc.getListGiangVien() != null ? lopHoc.getListGiangVien().size() : 0));
			if (lopHoc.getListGiangVien() != null) {
				for (GiangVien gv : lopHoc.getListGiangVien()) {
					System.out.println("Giảng viên: " + gv.getHoTenGV() + " (ID: " + gv.getGiangVienID() + ")");
				}
				uniqueGiangViens.addAll(lopHoc.getListGiangVien());
			}
		}
		List<GiangVien> listGiangVien = new ArrayList<>(uniqueGiangViens);
		System.out.println("Tổng số giảng viên không trùng lặp: " + listGiangVien.size());
		for (GiangVien gv : listGiangVien) {
			System.out.println(
					"Giảng viên sau khi loại bỏ trùng lặp: " + gv.getHoTenGV() + " (ID: " + gv.getGiangVienID() + ")");
		}

		model.addAttribute("hocVien", hocVien);
		model.addAttribute("listGiangVien", listGiangVien);
		model.addAttribute("pageTitle", "Thông tin chi tiết học viên");
		return "admin/hocVien/hocVienDetail";
	}

	@Transactional
	@PostMapping("/cap_nhat")
	public String getUpdateHocVien(@ModelAttribute("hocVien") HocVien hocVien,
			@RequestParam("lopHocID") Integer lopHocID, RedirectAttributes redirectAttributes,
			Authentication authentication, Model model) {
		try {
			String nguoiThucHien = authentication.getName();

			// Get class info
			LopHoc lopHocMoi = lopHocService.findByLopHocID(lopHocID);
			if (lopHocMoi == null) {
				model.addAttribute("error", "Không tìm thấy lớp học");
				return setupUpdatePage(hocVien.getHocVienID(), model, authentication);
			}

			// Get current student info from database
			HocVien hocVienHienTai = hocVienService.findByID(hocVien.getHocVienID());
			if (hocVienHienTai == null) {
				model.addAttribute("error", "Không tìm thấy thông tin học viên");
				return setupUpdatePage(hocVien.getHocVienID(), model, authentication);
			}

			// Lưu thời gian học và trạng thái hiện tại
			Double thoiGianDaHoc = hocVienHienTai.getThoiGianDaHoc();
			String trangThai = hocVienHienTai.getTrangThai();

			// Create StringBuilder to track changes
			StringBuilder changes = new StringBuilder();

			// Check and record changes
			if (!Objects.equals(hocVienHienTai.getHoTen(), hocVien.getHoTen())) {
				changes.append("Họ tên: ").append(hocVienHienTai.getHoTen()).append(" → ").append(hocVien.getHoTen())
						.append("\n");
			}
			if (!Objects.equals(hocVienHienTai.getEmail(), hocVien.getEmail())) {
				changes.append("Email: ").append(hocVienHienTai.getEmail()).append(" → ").append(hocVien.getEmail())
						.append("\n");
			}
			if (!Objects.equals(hocVienHienTai.getSdt(), hocVien.getSdt())) {
				changes.append("SĐT: ").append(hocVienHienTai.getSdt()).append(" → ").append(hocVien.getSdt())
						.append("\n");
			}
			if (!Objects.equals(hocVienHienTai.getDiaChi(), hocVien.getDiaChi())) {
				changes.append("Địa chỉ: ").append(hocVienHienTai.getDiaChi()).append(" → ").append(hocVien.getDiaChi())
						.append("\n");
			}

			// Update basic information
			hocVienHienTai.setHoTen(hocVien.getHoTen());
			hocVienHienTai.setEmail(hocVien.getEmail());
			hocVienHienTai.setSdt(hocVien.getSdt());
			hocVienHienTai.setDiaChi(hocVien.getDiaChi());
			hocVienHienTai.setHangDK(hocVien.getHangDK());
			hocVienHienTai.setBuoiHoc(hocVien.getBuoiHoc());
			hocVienHienTai.setLichHoc(hocVien.getLichHoc());
			hocVienHienTai.setLoaiXeDK(hocVien.getLoaiXeDK());
			hocVienHienTai.setLoaiThucHanh(hocVien.getLoaiThucHanh());
			hocVienHienTai.setGhiChu(hocVien.getGhiChu());

			// Update class
			if (!hocVienHienTai.getLopHocs().contains(lopHocMoi)) {
				// Save old classes for checking
				List<LopHoc> lopHocCu = new ArrayList<>(hocVienHienTai.getLopHocs());

				// Remove student from old classes and update count
				for (LopHoc lop : lopHocCu) {
					// Save instructor list of old class
					List<GiangVien> currentGiangViens = new ArrayList<>(lop.getListGiangVien());
					List<Integer> giangVienIds = currentGiangViens.stream().map(GiangVien::getGiangVienID)
							.collect(Collectors.toList());

					hocVienHienTai.removeLopHoc(lop);
					lopHocService.updateLopHoc(lop, giangVienIds, nguoiThucHien);
					changes.append("Xóa khỏi lớp: ").append(lop.getTenLop()).append("\n");
				}

				// Add new class
				hocVienHienTai.addLopHoc(lopHocMoi);
				changes.append("Thêm lớp học: ").append(lopHocMoi.getTenLop()).append("\n");

				// Lấy giảng viên chính từ lớp học mới
				if (lopHocMoi.getListGiangVien() != null && !lopHocMoi.getListGiangVien().isEmpty()) {
					GiangVien giangVienChinh = lopHocMoi.getListGiangVien().get(0);
					hocVienHienTai.setGiangVienChinh(giangVienChinh);
					changes.append("Phân công giảng viên chính: ").append(giangVienChinh.getHoTenGV()).append("\n");
				}

				// Save instructor list of new class
				List<GiangVien> newGiangViens = new ArrayList<>(lopHocMoi.getListGiangVien());
				List<Integer> newGiangVienIds = newGiangViens.stream().map(GiangVien::getGiangVienID)
						.collect(Collectors.toList());

				// Update student count for new class
				lopHocMoi.capNhatSoLuong();
				lopHocService.updateLopHoc(lopHocMoi, newGiangVienIds, nguoiThucHien);

				// Cập nhật thời gian học từ lớp học mới
				hocVienHienTai.setThoiGianHoc(lopHocMoi.getThoiLuongHoc());
			}

			// Khôi phục thời gian đã học và trạng thái
			hocVienHienTai.setThoiGianDaHoc(thoiGianDaHoc);
			hocVienHienTai.setTrangThai(trangThai);

			// Save changes
			hocVienService.updateHocVien(hocVienHienTai, nguoiThucHien);

			redirectAttributes.addFlashAttribute("success", "Cập nhật học viên thành công");
			return "redirect:/admin/hoc_vien";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật học viên: " + e.getMessage());
			return "redirect:/admin/hoc_vien";
		}
	}

	@PostMapping("/xac_nhan_hoan_thanh/{id}")
	@ResponseBody
	@Transactional
	public ResponseEntity<?> xacNhanHoanThanh(@PathVariable("id") Integer hocVienID, Authentication authentication) {
		try {
			HocVien hocVien = hocVienService.findByID(hocVienID);
			if (hocVien == null) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy học viên"));
			}

			// Kiểm tra điều kiện hoàn thành
			if (!"Đủ điều kiện thi".equals(hocVien.getTrangThai())) {
				return ResponseEntity.badRequest()
						.body(Map.of("success", false, "message", "Học viên chưa đủ điều kiện thi"));
			}

			// Tạo nội dung chi tiết cho lịch sử
			StringBuilder chiTiet = new StringBuilder();
			chiTiet.append("Học viên: ").append(hocVien.getHoTen()).append("\nMã học viên: ")
					.append(hocVien.getHocVienID()).append("\nHạng đăng ký: ").append(hocVien.getHangDK())
					.append("\nXe đăng ký: ").append(hocVien.getLoaiXeDK()).append("\nLớp học: ");

			// Thêm thông tin về các lớp học
			for (LopHoc lopHoc : hocVien.getLopHocs()) {
				chiTiet.append("\n- ").append(lopHoc.getTenLop()).append(" (Thời gian học: ")
						.append(lopHoc.getThoiLuongHoc()).append(" giờ)");

				// Thêm thông tin giảng viên của lớp
				if (lopHoc.getListGiangVien() != null && !lopHoc.getListGiangVien().isEmpty()) {
					chiTiet.append("\n  Giảng viên: ");
					for (GiangVien gv : lopHoc.getListGiangVien()) {
						chiTiet.append(gv.getHoTenGV()).append(", ");
					}
					// Xóa dấu phẩy cuối cùng
					chiTiet.setLength(chiTiet.length() - 2);
				}
			}

			chiTiet.append("\nThời gian đã học: ").append(hocVien.getThoiGianDaHoc()).append(" giờ");

			// Cập nhật số lượng học viên trong các lớp học
			for (LopHoc lopHoc : hocVien.getLopHocs()) {
				lopHoc.giamSoLuong();
				lopHocService.updateLopHoc(lopHoc,
						lopHoc.getListGiangVien().stream().map(GiangVien::getGiangVienID).collect(Collectors.toList()),
						authentication.getName());
			}

			// Cập nhật số lượng xe còn lại
			if (hocVien.getLoaiXeDK() != null) {
				List<XeTapLai> xeList = xeService.findAllXe();
				for (XeTapLai xe : xeList) {
					if (xe.getTenXe().equals(hocVien.getLoaiXeDK())) {
						xe.setSoLuongConLai(xe.getSoLuongConLai() + 1);
						xeService.updateSoLuongXe(xe);
						break;
					}
				}
			}

			// Xóa học viên khỏi danh sách học viên hiện tại
			hocVienService.deleteHocVien(hocVienID);

			// Ghi log với chi tiết đầy đủ
			lichSuService.themLichSu(authentication.getName(), "Xác nhận hoàn thành", "Học Viên", chiTiet.toString());

			return ResponseEntity.ok(
					Map.of("success", true, "message", "Học viên " + hocVien.getHoTen() + " đã hoàn thành khóa học"));
		} catch (Exception e) {
			e.printStackTrace(); // Log lỗi để debug
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "Lỗi khi xác nhận hoàn thành: " + e.getMessage()));
		}
	}

	// Tách phần setup trang cập nhật thành method riêng để tái sử dụng
	private String setupUpdatePage(Integer hocVienID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		List<Hang> listHang = hangService.findAll();
		List<LopHoc> listLopHoc = lopHocService.findAllLopHoc();
		List<GiangVien> listGiangVien = giangVienService.findAllGiangVien();
		model.addAttribute("listHang", listHang);
		model.addAttribute("listLopHoc", listLopHoc);
		model.addAttribute("listGiangVien", listGiangVien);
		model.addAttribute("hocVien", hocVienService.findByID(hocVienID));
		model.addAttribute("listKhoaHoc", khoaHocService.findAllKhoaHoc());
		model.addAttribute("listXe", xeService.findAllXe());
		model.addAttribute("pageTitle", "Học Viên/Cập Nhật");
		return "/admin/hocVien/addUpateHocVien";
	}

	// Xử lý Post khi xoá học viên
	@Transactional
	@PostMapping("/delete/{id}")
	public String deleteHocVien(@PathVariable("id") Integer hocVienID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();

			// Lấy thông tin học viên trước khi xóa
			HocVien hocVien = hocVienService.findByID(hocVienID);
			if (hocVien != null) {
				// Cập nhật số lượng xe còn lại
				if (hocVien.getLoaiXeDK() != null) {
					List<XeTapLai> xeList = xeService.findAllXe();
					for (XeTapLai xe : xeList) {
						if (xe.getTenXe().equals(hocVien.getLoaiXeDK())) {
							xe.setSoLuongConLai(xe.getSoLuongConLai() + 1);
							xeService.updateXe(xe, nguoiThucHien);
							break;
						}
					}
				}

				// Cập nhật số lượng học viên trong các lớp học
				List<LopHoc> lopHocList = new ArrayList<>(hocVien.getLopHocs());
				for (LopHoc lopHoc : lopHocList) {
					// Xóa học viên khỏi danh sách của lớp
					hocVien.removeLopHoc(lopHoc);
					// Cập nhật số lượng học viên trong lớp
					lopHoc.capNhatSoLuong();
					// Cập nhật thông tin lớp học vào database
					lopHocService.updateLopHoc(lopHoc, lopHoc.getListGiangVien().stream().map(GiangVien::getGiangVienID)
							.collect(Collectors.toList()), nguoiThucHien);
				}

				// Xóa học viên
				hocVienService.deleteHocVienByID(hocVienID, nguoiThucHien);
				redirectAttributes.addFlashAttribute("success", "Xóa học viên thành công");
			} else {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy học viên");
			}
		} catch (Exception e) {
			e.printStackTrace(); // Log lỗi để debug
			redirectAttributes.addFlashAttribute("error", "Không thể xóa học viên: " + e.getMessage());
		}
		return "redirect:/admin/hoc_vien";
	}

	@PostMapping("/cap_nhat_thoi_gian/{id}")
	@ResponseBody
	@Transactional
	public ResponseEntity<?> capNhatThoiGianHoc(@PathVariable("id") Integer hocVienID,
			@RequestParam("time") Double thoiGianDaHoc, Authentication authentication) {
		try {
			HocVien hocVien = hocVienService.findByID(hocVienID);
			if (hocVien == null) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy học viên"));
			}

			// Lấy thời gian học từ lớp học
			Integer thoiGianHoc = null;
			if (!hocVien.getLopHocs().isEmpty()) {
				LopHoc lopHoc = hocVien.getLopHocs().get(0);
				thoiGianHoc = lopHoc.getThoiLuongHoc();
			}

			if (thoiGianHoc == null) {
				return ResponseEntity.badRequest()
						.body(Map.of("success", false, "message", "Không tìm thấy thời gian học"));
			}

			// Cập nhật thời gian đã học
			hocVien.setThoiGianDaHoc(thoiGianDaHoc);

			// Cập nhật trạng thái dựa trên thời gian học
			if (thoiGianDaHoc >= thoiGianHoc) {
				hocVien.setTrangThai("Đủ điều kiện thi");
			} else {
				hocVien.setTrangThai("Đang học");
			}

			// Lưu vào cơ sở dữ liệu
			hocVienService.updateHocVien(hocVien, authentication.getName());

			return ResponseEntity.ok(Map.of("success", true, "trangThai", hocVien.getTrangThai(), "thoiGianDaHoc",
					hocVien.getThoiGianDaHoc(), "thoiGianHoc", thoiGianHoc));
		} catch (Exception e) {
			e.printStackTrace(); // Log lỗi để debug
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "Lỗi server: " + e.getMessage()));
		}
	}

}
