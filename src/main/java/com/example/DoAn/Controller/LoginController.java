package com.example.DoAn.Controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.DoAn.Model.AdminAccount;
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
@RequestMapping("/admin/login")
public class LoginController {
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

	// Quản lý session
	private String verificationCode;
	private static final String PHONE_VERIFIED = "PHONE_VERIFIED"; // Trạng thái xác thực số điện thoại
	private static final String CODE_VERIFIED = "CODE_VERIFIED"; // Trạng thái xác thực mã
	private static final String PHONE_NUMBER = "PHONE_NUMBER"; // Lưu số điện thoại trong session

	// Giao diện đăng nhập trang Admin
	@GetMapping("")
	public String getLoginAdmin(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("error", "Tài khoản hoặc mật khẩu không chính xác");

		}
		return ("admin/login/login");
	}

	// Xử lý Post đăng nhập
	@PostMapping("/login")
	public String getLogin(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, Model model) {
		Optional<AdminAccount> adminOptional = adminService.findByUserName(username);

		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();

			// Kiểm tra mật khẩu đăng nhập với mật khẩu đã mã hoá trong database
			if (passwordEncoder.matches(password, admin.getPassword())) {
				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				return "redirect:/admin/dashboard";
			}

		}

		model.addAttribute("error", "đăng nhập thất bại");
		return "/admin/login/login";

	}

	// Trang nhập SDT
	@GetMapping("/so_dien_thoai")
	public String getPhone() {
		// Xóa các trạng thái cũ trong session
		session.removeAttribute(PHONE_VERIFIED);
		session.removeAttribute(CODE_VERIFIED);
		session.removeAttribute(PHONE_NUMBER);
		return ("/admin/login/inputPhone");
	}

//	@PostMapping("/so_dien_thoai")
//	public String checkPhone(@RequestParam(value = "phone") String phone, Model model) {
//		Optional<AdminAccount> adminOptional = adminService.findByPhone(phone);
//		if (adminOptional.isPresent()) {
//			session.setAttribute("phone", phone);
//			return "redirect:/admin/login/so_dien_thoai/ma";
//		} else {
//			model.addAttribute("error", "Số điện thoại không tồn tại");
//			return "admin/login/inputPhone";
//		}
//	}

	// Xử lý Post cho trang nhập SDT
	@PostMapping("/so_dien_thoai")
	public String checkPhone(@RequestParam(value = "phone") String phone, Model model, HttpSession session) {
		Optional<AdminAccount> adminOptional = adminService.findByPhone(phone);
		if (adminOptional.isPresent()) {
			// Lưu trạng thái xác thực số điện thoại và số điện thoại vào session
			session.setAttribute(PHONE_VERIFIED, true);
			session.setAttribute(PHONE_NUMBER, phone);
			return "redirect:/admin/login/so_dien_thoai/ma";
		} else {
			model.addAttribute("error", "Số điện thoại không tồn tại");
			return "admin/login/inputPhone";
		}
	}

	// Giao diện trang nhập mã xác nhận
	@GetMapping("/so_dien_thoai/ma")
	public String getCode(HttpSession session, Model model) {
		// Kiểm tra xem số điện thoại đã được xác thực chưa
		Boolean isPhoneVerified = (Boolean) session.getAttribute(PHONE_VERIFIED);
		if (isPhoneVerified == null || !isPhoneVerified) {
			model.addAttribute("error", "Vui lòng nhập số điện thoại trước");
			return "redirect:/admin/login/so_dien_thoai";
		}

		// Tạo mã xác nhận mới
		verificationCode = generateRandomCode();
		System.out.println("Mã xác nhận: " + verificationCode);
		return "/admin/login/codePhone";
	}

//	@PostMapping("/so_dien_thoai/ma")
//	public String sendCode(@RequestParam("digit1") String digit1, @RequestParam("digit2") String digit2,
//			@RequestParam("digit3") String digit3, @RequestParam("digit4") String digit4, Model model) {
//		// Ghép các số lại thành một chuỗi
//		String submittedCode = digit1 + digit2 + digit3 + digit4;
//
//		// Kiểm tra mã xác nhận
//		if (submittedCode.equals(verificationCode)) {
//			return "redirect:/admin/login/cap_nhat_mat_khau";
//		} else {
//			model.addAttribute("error", "Mã xác nhận không hợp lệ");
//			return "/admin/login/codePhone";
//		}
//	}

	// Xử lý Post cho trang nhập mã xác nhận
	@PostMapping("/so_dien_thoai/ma")
	public String sendCode(@RequestParam("digit1") String digit1, @RequestParam("digit2") String digit2,
			@RequestParam("digit3") String digit3, @RequestParam("digit4") String digit4, HttpSession session,
			Model model) {
		// Kiểm tra xem số điện thoại đã được xác thực chưa
		Boolean isPhoneVerified = (Boolean) session.getAttribute(PHONE_VERIFIED);
		if (isPhoneVerified == null || !isPhoneVerified) {
			return "redirect:/admin/login/so_dien_thoai";
		}

		String submittedCode = digit1 + digit2 + digit3 + digit4;

		if (submittedCode.equals(verificationCode)) {
			// Lưu trạng thái xác thực mã
			session.setAttribute(CODE_VERIFIED, true);
			return "redirect:/admin/login/cap_nhat_mat_khau";
		} else {
			model.addAttribute("error", true);
			return "/admin/login/codePhone";
		}
	}

	// Trang cập nhật mật khẩu
	@GetMapping("/cap_nhat_mat_khau")
	public String updateMatKhau(HttpSession session, Model model) {
		// Kiểm tra cả hai điều kiện: số điện thoại và mã xác nhận
		Boolean isPhoneVerified = (Boolean) session.getAttribute(PHONE_VERIFIED);
		Boolean isCodeVerified = (Boolean) session.getAttribute(CODE_VERIFIED);

		if (isPhoneVerified == null || !isPhoneVerified || isCodeVerified == null || !isCodeVerified) {
			return "redirect:/admin/login/so_dien_thoai";
		}

		return "/admin/login/quenMK";
	}

//	@PostMapping("/cap_nhat_mat_khau")
//	public String updatePass(@RequestParam(value = "newPassword") String newPassword,
//			@RequestParam(value = "confirmPassword") String confirmPassword, Model model) {
//		if (!newPassword.equals(confirmPassword)) {
//			model.addAttribute("error", "Mật khẩu không trùng khớp");
//			return "/admin/login/quenMK";
//		}
//
//		String phone = (String) session.getAttribute("phone");
//		Optional<AdminAccount> adminOptional = adminService.findByPhone(phone);
//		if (adminOptional.isPresent()) {
//			AdminAccount admin = adminOptional.get();
//			String encodedPassword = passwordEncoder.encode(newPassword);
//			admin.setPassword(encodedPassword);
//			adminService.saveAdmin(admin);
//
//			Authentication authentication = authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(admin.getUserName(), newPassword));
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//			return "redirect:/admin/login";
//		} else {
//			model.addAttribute("error", "Cập nhật không thành công");
//			return "/admin/login/quenMK";
//		}
//	}

	// Xử lý Post cập nhật mật khẩu
	@PostMapping("/cap_nhat_mat_khau")
	public String updatePass(@RequestParam(value = "newPassword") String newPassword,
			@RequestParam(value = "confirmPassword") String confirmPassword, HttpSession session, Model model) {
		// Kiểm tra điều kiện truy cập
		Boolean isPhoneVerified = (Boolean) session.getAttribute(PHONE_VERIFIED);
		Boolean isCodeVerified = (Boolean) session.getAttribute(CODE_VERIFIED);

		if (isPhoneVerified == null || !isPhoneVerified || isCodeVerified == null || !isCodeVerified) {
			return "redirect:/admin/login/so_dien_thoai";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Mật khẩu không trùng khớp");
			return "/admin/login/quenMK";
		}

		String phone = (String) session.getAttribute(PHONE_NUMBER);
		Optional<AdminAccount> adminOptional = adminService.findByPhone(phone);

		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			String encodedPassword = passwordEncoder.encode(newPassword);
			admin.setPassword(encodedPassword);
			adminService.saveAdmin(admin);

			// Xóa tất cả các session attributes sau khi hoàn thành
			session.removeAttribute(PHONE_VERIFIED);
			session.removeAttribute(CODE_VERIFIED);
			session.removeAttribute(PHONE_NUMBER);

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(admin.getUserName(), newPassword));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return "redirect:/admin/login";
		} else {
			model.addAttribute("error", "Cập nhật không thành công");
			return "/admin/login/quenMK";
		}
	}

	// Phương thức tạo mã xác nhận ngẫu nhiên (4 số)
	private String generateRandomCode() {
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			code.append(random.nextInt(10)); // thêm số ngẫu nhiên từ o-9
		}
		return code.toString();
	}

}
