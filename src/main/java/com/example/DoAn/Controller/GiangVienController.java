package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/admin/giang_vien")
public class GiangVienController {

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

	/**
	 * Hiển thị danh sách giảng viên Các chức năng lọc, tìm kiếm
	 *
	 */
	@GetMapping("")
	public String getALlGiangVien(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "hang", required = false) Integer hangID,
			@RequestParam(value = "buoiDay", required = false) String buoiDay,
			@RequestParam(value = "lichDay", required = false) String lichDay,
			@RequestParam(value = "lopHoc", required = false) Integer lopHocID, Model model,
			Authentication authentication) {

		try {
			Page<GiangVien> listGiangVien;

			// Tìm kiếm theo từ khóa (họ tên, email hoặc số điện thoại)
			if (keyword != null && !keyword.isEmpty()) {
				listGiangVien = giangVienService.findByHoTenGVContainingIgnoreCase(keyword,
						PageRequest.of(pageNo - 1, 5));
				if (listGiangVien.isEmpty()) {
					listGiangVien = giangVienService.findByEmailGVContainingIgnoreCase(keyword,
							PageRequest.of(pageNo - 1, 5));
				}
				if (listGiangVien.isEmpty()) {
					listGiangVien = giangVienService.findBySdtGVContaining(keyword, PageRequest.of(pageNo - 1, 5));
				}
				if (listGiangVien.isEmpty()) {
					model.addAttribute("message", "Không tìm thấy giảng viên nào phù hợp với từ khóa: " + keyword);
				}

				// Lọc theo các tiêu chí (hạng, buổi dạy, lịch dạy, lớp học)
			} else if (hangID != null || buoiDay != null || lichDay != null || lopHocID != null) {
				List<GiangVien> filteredList = giangVienService.findAllGiangVien();
				// Lọc theo Hạng
				if (hangID != null) {
					filteredList = filteredList.stream()
							.filter(gv -> gv.getHang() != null && gv.getHang().getHangID().equals(hangID))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy giảng viên nào thuộc hạng đã chọn");
					}
				}
				// Lọc theo Buổi Dạy
				if (buoiDay != null && !buoiDay.isEmpty()) {
					filteredList = filteredList.stream().filter(gv -> buoiDay.equals(gv.getBuoiDay()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy giảng viên nào dạy vào buổi " + buoiDay);
					}
				}

				// Lọc theo Lịch Dạy
				if (lichDay != null && !lichDay.isEmpty()) {
					filteredList = filteredList.stream().filter(gv -> lichDay.equals(gv.getLichDay()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy giảng viên nào có lịch dạy " + lichDay);
					}
				}

				if (lopHocID != null) {
					filteredList = filteredList.stream()
							.filter(gv -> gv.getLopHocs().stream().anyMatch(lop -> lop.getLopHocID().equals(lopHocID)))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
						model.addAttribute("message", "Không tìm thấy giảng viên nào dạy lớp "
								+ (lopHoc != null ? lopHoc.getTenLop() : "đã chọn"));
					}
				}

				// Phân trang cho danh sách đã lọc
				int pageSize = 5;
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<GiangVien> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listGiangVien = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			} else {
				// Nếu không có lọc hoặc tìm kiếm, lấy toàn bộ danh sách phân trang
				listGiangVien = giangVienService.getAllGiangVien(pageNo);
			}

			// Lấy thông tin người dùng hiện tại (admin)
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm dữ liệu cho các dropdown
			model.addAttribute("listHang", hangService.findAll());
			model.addAttribute("listLopHoc", lopHocService.findAllLopHoc());

			// Thêm danh sách lịch dạy duy nhất
			List<String> listLichDay = giangVienService.findAllGiangVien().stream().map(GiangVien::getLichDay)
					.distinct().filter(lichday -> lichday != null && !lichday.isEmpty()).collect(Collectors.toList());

			// Lưu các giá trị đã chọn để hiển thị lại trên giao diện
			model.addAttribute("selectedHang", hangID);
			model.addAttribute("selectedBuoiDay", buoiDay);
			model.addAttribute("selectedLichDay", lichDay);
			model.addAttribute("selectedLopHoc", lopHocID);
			model.addAttribute("listLichDay", listLichDay);

			// Truyền dữ liệu sang view
			model.addAttribute("totalPage", listGiangVien.getTotalPages());
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("listGiangVien", listGiangVien);
			model.addAttribute("keyword", keyword);
			model.addAttribute("pageTitle", "Danh Sách Giảng Viên");

			return "/admin/giangVien/giangVien";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách giảng viên: " + e.getMessage());
			return "/admin/giangVien/giangVien";
		}
	}

	// Xử lý yêu cầu GET để xóa giảng viên
	@GetMapping("/delete/{id}")
	public String deletGiangVien(@PathVariable("id") Integer giangVienID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();
			giangVienService.deleteGiangVien(giangVienID, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Xoá giảng viên thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xoá, vui lòng thử lại!");
		}
		return ("redirect:/admin/giang_vien");
	}

	// Hiển thị thông tin chi tiết giảng viên để chỉnh sửa
	@GetMapping("/{id}")
	public String getIDGiangVien(@PathVariable("id") Integer giangVienID, Model model, Authentication authentication) {
		List<Hang> listHang = hangService.findAll();
		List<LopHoc> listLopHoc = lopHocService.findAllLopHoc();

		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}
		System.out.println(listHang);
		GiangVien giangVien = giangVienService.findByIDGV(giangVienID);
		if (giangVien.getHang() == null) {
			giangVien.setHang(new Hang()); // Đặt một đối tượng Hang trống nếu null
		}

		model.addAttribute("giangVien", giangVien);
		model.addAttribute("listHang", listHang);
		model.addAttribute("listLop", listLopHoc);
		model.addAttribute("pageTitle", "Giảng Viên/Cập Nhật");
		model.addAttribute("isEdit", true);
		return ("/admin/giangVien/addUpdateGiangVien");
	}

	// Xử lý yêu cầu POST để cập nhật thông tin giảng viên
	@PostMapping("/cap_nhat")
	public String updateGiangVien(@ModelAttribute GiangVien giangVien,
			@RequestParam(name = "hangID", required = false) Integer hangID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			// Kiểm tra email trùng lặp với giảng viên khác
			if (giangVienService.isEmailExistsForOtherTeacher(giangVien.getEmailGV(), giangVien.getGiangVienID())) {
				redirectAttributes.addFlashAttribute("error",
						"Email này đã được sử dụng. Vui lòng sử dụng email khác.");
				return "redirect:/admin/giang_vien/" + giangVien.getGiangVienID();
			}

			// Kiểm tra số điện thoại trùng lặp với giảng viên khác
			if (giangVienService.isPhoneExistsForOtherTeacher(giangVien.getSdtGV(), giangVien.getGiangVienID())) {
				redirectAttributes.addFlashAttribute("error",
						"Số điện thoại này đã được sử dụng. Vui lòng sử dụng số điện thoại khác.");
				return "redirect:/admin/giang_vien/" + giangVien.getGiangVienID();
			}

			if (hangID != null) {
				// Tìm hạng theo ID
				Hang hang = hangService.findByHangID(hangID)
						.orElseThrow(() -> new RuntimeException("Không tìm thấy hạng"));
				giangVien.setHang(hang);
			}

			// Cập nhật giảng viên
			String nguoiThucHien = authentication.getName();
			giangVienService.updateGiangVien(giangVien, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Cập nhật giảng viên thành công");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật giảng viên: " + e.getMessage());
		}

		return "redirect:/admin/giang_vien";
	}

	// Xử lý yêu cầu POST để lọc danh sách lớp học theo lịch dạy, buổi dạy và hạng
	// (trả về HTML options)
	@PostMapping("/filter")
	@ResponseBody
	public String filterLopHoc(@RequestParam(value = "lichDay") String lichDay,
			@RequestParam(value = "buoiDay") String buoiDay, @RequestParam(value = "dayHang") Integer hangID) {
		try {
			Optional<Hang> hangOptional = hangService.findByHangID(hangID);

			if (!hangOptional.isPresent()) {
				return "<option value='' disabled>Không có lớp phù hợp</option>";
			}

			Hang hang = hangOptional.get();
			List<LopHoc> filteredLopHoc = lopHocService.findByFilter(lichDay, buoiDay, hang);
			if (filteredLopHoc.isEmpty()) {
				return "<option value='' disabled>Không có lớp phù hợp</option>";
			}
			StringBuilder options = new StringBuilder();
			for (LopHoc lopHoc : filteredLopHoc) {
				options.append("<option value='").append(lopHoc.getTenLop()).append("'>").append(lopHoc.getTenLop())
						.append("</option>");
			}
			return options.toString();
		} catch (Exception e) {
			return "<option value='' disabled>Lỗi: \" + e.getMessage() + \"</option>";
		}

	}

	// Hiển thị giao diện để thêm mới giảng viên
	@GetMapping("/them_moi")
	public String getAddGiangVien(Model model, Authentication authentication) {
		List<Hang> listHang = hangService.findAll();
		List<LopHoc> listLop = lopHocService.findAllLopHoc();

		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		model.addAttribute("giangVien", new GiangVien());
		model.addAttribute("listHang", listHang);
		model.addAttribute("listLop", listLop);
		model.addAttribute("isEdit", false);
		model.addAttribute("pageTitle", "Giảng Viên/Thêm Mới");
		return ("/admin/giangVien/addUpdateGiangVien");
	}

	// Xử lý yêu cầu POST để thêm mới giảng viên
	@PostMapping("/add")
	public String createGiangVien(@ModelAttribute("giangVien") GiangVien giangVien,
			@RequestParam(value = "lopHocID", required = false) Integer lopHocID, RedirectAttributes redirectAttributes,
			@RequestParam(value = "hangID", required = false) Integer hangID, Authentication authentication) {

		try {
			// Kiểm tra email trùng lặp
			if (giangVienService.isEmailExists(giangVien.getEmailGV())) {
				redirectAttributes.addFlashAttribute("error",
						"Email này đã được sử dụng. Vui lòng sử dụng email khác.");
				return "redirect:/admin/giang_vien/them_moi";
			}

			// Kiểm tra số điện thoại trùng lặp
			if (giangVienService.isPhoneExists(giangVien.getSdtGV())) {
				redirectAttributes.addFlashAttribute("error",
						"Số điện thoại này đã được sử dụng. Vui lòng sử dụng số điện thoại khác.");
				return "redirect:/admin/giang_vien/them_moi";
			}

			String nguoiThucHien = authentication.getName();
			if (hangID != null) {
				Hang hang = hangService.findByHangID(hangID)
						.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hạng với ID: " + hangID));
				giangVien.setHang(hang);
			}
			giangVienService.createGiangVien(giangVien, lopHocID, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Thêm giảng viên thành công");
			return "redirect:/admin/giang_vien";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/admin/giang_vien/them_moi";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình xử lý");
			e.printStackTrace();
			return "redirect:/admin/giang_vien/them_moi";
		}
	}

}