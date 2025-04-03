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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Model.DanhSachDangKy;
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
@RequestMapping("/admin/danh_sach_dang_ky")
public class DanhSachDKController {
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
	 * Hiển thị danh sách người dùng đăng ký Các chức năng tìm kiếm, lọc
	 */
	@GetMapping("")
	public String GetAllDanhSach(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "hang", required = false) String hang,
			@RequestParam(value = "buoiHoc", required = false) String buoiHoc,
			@RequestParam(value = "lichHoc", required = false) String lichHoc,
			@RequestParam(value = "thucHanh", required = false) String thucHanh,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Authentication authentication) {
		try {
			Page<DanhSachDangKy> listHocVienDK;
			int pageSize = 5; // Kích thước mỗi trang: 5

			if (keyword != null && !keyword.isEmpty()) {
				// Tìm theo họ tên
				Page<DanhSachDangKy> nameResults = dangKyService.findByHoTenContainingIgnoreCase(keyword,
						PageRequest.of(pageNo - 1, pageSize));

				// Nếu không tìm thấy theo tên, tìm theo email
				if (nameResults.isEmpty()) {
					nameResults = dangKyService.findByEmailContainingIgnoreCase(keyword,
							PageRequest.of(pageNo - 1, pageSize));
				}

				listHocVienDK = nameResults;

				if (listHocVienDK.isEmpty()) {
					model.addAttribute("message", "Không tìm thấy học viên nào phù hợp với từ khóa: " + keyword);
				}

				// Lọc theo các tiêu chí (hạng, buổi học, lịch học, loại thực hành)
			} else if (hang != null || buoiHoc != null || lichHoc != null || thucHanh != null) {
				// Xử lý lọc theo các tiêu chí
				List<DanhSachDangKy> filteredList = dangKyService.findAllDanhSach();

				if (hang != null && !hang.isEmpty()) {
					filteredList = filteredList.stream().filter(dk -> hang.equals(dk.getHangDK()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào thuộc hạng " + hang);
					}
				}

				if (buoiHoc != null && !buoiHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(dk -> buoiHoc.equals(dk.getBuoiHoc()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào học vào buổi " + buoiHoc);
					}
				}

				if (lichHoc != null && !lichHoc.isEmpty()) {
					filteredList = filteredList.stream().filter(dk -> lichHoc.equals(dk.getLichHoc()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào có lịch học " + lichHoc);
					}
				}

				if (thucHanh != null && !thucHanh.isEmpty()) {
					filteredList = filteredList.stream().filter(dk -> thucHanh.equals(dk.getLoaiThucHanh()))
							.collect(Collectors.toList());
					if (filteredList.isEmpty()) {
						model.addAttribute("message", "Không tìm thấy học viên nào có loại thực hành " + thucHanh);
					}
				}

				// Phân trang cho danh sách đã lọc
				int start = (pageNo - 1) * pageSize;
				int end = Math.min(start + pageSize, filteredList.size());
				int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
				if (pageNo > totalPages && totalPages > 0) {
					pageNo = totalPages;
					start = (pageNo - 1) * pageSize;
					end = Math.min(start + pageSize, filteredList.size());
				}

				List<DanhSachDangKy> pageContent = start < filteredList.size() ? filteredList.subList(start, end)
						: new ArrayList<>();

				listHocVienDK = new PageImpl<>(pageContent, PageRequest.of(pageNo - 1, pageSize), filteredList.size());
			} else {
				// Nếu không có lọc hoặc tìm kiếm, lấy toàn bộ danh sách phân trang
				listHocVienDK = dangKyService.findAllListDS(pageNo);
			}

			// Lấy thông tin người dùng hiện tại (admin)
			String userName = authentication.getName();
			Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
			if (adminOptional.isPresent()) {
				AdminAccount admin = adminOptional.get();
				model.addAttribute("userName", admin.getUserName());
			}

			// Thêm danh sách hạng cho dropdown
			List<Hang> listHang = hangService.findAll();
			model.addAttribute("listHang", listHang);

			// Lưu các giá trị đã chọn để hiển thị lại trên giao diện
			model.addAttribute("selectedHang", hang);
			model.addAttribute("selectedBuoiHoc", buoiHoc);
			model.addAttribute("selectedLichHoc", lichHoc);
			model.addAttribute("selectedThucHanh", thucHanh);

			// Truyền dữ liệu sang view
			model.addAttribute("pageHocVienDK", listHocVienDK);
			model.addAttribute("pageTitle", "Danh Sách Học Viên Đăng Ký");
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPage", listHocVienDK.getTotalPages());
			model.addAttribute("keyword", keyword);

			return "admin/danhSachDK/danhSachDangKy";
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách đăng ký: " + e.getMessage());
			return "admin/danhSachDK/danhSachDangKy";
		}
	}

	// Hiển thị thông tin chi tiết của một học viên đăng ký dựa trên ID
	@GetMapping("/{id}")
	public String getID(@PathVariable("id") Integer hocVienDKId, Model model, Authentication authentication) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			model.addAttribute("userName", admin.getUserName());
		}
		DanhSachDangKy hocVienDk = dangKyService.findById(hocVienDKId);
		if (hocVienDk == null) {
			throw new RuntimeException("Không tìm tháy học viên đăng ký với ID: " + hocVienDKId);

		}

		model.addAttribute("hocVienDk", hocVienDk);
		model.addAttribute("pageTitle", "Thông tin chi tiết học viên");
		return "admin/danhSachDK/hocVienDKDetails";
	}

	// Hiển thị giao diện chọn lớp học cho học viên đăng ký
	@GetMapping("/chon_lop/{id}")
	public String chonLop(@PathVariable("id") Integer hocVienDKId, Model model, Authentication authentication,
			RedirectAttributes redirectAttributes) {
		String userName = authentication.getName();
		Optional<AdminAccount> adminOptional = adminService.findByUserName(userName);
		adminOptional.ifPresent(admin -> model.addAttribute("userName", admin.getUserName()));

		DanhSachDangKy hocVienDk = dangKyService.findById(hocVienDKId);
		if (hocVienDk == null) {
			throw new RuntimeException("Không tìm thấy học viên đăng ký với ID: " + hocVienDKId);
		}

		// Lấy thông tin để lọc danh sách lớp học phù hợp
		String hangDK = hocVienDk.getHangDK();
		String buoiHoc = hocVienDk.getBuoiHoc();
		String lichHoc = hocVienDk.getLichHoc();
		String giangVienDK = hocVienDk.getGVChinh(); // Lấy giảng viên chính của học viên đăng ký

		System.out.println("Hạng đăng ký: " + hangDK + ", Giảng viên: " + giangVienDK);

		// Lọc danh sách lớp học dựa trên hạng, buổi học và lịch học
		List<LopHoc> filterLopHoc = lopHocService.findAllLopHoc().stream()
				.filter(lopHoc -> hangDK.equals(lopHoc.getHang().toString()) // Lọc theo hạng
						&& buoiHoc.equals(lopHoc.getBuoiHoc()) // Lọc theo buổi học
						&& lichHoc.equals(lopHoc.getLichHoc()) // Lọc theo lịch học
				).collect(Collectors.toList());

		if (filterLopHoc.isEmpty()) {
			System.out.println("Không tìm thấy lớp học phù hợp");
		}

		model.addAttribute("GVChinh", giangVienDK);
		model.addAttribute("listLopHoc", filterLopHoc);
		model.addAttribute("hocVienDk", hocVienDk);
		model.addAttribute("pageTitle", "Sắp xếp lớp học");

		return "/admin/danhSachDK/phanLop";
	}

	// Xử lý yêu cầu POST để phân lớp cho học viê
	@Transactional
	@PostMapping("/chon_lop")
	public String chonLop(@RequestParam(value = "hocVienID") Integer hocVienID,
			@RequestParam("lopHocID") Integer lopHocID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			System.out.println("=== BẮT ĐẦU QUÁ TRÌNH PHÂN LỚP ===");
			System.out.println("Dữ liệu nhận được - ID học viên: " + hocVienID + ", ID lớp: " + lopHocID);

			// 1. Tìm thông tin học viên đăng ký
			DanhSachDangKy hocVienDK = dangKyService.findById(hocVienID);
			if (hocVienDK == null) {
				throw new RuntimeException("Không tìm thấy học viên đăng ký với ID: " + hocVienID);
			}
			System.out.println("1. Tìm thấy học viên đăng ký: " + hocVienDK.getHoTen());

			// 2. Tìm thông tin lớp học
			LopHoc lopHoc = lopHocService.findByLopHocID(lopHocID);
			if (lopHoc == null) {
				throw new RuntimeException("Không tìm thấy lớp học với ID: " + lopHocID);
			}
			System.out.println("2. Tìm thấy lớp học: " + lopHoc.getTenLop());

			// 3. Tìm thông tin giảng viên
			List<GiangVien> giangViens = giangVienService
					.findByHoTenGVContainingIgnoreCase(hocVienDK.getGVChinh(), PageRequest.of(0, 1)).getContent();

			if (giangViens.isEmpty()) {
				throw new RuntimeException("Không tìm thấy giảng viên: " + hocVienDK.getGVChinh());
			}
			GiangVien giangVienChinh = giangViens.get(0); // Lấy giảng viên đầu tiên tìm thấy

			String nguoiThucHien = authentication.getName();

			// 4. Tạo đối tượng học viên mới
			System.out.println("4. Bắt đầu tạo học viên mới");
			HocVien hocVien = new HocVien();
			hocVien.setHoTen(hocVienDK.getHoTen());
			hocVien.setSdt(hocVienDK.getSdt());
			hocVien.setDiaChi(hocVienDK.getDiaChi());
			hocVien.setEmail(hocVienDK.getEmail());
			hocVien.setHangDK(hocVienDK.getHangDK());
			hocVien.setBuoiHoc(hocVienDK.getBuoiHoc());
			hocVien.setLichHoc(hocVienDK.getLichHoc());
			hocVien.setLoaiXeDK(hocVienDK.getLoaiXeDK());
			hocVien.setLoaiThucHanh(hocVienDK.getLoaiThucHanh());
			hocVien.setGiangVienChinh(giangVienChinh);

			// 5. Cập nhật lớp học trước khi lưu học viên
			System.out.println("5. Cập nhật thông tin lớp học");
			boolean giangVienDaTonTai = lopHoc.getListGiangVien().stream()
					.anyMatch(gv -> gv.getGiangVienID().equals(giangVienChinh.getGiangVienID()));
			if (!giangVienDaTonTai) {
				System.out.println("   Thêm giảng viên " + giangVienChinh.getHoTenGV() + " vào lớp");
				lopHoc.getListGiangVien().add(giangVienChinh);
			}
			lopHoc.tangSoLuong();

			// 6. Lưu cập nhật lớp học
			try {
				lopHocService.updateLopHoc(lopHoc,
						lopHoc.getListGiangVien().stream().map(gv -> gv.getGiangVienID()).collect(Collectors.toList()),
						nguoiThucHien);
				System.out.println("   Đã cập nhật thông tin lớp học");
			} catch (Exception e) {
				throw new RuntimeException("Không thể cập nhật thông tin lớp học: " + e.getMessage());
			}

			// 7. Gán lớp học cho học viên và lưu học viên
			hocVien.addLopHoc(lopHoc);
			HocVien savedHocVien = hocVienService.createHocVien(hocVien, nguoiThucHien);
			if (savedHocVien == null || savedHocVien.getHocVienID() == null) {
				throw new RuntimeException("Không thể lưu thông tin học viên mới");
			}
			System.out.println("   Đã lưu học viên mới với ID: " + savedHocVien.getHocVienID());

			// 8. Xóa thông tin đăng ký sau khi phân lớp thành công
			dangKyService.deleteHocVienDK(hocVienID);
			System.out.println("   Đã xóa thông tin đăng ký");

			System.out.println("=== HOÀN TẤT QUÁ TRÌNH PHÂN LỚP ===");
			redirectAttributes.addFlashAttribute("success",
					"Phân lớp thành công cho học viên: " + hocVienDK.getHoTen());
			return "redirect:/admin/danh_sach_dang_ky";

		} catch (Exception e) {
			System.out.println("=== LỖI TRONG QUÁ TRÌNH PHÂN LỚP ===");
			System.out.println("Chi tiết lỗi: " + e.getMessage());
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi phân lớp: " + e.getMessage());
			return "redirect:/admin/danh_sach_dang_ky";
		}
	}

	// Xử lý yêu cầu GET để xóa học viên đăng ký
	@GetMapping("/delete/{id}")
	public String deleteHocVien(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			// Call your service to delete the student
			dangKyService.deleteHocVienDK(id);
			redirectAttributes.addFlashAttribute("success", "Xoá học viên thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xoá học viên. Vui lòng thử lại!");
		}
		return "redirect:/admin/danh_sach_dang_ky";
	}

}
