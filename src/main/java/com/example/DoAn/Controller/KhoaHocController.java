package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.KhoaHoc;
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
@RequestMapping("/admin/khoa_hoc")
public class KhoaHocController {
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
	 * Hiển thị danh sách khoá học Phân trang
	 */
	@GetMapping("")
	public String getAllKhoaHoc(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Authentication authentication) {
		try {
			Page<KhoaHoc> listKhoaHoc;
			int pageSize = 5;

			if (keyword != null && !keyword.isEmpty()) {
				List<KhoaHoc> filteredList = new ArrayList<>();

				// Tìm theo tên khóa học
				Page<KhoaHoc> nameResults = khoaHocService.findByTenKhoaHocContainingIgnoreCase(keyword,
						PageRequest.of(0, Integer.MAX_VALUE));
				filteredList.addAll(nameResults.getContent());

				if (filteredList.isEmpty()) {
					model.addAttribute("message", "Không tìm thấy khóa học nào phù hợp với từ khóa: " + keyword);
				}

				// Tính toán phân trang cho kết quả tìm kiếm
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());

				// Kiểm tra và điều chỉnh pageNo
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<KhoaHoc> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listKhoaHoc = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			} else {
				listKhoaHoc = khoaHocService.findAllKhoaHocList(pageNo);
			}

			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			model.addAttribute("totalPage", listKhoaHoc.getTotalPages());
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("listKhoaHoc", listKhoaHoc);
			model.addAttribute("keyword", keyword);
			model.addAttribute("pageTitle", "Danh Sách Khóa Học");

			return "/admin/khoaHoc/khoaHoc";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách khóa học: " + e.getMessage());
			return "/admin/khoaHoc/khoaHoc";
		}
	}

	// Lấy ID của khoá học
	@GetMapping("/{id}")
	public String getKhoaHocByID(@PathVariable("id") Integer khoaHocID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		KhoaHoc khoaHoc = khoaHocService.findByIDKhoaHoc(khoaHocID);
		if (khoaHoc == null) {
			model.addAttribute("error", "Khoá Học không tồn tại");
			return "redirect:/admin/khoa_hoc";
		}
		model.addAttribute("khoaHoc", khoaHoc);
		model.addAttribute("listKhoaHoc", khoaHocService.findAllKhoaHoc());
		model.addAttribute("isEdit", true);
		model.addAttribute("titlePage", "Khóa Học/Cập Nhật");
		return ("/admin/khoaHoc/updateKhoaHoc");

	}

	// Xử lý Post khi cập nhật
	@PostMapping("/cap_nhat")
	public String updateKhoaHoc(@ModelAttribute("khoaHoc") KhoaHoc khoaHoc, Model model) {
		boolean isUpdating = khoaHoc.getKhoaHocID() != null;

		if (isUpdating) {
			khoaHocService.updateKhoaHoc(khoaHoc);

		} else {
			khoaHocService.createKhoaHoc(khoaHoc);

		}
		return ("redirect:/admin/khoa_hoc");
	}

	// Thêm mới khoá học
	@GetMapping("/them_moi")
	public String getAddKhoaHoc(Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}
		model.addAttribute("khoaHoc", new KhoaHoc());
		model.addAttribute("isEdit", false);
		model.addAttribute("pageTitle", "Khóa Học/Thêm Mới");
		return ("/admin/khoaHoc/updateKhoaHoc");
	}

	// Xoá khoá học
	@GetMapping("/delete_khoa_hoc/{id}")
	public String deleteKhoaHoc(@PathVariable("id") Integer khoaHocID) {

		KhoaHoc khoaHoc = khoaHocService.findByIDKhoaHoc(khoaHocID);

		if (khoaHoc != null) {
			khoaHocService.deleteKhoaHoc(khoaHocID);
		}

		return ("redirect:/admin/khoa_hoc");
	}
}
