package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.KhoaHoc;
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
@RequestMapping("/admin")
public class AdminController {

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
//	@GetMapping
//	public String redirectToDashboard() {
//		return "redirect:/admin/login";
//	}

	/**
	 * Kiểm tra trạng thái đăng nhập của admin Đăng nhập thành công thì chuyển đến
	 * trang dashboard Đăng nhập thất bại thì chuyển đến trang login
	 */
	@GetMapping
	public String defaultAdminPage(Authentication authentication) {
		// Kiểm tra nếu người dùng đã đăng nhập
		if (authentication != null && authentication.isAuthenticated()
				&& authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			// Nếu đã đăng nhập và có quyền ADMIN, chuyển đến dashboard
			return "redirect:/admin/dashboard";
		}
		// Nếu chưa đăng nhập, chuyển đến trang login
		return "redirect:/admin/login";
	}

	/**
	 * Hiển thị trang Dashboard với các thống kê về: Số lượng học viên, giảng viên,
	 * xe, lớp học Phân bố học viên, giảng viên, lớp học, xe theo hạng bằng lái Hiển
	 * thị lịch sử hoạt động của hệ thống
	 */

	@GetMapping("/dashboard")
	public String getAdmin(Model model, Authentication authentication, HttpSession session) {

		// Lấy danh sách dữ liệu từ các bảng
		List<HocVien> listHocVien = hocVienService.findAllHocVienList();
		List<GiangVien> listGiangVien = giangVienService.findAllGiangVien();
		List<XeTapLai> listXe = xeService.findAllXe();
		List<KhoaHoc> listKhoaHoc = khoaHocService.findAllKhoaHoc();
		List<LopHoc> listLopHoc = lopHocService.findAllLopHoc();

		// Lấy tất cả các hạng từ database
		List<Hang> allHangs = hangService.findAll();

		// Tạo map để đếm số học viên, giảng viên, lớp học và xe cho mỗi hạng bằng lái
		Map<String, Long> statsHocVienTheoHang = new HashMap<>();
		Map<String, Long> statsGiangVienTheoHang = new HashMap<>();
		Map<String, Long> statsLopHocTheoHang = new HashMap<>();
		Map<String, Long> statsXeTheoHang = new HashMap<>();

		// Khởi tạo số lượng là 0 cho tất cả các hạng bằng lái
		for (Hang hang : allHangs) {
			statsHocVienTheoHang.put(hang.getTenHang(), 0L);
			statsGiangVienTheoHang.put(hang.getTenHang(), 0L);
		}

		// Đếm số học viên cho mỗi hạng
		for (HocVien hv : listHocVien) {
			if (hv.getHangDK() != null) {
				String hangDK = hv.getHangDK();
				statsHocVienTheoHang.merge(hangDK, 1L, Long::sum);
			}
		}

		// Đếm số giảng viên cho mỗi hạng
		for (GiangVien gv : listGiangVien) {
			if (gv.getHang() != null) {
				String tenHang = gv.getHang().getTenHang();
				statsGiangVienTheoHang.merge(tenHang, 1L, Long::sum);
			}
		}

		// Đếm số lớp học cho mỗi hạng
		for (LopHoc lop : listLopHoc) {
			if (lop.getHang() != null) {
				String tenHang = lop.getHang().getTenHang();
				statsLopHocTheoHang.merge(tenHang, 1L, Long::sum);
			}
		}

		// Đếm số xe cho mỗi hạng
		for (XeTapLai xe : listXe) {
			if (xe.getHang() != null) {
				String tenHang = xe.getHang().getTenHang();
				statsXeTheoHang.merge(tenHang, 1L, Long::sum);
			}
		}

		// Chuyển dữ liệu từ Map sang List để hiển thị trên biểu đồ
		List<String> hangLabels = new ArrayList<>(statsHocVienTheoHang.keySet());
		List<Long> hangData = new ArrayList<>(statsHocVienTheoHang.values());

		// Đảm bảo dữ liệu giảng viên theo đúng thứ tự của labels
		List<Long> giangVienData = new ArrayList<>();
		for (String hang : hangLabels) {
			giangVienData.add(statsGiangVienTheoHang.get(hang));
		}

		// Đảm bảo dữ liệu lớp học theo đúng thứ tự của labels
		List<Long> lopHocData = new ArrayList<>();
		for (String hang : hangLabels) {
			lopHocData.add(statsLopHocTheoHang.get(hang));
		}

		// Đảm bảo dữ liệu xe theo đúng thứ tự của labels
		List<Long> xeData = new ArrayList<>();
		for (String hang : hangLabels) {
			xeData.add(statsXeTheoHang.get(hang));
		}

		// Thêm dữ liệu vào model để hiển thị trên biểu đồ
		model.addAttribute("hangLabels", hangLabels);
		model.addAttribute("hangData", hangData);
		model.addAttribute("giangVienData", giangVienData);
		model.addAttribute("lopHocData", lopHocData);
		model.addAttribute("xeData", xeData);

		// Lấy thông tin admin đang đăng nhập
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		// Thêm tổng số lượng vào model
		model.addAttribute("totalLopHoc", listLopHoc.size());
		model.addAttribute("totalKhoaHoc", listKhoaHoc.size());
		model.addAttribute("totalHocVien", listHocVien.size());
		model.addAttribute("totalGiangVien", listGiangVien.size());
		model.addAttribute("totalXe", listXe.size());

		// Kiểm tra và thêm thông báo thành công vào model
		Boolean showSuccessMessage = (Boolean) session.getAttribute("showSuccessMessage");
		if (showSuccessMessage != null && showSuccessMessage) {
			model.addAttribute("success", session.getAttribute("successMessage"));
			// Xóa thông báo sau khi hiển thị để không hiển thị lại khi refresh trang
			session.removeAttribute("showSuccessMessage");
			session.removeAttribute("successMessage");
		}
		// Thêm lịch sử hoạt động vào model
		model.addAttribute("lichSu", lichSuService.findAllLichSuHoatDong());
		model.addAttribute("pageTitle", "Dashboard");
		return "admin/dashboard/dashboard";
	}

//	profile admin

	/**
	 * Hiển thị trang thông tin admin
	 */
	@GetMapping("/profile")
	public String profileAdmin(Model model, Authentication authentication) {
		// Lấy thông tin admin từ username
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
			model.addAttribute("admin", admin);
		}
		model.addAttribute("pageTitle", "Thông Tin Admin");
		return "/admin/profile/admin";
	}

	/**
	 * Cập nhật thông tin cá nhân của Admin Cập nhật email, sdt. Cập nhật mật khẩu
	 * nếu có
	 */
	@PostMapping("/profile/update")
	public String updateAdmin(@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestParam(value = "confirmPassword", required = false) String confirmPassword,
			@ModelAttribute AdminAccount admin, RedirectAttributes redirectAttributes, HttpSession session) {
		// Tìm thông tin admin hiện tại
		Optional<AdminAccount> exAdminOpt = adminService.findByUserName(admin.getUserName());

		if (!exAdminOpt.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin admin!");
			return "redirect:/admin/profile";
		}

		AdminAccount exAdmin = exAdminOpt.get();

		// Kiểm tra và cập nhật mật khẩu nếu có
		if (newPassword != null && !newPassword.isEmpty() && confirmPassword != null && !confirmPassword.isEmpty()) {
			if (!newPassword.equals(confirmPassword)) {
				redirectAttributes.addFlashAttribute("error", "Mật khẩu không trùng khớp");
				return "redirect:/admin/profile";
			}
			// Mã hóa mật khẩu mới trước khi lưu
			exAdmin.setPassword(passwordEncoder.encode(newPassword));
		}
		// Cập nhật thông tin cá nhân
		exAdmin.setEmail(admin.getEmail());
		exAdmin.setPhone(admin.getPhone());
		adminService.saveAdmin(exAdmin);

		// Lưu thông báo vào session
		session.setAttribute("showSuccessMessage", true);
		session.setAttribute("successMessage", "Cập nhật thông tin thành công");

		return "redirect:/admin/dashboard";
	}

	/**
	 * Xoá toàn bộ lịch sử hoạt động của hệ thống
	 */
	@PostMapping("/lich_su/delete-all")
	@ResponseBody
	public ResponseEntity<?> deleteAll(Authentication authentication) {
		try {
			// Lấy tên người thực hiện để ghi vào log
			String nguoiThucHien = authentication.getName();
			lichSuService.deleteAll(nguoiThucHien);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			// Trả về lỗi server nếu có vấn đề xảy ra
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
