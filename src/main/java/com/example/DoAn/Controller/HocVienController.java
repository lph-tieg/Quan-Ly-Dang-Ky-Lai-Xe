package com.example.DoAn.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.HocVien;
import com.example.DoAn.Model.LopHoc;
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
		model.addAttribute("listHang", listHang);
		model.addAttribute("listLopHoc", listLopHoc);
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

		model.addAttribute("hocVien", hocVien);
		model.addAttribute("pageTitle", "Thông tin chi tiết học viên");
		return "admin/hocVien/hocVienDetail";
	}

	// Xử lý Post khi cập nhật
	@PostMapping("/cap_nhat")
	public String getUpdateHocVien(@ModelAttribute("hocVien") HocVien hocVien,
			@RequestParam("lopHocID") Integer lopHocID, RedirectAttributes redirectAttributes,
			Authentication authentication, Model model) {
		try {
			String nguoiThucHien = authentication.getName();

			// Kiểm tra lớp học mới
			LopHoc lopHocMoi = lopHocService.findByLopHocID(lopHocID);
			if (lopHocMoi == null) {
				model.addAttribute("error", "Không tìm thấy lớp học");
				return setupUpdatePage(hocVien.getHocVienID(), model, authentication);
			}

			// Lấy thông tin học viên hiện tại từ database
			HocVien hocVienHienTai = hocVienService.findByID(hocVien.getHocVienID());
			if (hocVienHienTai == null) {
				model.addAttribute("error", "Không tìm thấy thông tin học viên");
				return setupUpdatePage(hocVien.getHocVienID(), model, authentication);
			}

			// Tạo StringBuilder để lưu các thay đổi
			StringBuilder changes = new StringBuilder();

			// Kiểm tra và ghi nhận các thay đổi
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

			// Cập nhật thông tin cơ bản
			hocVienHienTai.setHoTen(hocVien.getHoTen());
			hocVienHienTai.setEmail(hocVien.getEmail());
			hocVienHienTai.setSdt(hocVien.getSdt());
			hocVienHienTai.setDiaChi(hocVien.getDiaChi());
			hocVienHienTai.setHangDK(hocVien.getHangDK());
			hocVienHienTai.setBuoiHoc(hocVien.getBuoiHoc());
			hocVienHienTai.setLichHoc(hocVien.getLichHoc());
			hocVienHienTai.setLoaiXeDK(hocVien.getLoaiXeDK());
			hocVienHienTai.setLoaiThucHanh(hocVien.getLoaiThucHanh());
			hocVienHienTai.setGiangVienChinh(hocVien.getGiangVienChinh());

			// Cập nhật lớp học
			if (!hocVienHienTai.getLopHocs().contains(lopHocMoi)) {
				// Lưu lại các lớp học cũ để kiểm tra
				List<LopHoc> lopHocCu = new ArrayList<>(hocVienHienTai.getLopHocs());
				
				// Xóa học viên khỏi các lớp cũ và cập nhật số lượng
				for (LopHoc lop : lopHocCu) {
					hocVienHienTai.removeLopHoc(lop);
					lopHocService.updateLopHoc(lop, null, nguoiThucHien);
					changes.append("Xóa khỏi lớp: ").append(lop.getTenLop()).append("\n");
				}
				
				// Thêm lớp học mới
				hocVienHienTai.addLopHoc(lopHocMoi);
				changes.append("Thêm lớp học: ").append(lopHocMoi.getTenLop()).append("\n");
				
				// Cập nhật số lượng học viên cho lớp mới
				lopHocMoi.tangSoLuong();
				lopHocService.updateLopHoc(lopHocMoi, null, nguoiThucHien);
			}

			// Lưu thay đổi
			hocVienService.updateHocVien(hocVienHienTai, nguoiThucHien);

			redirectAttributes.addFlashAttribute("success", "Cập nhật học viên thành công");
			return "redirect:/admin/hoc_vien";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật học viên: " + e.getMessage());
			return "redirect:/admin/hoc_vien";
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
		model.addAttribute("listHang", listHang);
		model.addAttribute("listLopHoc", listLopHoc);
		model.addAttribute("hocVien", hocVienService.findByID(hocVienID));
		model.addAttribute("listKhoaHoc", khoaHocService.findAllKhoaHoc());
		model.addAttribute("listXe", xeService.findAllXe());
		model.addAttribute("pageTitle", "Học Viên/Cập Nhật");
		return "/admin/hocVien/addUpateHocVien";
	}

	// Xử lý Post khi xoá học viên
	@PostMapping("/delete/{id}")
	public String deleteHocVien(@PathVariable("id") Integer hocVienID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();
			hocVienService.deleteHocVienByID(hocVienID, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Xóa học viên thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa học viên: " + e.getMessage());
		}
		return "redirect:/admin/hoc_vien";
	}

}
