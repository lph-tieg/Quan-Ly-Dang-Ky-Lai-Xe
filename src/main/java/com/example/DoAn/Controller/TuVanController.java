package com.example.DoAn.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.HocVienTuVan;
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
@RequestMapping("/admin/tu_van")
public class TuVanController {
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
	 * Hiển thị danh sách tư vấn Các chức năng lọc, tìm kiếm
	 */
	@GetMapping("")
	public String getAllTuVan(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "hangDK", required = false) String hangDK, Model model,
			Authentication authentication) {

		try {
			Page<HocVienTuVan> listTuVan;

			// Xử lý tìm kiếm và lọc kết hợp
			if (keyword != null && !keyword.isEmpty() || hangDK != null) {
				List<HocVienTuVan> filteredList = tuVanService.findAllTuVan();

				// Lọc theo hạng nếu có
				if (hangDK != null && !hangDK.isEmpty()) {
					filteredList = filteredList.stream().filter(tv -> hangDK.equals(tv.getHangDK()))
							.collect(Collectors.toList());
				}

				// Lọc theo keyword nếu có
				if (keyword != null && !keyword.isEmpty()) {
					filteredList = filteredList.stream()
							.filter(tv -> tv.getName().toLowerCase().contains(keyword.toLowerCase())
									|| tv.getEmail().toLowerCase().contains(keyword.toLowerCase())
									|| tv.getSdt().contains(keyword))
							.collect(Collectors.toList());
				}

				// Thông báo nếu không tìm thấy kết quả
				if (filteredList.isEmpty()) {
					StringBuilder message = new StringBuilder("Không tìm thấy kết quả");
					if (keyword != null && !keyword.isEmpty()) {
						message.append(" với từ khóa: ").append(keyword);
					}
					if (hangDK != null && !hangDK.isEmpty()) {
						message.append(keyword != null && !keyword.isEmpty() ? " và" : "");
						message.append(" hạng đã chọn");
					}
					model.addAttribute("message", message.toString());
				}

				// Phân trang
				int pageSize = 5;
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<HocVienTuVan> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listTuVan = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			} else {
				// Hiển thị tất cả dữ liệu nếu không lọc hoặc tìm kiếm
				listTuVan = tuVanService.findAllTuVan(pageNo);
			}

			// Xử lý thông tin người dùng
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm các giá trị đã chọn vào model
			model.addAttribute("selectedHang", hangDK);

			// Thêm thông tin phân trang và dữ liệu vào model
			model.addAttribute("totalPage", listTuVan.getTotalPages());
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("listTuVan", listTuVan);
			model.addAttribute("keyword", keyword);
			model.addAttribute("pageTitle", "Danh Sách Học Viên Tư Vấn");

			return "/admin/tuVan/tuVan";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách tư vấn: " + e.getMessage());
			return "/admin/tuVan/tuVan";
		}
	}

	// Xoá học viên tư vấn
	@GetMapping("/delete_tu_van/{id}")
	public String deleteTuVan(@PathVariable("id") Integer tuVanID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			Optional<HocVienTuVan> hocVienTuVan = tuVanService.findByID(tuVanID);
			if (hocVienTuVan.isPresent()) {
				tuVanService.deleteTuVan(tuVanID);

				// Thêm vào lịch sử hoạt động
				String nguoiThucHien = authentication.getName();
				lichSuService.themLichSu(nguoiThucHien, "Xoá", "Tư Vấn",
						String.format("Đã xóa thông tin tư vấn của học viên %s", hocVienTuVan.get().getName()));

				redirectAttributes.addFlashAttribute("success", "Xóa thông tin tư vấn thành công");
			} else {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin tư vấn");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa thông tin tư vấn");
		}
		return "redirect:/admin/tu_van";
	}

	// Xử lý Post cho xác nhận trạng thái tư vấn
	@PostMapping("/xac-nhan-tu-van/{id}")
	@ResponseBody
	public ResponseEntity<?> xacNhanTuVan(@PathVariable("id") Integer tuVanID, Authentication authentication) {
		try {
			Optional<HocVienTuVan> hocVienTuVanOpt = tuVanService.findByID(tuVanID);
			if (hocVienTuVanOpt.isPresent()) {
				HocVienTuVan hocVienTuVan = hocVienTuVanOpt.get();
				if (!hocVienTuVan.isTrangThai()) {
					hocVienTuVan.setTrangThai(true);
					hocVienTuVan.setNgayTuVan(new Date());
					tuVanService.addTuVan(hocVienTuVan);

					// Thêm vào lịch sử hoạt động
					String nguoiThucHien = authentication.getName();
					lichSuService.themLichSu(nguoiThucHien, "Xác Nhận", "Tư Vấn", String.format(
							"Đã tư vấn cho học viên %s - SĐT: %s", hocVienTuVan.getName(), hocVienTuVan.getSdt()));

					Map<String, String> response = new HashMap<>();
					response.put("message", "Xác nhận tư vấn thành công");
					response.put("ngayTuVan",
							new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(hocVienTuVan.getNgayTuVan()));
					return ResponseEntity.ok(response);
				}
				return ResponseEntity.badRequest().body("Học viên này đã được tư vấn");
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Lỗi khi xác nhận tư vấn: " + e.getMessage());
		}
	}

}