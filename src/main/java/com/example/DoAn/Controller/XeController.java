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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.XeTapLai;
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
@RequestMapping("/admin/xe_tap_lai")
public class XeController {
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
	 * Hiển thị danh sách xe Các chức năng lọc, tìm kiếm
	 */
	@GetMapping("")
	public String getAllXe(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "hang", required = false) Integer hangID,
			@RequestParam(value = "loaiSoXe", required = false) String loaiSoXe,
			@RequestParam(value = "loaiXe", required = false) String loaiXe,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Authentication authentication) {
		try {
			Page<XeTapLai> listXe;
			int pageSize = 5;
			List<XeTapLai> filteredList = new ArrayList<>();

			// Lấy tất cả xe
			List<XeTapLai> allXe = xeService.findAllXe();

			// Áp dụng các bộ lọc
			for (XeTapLai xe : allXe) {
				boolean matchesKeyword = true;
				boolean matchesHang = true;
				boolean matchesLoaiSo = true;
				boolean matchesLoaiXe = true;

				// Lọc theo từ khóa
				if (keyword != null && !keyword.isEmpty()) {
					matchesKeyword = xe.getTenXe().toLowerCase().contains(keyword.toLowerCase())
							|| xe.getLoaiXe().toLowerCase().contains(keyword.toLowerCase());
				}

				// Lọc theo hạng
				if (hangID != null) {
					matchesHang = xe.getHang().getHangID().equals(hangID);
				}

				// Lọc theo loại số xe
				if (loaiSoXe != null && !loaiSoXe.isEmpty()) {
					matchesLoaiSo = xe.getLoaiSoXe().equals(loaiSoXe);
				}

				// Lọc theo loại xe
				if (loaiXe != null && !loaiXe.isEmpty()) {
					matchesLoaiXe = xe.getLoaiXe().equals(loaiXe);
				}

				if (matchesKeyword && matchesHang && matchesLoaiSo && matchesLoaiXe) {
					filteredList.add(xe);
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

			List<XeTapLai> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
					: new ArrayList<>();
			listXe = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());

			// Lấy danh sách hạng và thông tin người dùng
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm danh sách vào model
			List<Hang> listHang = hangService.findAllOrderByTenHang();
			model.addAttribute("listHang", listHang);
			model.addAttribute("selectedHang", hangID);
			model.addAttribute("selectedLoaiXe", loaiXe);
			model.addAttribute("selectedLoaiSoXe", loaiSoXe);
			model.addAttribute("totalPage", listXe.getTotalPages());
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("listXeTapLai", listXe);
			model.addAttribute("keyword", keyword);
			model.addAttribute("pageTitle", "Danh Sách Xe Tập Lái");

			if (filteredList.isEmpty() && (keyword != null || hangID != null || loaiSoXe != null || loaiXe != null)) {
				model.addAttribute("message", "Không tìm thấy xe nào phù hợp với điều kiện tìm kiếm");
			}

			return "/admin/xeTapLai/xe";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách xe: " + e.getMessage());
			return "/admin/xeTapLai/xe";
		}
	}

	// Xoá xe
	@GetMapping("/delete_xe/{id}")
	public String deleteXe(@PathVariable("id") Integer xeID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			XeTapLai xe = xeService.findByID(xeID);
			if (xe != null) {
				String nguoiThucHien = authentication.getName();
				xeService.deleteXe(xeID, nguoiThucHien);
				redirectAttributes.addFlashAttribute("success", "Xóa xe " + xe.getTenXe() + " thành công!");
			} else {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy xe cần xóa!");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Xóa xe thất bại: " + e.getMessage());
		}
		return "redirect:/admin/xe_tap_lai";
	}

	// Tìm xe theo ID
	@GetMapping("/{id}")
	public String getUpdateXe(@PathVariable("id") Integer xeID, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		// Thêm danh sách hạng vào model
		List<Hang> listHang = hangService.findAll();
		model.addAttribute("listHang", listHang);

		model.addAttribute("xeTapLai", xeService.findByID(xeID));
		model.addAttribute("titlePage", "Xe Tập Lái/Cập Nhật");
		model.addAttribute("isEdit", true);
		return ("/admin/xeTapLai/addUpdateXe");
	}

	// Xử lý Post cho cập nhật xe
	@PostMapping("/cap_nhat")
	public String saveUpdateXe(@ModelAttribute("xeTapLai") XeTapLai xeTapLai, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			XeTapLai existingXe = xeService.findByID(xeTapLai.getXeID());
			if (existingXe != null) {
				// Tính toán số lượng còn lại dựa trên sự thay đổi số lượng
				int soLuongMoi = xeTapLai.getSoLuong();
				int soLuongCu = existingXe.getSoLuong();
				int soLuongConLaiCu = existingXe.getSoLuongConLai();

				// Tính toán số lượng còn lại mới
				int soLuongConLaiMoi = soLuongConLaiCu + (soLuongMoi - soLuongCu);
				xeTapLai.setSoLuongConLai(soLuongConLaiMoi);
				String nguoiThucHien = authentication.getName();
				xeService.updateXe(xeTapLai, nguoiThucHien);
				redirectAttributes.addFlashAttribute("success", "Cập nhật xe " + xeTapLai.getTenXe() + " thành công!");
			} else {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy xe cần cập nhật!");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Cập nhật xe thất bại: " + e.getMessage());
		}
		return "redirect:/admin/xe_tap_lai";
	}

	// Thêm mới xe
	@GetMapping("/them_moi")
	public String getAddXe(Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}

		// Thêm danh sách hạng vào model
		List<Hang> listHang = hangService.findAll();
		model.addAttribute("listHang", listHang);

		XeTapLai newXe = new XeTapLai();
		model.addAttribute("xeTapLai", newXe);
		model.addAttribute("isEdit", false);
		model.addAttribute("pageTitle", "Xe Tập Lái/Thêm Mới");
		return ("/admin/xeTapLai/addUpdateXe");
	}

	// Xử lý Post cho thêm mới xe
	@PostMapping("/them_moi")
	public String addXe(@ModelAttribute("xeTapLai") XeTapLai xeTapLai, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			// Khi thêm xe mới, số lượng còn lại bằng số lượng ban đầu
			String nguoiThucHien = authentication.getName();
			xeTapLai.setSoLuongConLai(xeTapLai.getSoLuong());
			xeService.createXe(xeTapLai, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Thêm xe " + xeTapLai.getTenXe() + " thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Thêm xe thất bại: " + e.getMessage());
		}
		return "redirect:/admin/xe_tap_lai";
	}
}
