package com.example.DoAn.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DoAn.Model.DanhSachDangKy;
import com.example.DoAn.Model.GiangVien;
import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.HocVienTuVan;
import com.example.DoAn.Model.XeTapLai;
import com.example.DoAn.Service.BuoiThucHanhService;
import com.example.DoAn.Service.DangKyService;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.HangService;
import com.example.DoAn.Service.HocVienService;
import com.example.DoAn.Service.KhoaHocService;
import com.example.DoAn.Service.LichSuHoatDongService;
import com.example.DoAn.Service.TuVanService;
import com.example.DoAn.Service.XeService;

@Controller
@RequestMapping("/trung-tam-day-lai-xe-Thanh-Cong")
public class HomeController {

	@Autowired
	private TuVanService tuVanService;

	@Autowired
	private HocVienService hocVienService;

	@Autowired
	private KhoaHocService khoaHocService;

	@Autowired
	private XeService xeService;

	@Autowired
	private GiangVienService giangVienService;

	@Autowired
	private DangKyService hocVienDKService;

	@Autowired
	private BuoiThucHanhService buoiThucHanhService;

	@Autowired
	private HangService hangService;

	@Autowired
	private LichSuHoatDongService lichSuHoatDongService;

	// Trang chủ
	@GetMapping
	public String getIndex() {
		return "user/index"; // Trả về trang index mà không cần redirect
	}

	// Trang dịch vụ
	@GetMapping("/dich_vu")
	public String getService(Model model) {
		List<XeTapLai> listXe = xeService.findAllXe();
		model.addAttribute("listXe", listXe);
		return "user/service";
	}

	// Trang xe máy
	@GetMapping("/xe_may")
	public String getXeMay() {
		return "user/xeMay";
	}

	// Trang xe tải
	@GetMapping("/xe_tai")
	public String getXeTai() {
		return "user/xeTai";
	}

	// Trang xe oto
	@GetMapping("/xe_oto")
	public String getOto() {
		return "user/oTo";
	}

	// Trang tư vấn
	@GetMapping("/tu_van")
	public String getTuVan() {
		return "user/tuVan";
	}

	// Trang đăng ký
	@GetMapping("/dang_ky")
	public String getDangKy(@RequestParam(value = "lichHoc", required = false) String lichHoc,
			@RequestParam(value = "buoiThucHanh", required = false) String buoiThucHanh, Model model) {

		// Lấy tất cả các khóa học, xe và giảng viên
		List<Hang> listHang = hangService.findAll();
		List<XeTapLai> listXe = xeService.findAllXe();
		List<GiangVien> listGiangVien = giangVienService.findAllGiangVien();

		if ((lichHoc != null && !lichHoc.isEmpty()) && (buoiThucHanh != null && !buoiThucHanh.isEmpty())) {
			listGiangVien = giangVienService.findByLichDayAndBuoiDay(lichHoc, buoiThucHanh);
		} else if (lichHoc != null && !lichHoc.isEmpty()) {
			listGiangVien = giangVienService.findByLichDay(lichHoc);
		}

		// Cập nhật lại dữ liệu vào Model
		model.addAttribute("listHang", listHang);
		model.addAttribute("listXe", listXe);
		model.addAttribute("listGiangVien", listGiangVien);
		model.addAttribute("lichHoc", lichHoc);
		model.addAttribute("buoiThucHanh", buoiThucHanh);

		return "user/dangKy";
	}

	// Xử lý Post khi đăng ký tưu vấn
	@PostMapping("/dang_ky_tu_van")
	public String dangKyTuVan(@ModelAttribute("hocVienTuVan") HocVienTuVan hocVienTuVan,
			RedirectAttributes redirectAttributes) {
		try {
			// Kiểm tra số điện thoại đã tồn tại
			if (tuVanService.existsBySdt(hocVienTuVan.getSdt())) {
				redirectAttributes.addFlashAttribute("error", "Số điện thoại đã được đăng ký!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/tu_van";
			}

			// Kiểm tra email đã tồn tại
			if (tuVanService.existsByEmail(hocVienTuVan.getEmail())) {
				redirectAttributes.addFlashAttribute("error", "Email đã được đăng ký!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/tu_van";
			}

			tuVanService.addTuVan(hocVienTuVan);
			redirectAttributes.addFlashAttribute("success", "Đăng ký tư vấn thành công");
			return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/tu_van";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
			return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/tu_van";
		}
	}

//	@PostMapping("/dang_ky_khoa_hoc")
//	public String dangKyKH(@ModelAttribute("hocVien") HocVien hocVien, Model model,
//			@RequestParam(value = "buoiHoc") String buoiHoc,
//			@RequestParam(value = "buoiThucHanh", required = false) String buoiThucHanh,
//			@RequestParam(value = "loaiXeDK") Integer loaiXeDK, @RequestParam(value = "giangVien") Integer giangVienID,
//			RedirectAttributes redirectAttributes) {
//
//		GiangVien giangVien = giangVienService.findByIDGV(giangVienID);
//
//		hocVien.setNgayDKKH(new Date());
//
//		// Gán giá trị buoiThucHanh cho hocVien
//		hocVien.setThucHanh(buoiThucHanh);
//		hocVien.setGVChinh(giangVien.getHoTenGV());
//
//		// Lấy thông tin xe dựa trên ID và gán tên xe cho hocVien
//		Optional<XeTapLai> xeOptional = xeService.findByXeID(loaiXeDK);
//		if (xeOptional.isPresent()) {
//			hocVien.setLoaiXeDK(xeOptional.get().getTenXe());
//		}
//
//		hocVienService.createHocVien(hocVien);
//		redirectAttributes.addFlashAttribute("success", "Đăng ký thành công");
//		return ("redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky");
//	}

	// Xử lý Post khi đăng ký khoá học
	@PostMapping("/dang_ky_khoa_hoc")
	public String dangKyKH(@ModelAttribute("hocVienDK") DanhSachDangKy hocVienDK, Model model,
			@RequestParam(value = "lichHoc") String lichHoc,
			@RequestParam(value = "loaiThucHanh", required = false) String loaiThucHanh,
			@RequestParam(value = "buoiThucHanh", required = false) String buoiThucHanh,
			@RequestParam(value = "hang") Integer hangID, @RequestParam(value = "giangVien") Integer giangVienID,
			@RequestParam(value = "xeID") Integer xeID, RedirectAttributes redirectAttributes) {

		try {
			// Kiểm tra số điện thoại đã tồn tại
			List<DanhSachDangKy> existingPhoneList = hocVienDKService.findBySdtContaining(hocVienDK.getSdt());
			if (!existingPhoneList.isEmpty()) {
				redirectAttributes.addFlashAttribute("error", "Số điện thoại đã được đăng ký!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Kiểm tra email đã tồn tại
			List<DanhSachDangKy> existingEmailList = hocVienDKService.findByEmailContaining(hocVienDK.getEmail());
			if (!existingEmailList.isEmpty()) {
				redirectAttributes.addFlashAttribute("error", "Email đã được đăng ký!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Tìm hạng từ ID
			Hang hang = hangService.findByID(hangID);
			if (hang == null) {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy hạng được chọn!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Tìm xe theo ID đã chọn
			XeTapLai xe = xeService.findByID(xeID);
			if (xe == null) {
				redirectAttributes.addFlashAttribute("error", "Không tìm thấy xe đã chọn!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Kiểm tra xe có thuộc hạng đã chọn không
			if (!xe.getHang().getHangID().equals(hangID)) {
				redirectAttributes.addFlashAttribute("error", "Xe không thuộc hạng đã chọn!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Kiểm tra số lượng xe còn lại
			if (xe.getSoLuongConLai() <= 0) {
				redirectAttributes.addFlashAttribute("error", "Xe đã hết số lượng!");
				return "redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky";
			}

			// Giảm số lượng xe còn lại
			xe.setSoLuongConLai(xe.getSoLuongConLai() - 1);
			xeService.updateSoLuongXe(xe);

			GiangVien giangVien = giangVienService.findByIDGV(giangVienID);

			hocVienDK.setNgayDKKH(new Date());
			hocVienDK.setLoaiThucHanh(loaiThucHanh);
			hocVienDK.setBuoiHoc(buoiThucHanh);
			hocVienDK.setGVChinh(giangVien.getHoTenGV());
			hocVienDK.setLoaiXeDK(xe.getTenXe());
			hocVienDK.setHangDK(hang.getTenHang());
			hocVienDK.setHang(hang);

			hocVienDKService.addHocVienDK(hocVienDK);
			redirectAttributes.addFlashAttribute("success", "Đăng ký thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
		}
		return ("redirect:/trung-tam-day-lai-xe-Thanh-Cong/dang_ky");
	}

	// Tìm xe theo select
	@GetMapping("/filter-xe")
	@ResponseBody
	public List<Map<String, Object>> filterXe(@RequestParam(value = "hangID") Integer hangID) {
		try {
			System.out.println("Filtering vehicles for hangID: " + hangID);
			List<XeTapLai> result = xeService.findByHangID(hangID);
			System.out.println("Found " + result.size() + " vehicles");

			// Chuyển đổi kết quả thành một danh sách các map đơn giản
			return result.stream().map(xe -> {
				Map<String, Object> simplified = new HashMap<>();
				simplified.put("xeID", xe.getXeID());
				simplified.put("tenXe", xe.getTenXe());
				simplified.put("soLuongConLai", xe.getSoLuongConLai());
				return simplified;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			System.err.println("Error filtering vehicles: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// Tìm giảng viên theo select
	@GetMapping("/trung-tam-day-lai-xe-Thanh-Cong/filter-giang-vien")
	public List<GiangVien> filterGiangVien(@RequestParam(value = "buoiHoc") String buoiHoc,
			@RequestParam(value = "buoiThucHanh") String buoiThucHanh) {
		return giangVienService.findByLichDayAndBuoiDay(buoiHoc, buoiThucHanh);
	}

}