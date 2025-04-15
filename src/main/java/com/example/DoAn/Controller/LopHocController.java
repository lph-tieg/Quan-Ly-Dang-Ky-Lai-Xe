package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.example.DoAn.Service.AdminService;
import com.example.DoAn.Service.DangKyService;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.HangService;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.KhoaHocService;
import com.example.DoAn.Service.LopHocService;
import com.example.DoAn.Service.TuVanService;
import com.example.DoAn.Service.XeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/lop_hoc")
public class LopHocController {

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

	// Tìm giảng viên theo select
	@GetMapping("/filter")
	@ResponseBody
	public ResponseEntity<?> getGiangVienFilter(@RequestParam(required = false) String lichHoc,
			@RequestParam(required = false) String buoiHoc, @RequestParam(required = false) String hang) {

		try {
			System.out.println("Received filter parameters:");
			System.out.println("Lich hoc: " + lichHoc);
			System.out.println("Buoi hoc: " + buoiHoc);
			System.out.println("Hang: " + hang);

			List<GiangVien> listGiangVien;

			// Kiểm tra nếu có đầy đủ tham số
			if (lichHoc != null && !lichHoc.isEmpty() && buoiHoc != null && !buoiHoc.isEmpty() && hang != null
					&& !hang.isEmpty()) {
				// Lấy danh sách giảng viên theo lịch học, buổi học và hạng
				listGiangVien = giangVienService.findGiangVienByLichHocBuoiHocHang(lichHoc, buoiHoc, hang);

				// Nếu không tìm thấy giảng viên nào, trả về danh sách rỗng
				if (listGiangVien == null || listGiangVien.isEmpty()) {
					listGiangVien = new ArrayList<>();
				}
			} else {
				// Nếu thiếu tham số, trả về danh sách rỗng
				listGiangVien = new ArrayList<>();
			}

			// Chuyển đổi kết quả thành DTO để tránh vòng lặp JSON
			List<Map<String, Object>> result = listGiangVien.stream().map(gv -> {
				Map<String, Object> dto = new HashMap<>();
				dto.put("giangVienID", gv.getGiangVienID());
				dto.put("hoTenGV", gv.getHoTenGV() + (gv.getHang() != null ? " - " + gv.getHang().getTenHang() : ""));
				dto.put("hangGV", gv.getHang() != null ? gv.getHang().getTenHang() : "");
				return dto;
			}).collect(Collectors.toList());

			System.out.println("Found " + result.size() + " matching giang vien");
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			System.err.println("Error in filter endpoint: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	/**
	 * Hiển thị danh sách lớp học Các chức năng lọc, tìm kiếm
	 */
	@GetMapping("")
	public String getLopHoc(Model model, @RequestParam(value = "keyword", required = false) String keyWord,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "hang", required = false) Integer hangID,
			@RequestParam(value = "buoiHoc", required = false) String buoiHoc,
			@RequestParam(value = "lichHoc", required = false) String lichHoc, Authentication authentication) {
		try {
			Page<LopHoc> listLopHoc;
			int pageSize = 5;

			if (keyWord != null && !keyWord.isEmpty()) {
				listLopHoc = lopHocService.findByTenLopContainingIgnoreCase(keyWord,
						PageRequest.of(pageNo - 1, pageSize));
				if (listLopHoc.isEmpty()) {
					model.addAttribute("message", "Không tìm thấy lớp học nào phù hợp với từ khóa: " + keyWord);
				}
			} else {
				List<LopHoc> filteredList = lopHocService.findAllLopHoc();
				List<String> conditions = new ArrayList<>();

				// Lọc theo Hạng
				if (hangID != null) {
					Optional<Hang> hangOpt = hangService.findByHangID(hangID);
					if (hangOpt.isPresent()) {
						String hangName = hangOpt.get().getTenHang();
						filteredList = filteredList.stream().filter(lop -> lop.getHang().getHangID().equals(hangID))
								.collect(Collectors.toList());
						conditions.add("hạng " + hangName);
					}
				}
				// Lọc theo Buổi học
				if (buoiHoc != null && !buoiHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(lop -> buoiHoc.equals(lop.getBuoiHoc()))
							.collect(Collectors.toList());
					conditions.add("buổi " + buoiHoc);
				}

				// Lọc theo Lịch Học
				if (lichHoc != null && !lichHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(lop -> lichHoc.equals(lop.getLichHoc()))
							.collect(Collectors.toList());
					conditions.add("lịch học " + lichHoc);
				}

				// Tạo thông báo tổng hợp nếu không tìm thấy kết quả
				if (!conditions.isEmpty() && filteredList.isEmpty()) {
					String message = "Không tìm thấy lớp học có " + String.join(" | ", conditions);
					model.addAttribute("message", message);
				}

				// Phân trang
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());

				List<LopHoc> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listLopHoc = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			}

			// Xử lý thông tin người dùng và các thuộc tính khác của model
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm dữ liệu cho các Dropdown
			model.addAttribute("listHang", hangService.findAll());
			model.addAttribute("selectedHang", hangID);
			model.addAttribute("selectedBuoiHoc", buoiHoc);
			model.addAttribute("selectedLichHoc", lichHoc);

			// Thêm danh sách lịch học
			List<String> listLichHoc = lopHocService.findAllLopHoc().stream().map(LopHoc::getLichHoc).distinct()
					.collect(Collectors.toList());
			model.addAttribute("listLichHoc", listLichHoc);

			// Thêm các thuộc tính khác
			model.addAttribute("listLopHoc", listLopHoc);
			model.addAttribute("pageTitle", "Danh Sách Lớp Học");
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPage", listLopHoc.getTotalPages());
			model.addAttribute("keyword", keyWord);

			return "/admin/lopHoc/lopHoc";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách lớp học: " + e.getMessage());
			return "/admin/lopHoc/lopHoc";
		}
	}

	// Thêm mới lớp học
	@GetMapping("/them_moi")
	public String getAddLopHoc(Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}
		List<Hang> listHang = hangService.findAll();
		model.addAttribute("listGiangVien", giangVienService.findAllGiangVien());
		model.addAttribute("listHang", listHang);
		model.addAttribute("lopHoc", new LopHoc());
		model.addAttribute("isEdit", false);
		model.addAttribute("pageTitle", "Lớp Học/Thêm Mới");
		return ("/admin/lopHoc/addUpdateLopHoc");
	}

	// Tìm lớp học dựa trên ID
	@GetMapping("/{id}")
	public String getLopHocByID(@PathVariable("id") Integer LophocID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}
		LopHoc lopHoc = lopHocService.findByLopHocID(LophocID);
		if (lopHoc == null) {
			model.addAttribute("error", "Lớp Học không tồn tại");
			return "redirect:/admin/lop_hoc";
		}

		List<Hang> listHang = hangService.findAll();
		model.addAttribute("listHang", listHang);
		model.addAttribute("lopHoc", lopHoc);
		model.addAttribute("listLopHoc", lopHocService.findAllLopHoc());
		model.addAttribute("isEdit", true);
		model.addAttribute("titlePage", "Lớp Học/Cập Nhật");
		return ("/admin/lopHoc/addUpdateLopHoc");
	}

	// Xử lý Post cho cập nhật
	@Transactional
	@PostMapping("/cap_nhat")
	public String updateLopHoc(@ModelAttribute("lopHoc") LopHoc lopHoc, RedirectAttributes redirectAttributes,
			@RequestParam(value = "hangID") Integer hangID,
			@RequestParam(value = "giangVienID", required = false) List<Integer> giangVienID,
			Authentication authentication) {

		try {
			boolean isUpdating = lopHoc.getLopHocID() != null;

			if (isUpdating) {
				Optional<LopHoc> existingLop = lopHocService.findByTenLop(lopHoc.getTenLop());
				if (existingLop.isPresent() && !existingLop.get().getLopHocID().equals(lopHoc.getLopHocID())) {
					redirectAttributes.addFlashAttribute("error", "Tên lớp đã tồn tại!");
					return "redirect:/admin/lop_hoc/cap_nhat?lopHocID=" + lopHoc.getLopHocID();
				}
			} else {
				if (lopHocService.existsByTenLop(lopHoc.getTenLop())) {
					redirectAttributes.addFlashAttribute("error", "Tên lớp đã tồn tại!");
					return "redirect:/admin/lop_hoc/them_moi";
				}
			}

			// Xử lý hạng
			Optional<Hang> hangOptional = hangService.findByHangID(hangID);
			if (hangOptional.isPresent()) {
				lopHoc.setHang(hangOptional.get());
			} else {
				redirectAttributes.addFlashAttribute("error", "Hạng không tồn tại!");
				return "redirect:/admin/lop_hoc/them_moi";
			}

			// Lấy danh sách giảng viên phù hợp với điều kiện mới
			List<GiangVien> availableGiangViens = giangVienService.findGiangVienByLichHocBuoiHocHang(
					lopHoc.getLichHoc(), lopHoc.getBuoiHoc(), lopHoc.getHang().getTenHang());

			// Xử lý giảng viên cho cả thêm mới và cập nhật
			if (giangVienID != null && !giangVienID.isEmpty()) {
				List<GiangVien> selectedGiangViens = giangVienService.findAllByID(giangVienID);

				// Kiểm tra xem các giảng viên được chọn có phù hợp với điều kiện mới không
				for (GiangVien gv : selectedGiangViens) {
					if (!availableGiangViens.contains(gv)) {
						// Tìm giảng viên thay thế có cùng tên nhưng đúng hạng
						Optional<GiangVien> replacement = availableGiangViens.stream()
								.filter(available -> available.getHoTenGV().equals(gv.getHoTenGV())).findFirst();

						if (replacement.isPresent()) {
							// Thay thế giảng viên cũ bằng giảng viên mới có cùng tên nhưng đúng hạng
							giangVienID.set(giangVienID.indexOf(gv.getGiangVienID()),
									replacement.get().getGiangVienID());
						}
					}
				}

				// Cập nhật lại danh sách giảng viên với các thay thế (nếu có)
				selectedGiangViens = giangVienService.findAllByID(giangVienID);
				lopHoc.setListGiangVien(selectedGiangViens);
			} else {
				// Nếu không có giảng viên được chọn, đặt danh sách rỗng
				lopHoc.setListGiangVien(new ArrayList<>());
			}

			String nguoiThucHien = authentication.getName();

			// Xử lý cập nhật hoặc thêm mới
			if (isUpdating) {
				lopHocService.updateLopHoc(lopHoc, giangVienID, nguoiThucHien);
				redirectAttributes.addFlashAttribute("success", "Cập nhật lớp học thành công");
			} else {
				lopHocService.createLopHoc(lopHoc, nguoiThucHien);
				redirectAttributes.addFlashAttribute("success", "Thêm mới lớp học thành công");
			}

			return "redirect:/admin/lop_hoc";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
			return "redirect:/admin/lop_hoc";
		}
	}

	// Xử lý Post cho xoá lớp học
	@PostMapping("/delete/{id}")
	public String deleteLopHoc(@PathVariable("id") Integer lopHocID, Authentication authentication,
			RedirectAttributes redirectAttributes) {
		try {
			String nguoiThucHien = authentication.getName();
			lopHocService.deleteLop(lopHocID, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Xóa lớp học thành công");
			return "redirect:/admin/lop_hoc";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa lớp học, hãy thử lại!");
			return "redirect:/admin/lop_hoc";
		}
	}

	@GetMapping("/{id}/danh_sach")
	public String getHocVienByLopHoc(@PathVariable("id") Integer lopHocID, Model model, Authentication authentication) {
		try {
			// Get user info
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Get class info
			LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
			if (lopHoc == null) {
				model.addAttribute("error", "Lớp học không tồn tại");
				return "redirect:/admin/lop_hoc";
			}

			// Get all students in the class
			List<HocVien> listHocVien = hocVienService.findHocVienByLopHoc(lopHocID);

			model.addAttribute("lopHoc", lopHoc);
			model.addAttribute("listHocVien", listHocVien);
			model.addAttribute("pageTitle", "Danh Sách Học Viên Lớp - " + lopHoc.getTenLop());

			return "/admin/lopHoc/allHocVien";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách học viên: " + e.getMessage());
			return "redirect:/admin/lop_hoc";
		}
	}

	@GetMapping("/{id}/chon")
	public String chonHocVien(@PathVariable("id") Integer lopHocID, Model model, Authentication authentication) {
		try {
			// Get user info
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Get class info
			LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
			if (lopHoc == null) {
				model.addAttribute("error", "Lớp học không tồn tại");
				return "redirect:/admin/lop_hoc";
			}

			// Get available students (not in this class)
			List<HocVien> availableStudents = hocVienService.findHocVienNotInLopHoc(lopHocID);

			if (availableStudents.isEmpty()) {
				model.addAttribute("message", "Không có học viên nào khả dụng để thêm vào lớp");
			}

			model.addAttribute("lopHoc", lopHoc);
			model.addAttribute("listHocVien", availableStudents);
			model.addAttribute("pageTitle", "Chọn học viên cho lớp - " + lopHoc.getTenLop());

			return "/admin/lopHoc/chonHocVien";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách học viên: " + e.getMessage());
			return "redirect:/admin/lop_hoc";
		}
	}

	@Transactional
	@PostMapping("/{id}/them_hoc_vien")
	public String themHocVien(@PathVariable("id") Integer lopHocID,
			@RequestParam("hocVienIds") List<Integer> hocVienIds, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();

			// Get class info
			LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
			if (lopHoc == null) {
				redirectAttributes.addFlashAttribute("error", "Lớp học không tồn tại");
				return "redirect:/admin/lop_hoc";
			}

			// Lưu lại danh sách giảng viên hiện tại
			List<GiangVien> currentGiangViens = new ArrayList<>(lopHoc.getListGiangVien());
			List<Integer> giangVienIds = currentGiangViens.stream().map(GiangVien::getGiangVienID)
					.collect(Collectors.toList());

			int soHocVienThem = 0;
			// Add each selected student to the class
			for (Integer hocVienId : hocVienIds) {
				HocVien hocVien = hocVienService.findByID(hocVienId);
				if (hocVien != null && !hocVien.getLopHocs().contains(lopHoc)) {
					hocVien.addLopHoc(lopHoc);
					hocVienService.updateHocVien(hocVien, nguoiThucHien);
					soHocVienThem++;
				}
			}

			// Cập nhật số lượng học viên trong lớp
			if (soHocVienThem > 0) {
				lopHoc.capNhatSoLuong();
				// Cập nhật lớp học với danh sách giảng viên hiện tại
				lopHocService.updateLopHoc(lopHoc, giangVienIds, nguoiThucHien);
			}

			redirectAttributes.addFlashAttribute("success",
					"Đã thêm " + soHocVienThem + " học viên vào lớp thành công");
			return "redirect:/admin/lop_hoc/" + lopHocID + "/danh_sach";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi thêm học viên: " + e.getMessage());
			return "redirect:/admin/lop_hoc/" + lopHocID + "/chon_hoc_vien";
		}
	}

	@Transactional
	@PostMapping("/{lopHocID}/xoa_hoc_vien")
	public String xoaHocVienKhoiLop(@PathVariable("lopHocID") Integer lopHocID,
			@RequestParam("hocVienID") Integer hocVienID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();

			// Lấy thông tin lớp học và học viên
			LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
			HocVien hocVien = hocVienService.findByID(hocVienID);

			if (lopHoc == null || hocVien == null) {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin lớp học hoặc học viên");
				return "redirect:/admin/lop_hoc/" + lopHocID + "/danh_sach";
			}

			// Lưu lại danh sách giảng viên hiện tại
			List<GiangVien> currentGiangViens = new ArrayList<>(lopHoc.getListGiangVien());
			List<Integer> giangVienIds = currentGiangViens.stream().map(GiangVien::getGiangVienID)
					.collect(Collectors.toList());

			// Xóa học viên khỏi lớp
			hocVien.removeLopHoc(lopHoc);

			// Cập nhật số lượng học viên
			lopHoc.capNhatSoLuong();

			// Cập nhật thông tin lớp học và học viên
			lopHocService.updateLopHoc(lopHoc, giangVienIds, nguoiThucHien);
			hocVienService.updateHocVien(hocVien, nguoiThucHien);

			redirectAttributes.addFlashAttribute("success", "Đã xóa học viên khỏi lớp thành công");
			return "redirect:/admin/lop_hoc/" + lopHocID + "/danh_sach";

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa học viên khỏi lớp: " + e.getMessage());
			return "redirect:/admin/lop_hoc/" + lopHocID + "/danh_sach";
		}
	}

}
