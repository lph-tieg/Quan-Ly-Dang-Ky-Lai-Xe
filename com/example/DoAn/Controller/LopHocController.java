package com.example.DoAn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class LopHocController {

	@Autowired
	private LopHocService lopHocService;

	@Autowired
	private HangService hangService;

	@Autowired
	private GiangVienService giangVienService;

	@PostMapping("/delete/{id}")
	public String deleteLopHoc(@PathVariable("id") Integer lopHocID, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			String nguoiThucHien = authentication.getName();
			lopHocService.deleteLop(lopHocID, nguoiThucHien);
			redirectAttributes.addFlashAttribute("success", "Xoá lớp học thành công");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xoá, hãy thử lại");
		}
		return "redirect:/admin/lop_hoc";
	}

	@DeleteMapping("/delete/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteLopHoc(@PathVariable("id") Integer lopHocID, 
			Authentication authentication) {
		Map<String, Object> response = new HashMap<>();
		try {
			String nguoiThucHien = authentication.getName();
			lopHocService.deleteLop(lopHocID, nguoiThucHien);
			response.put("success", true);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Không thể xoá lớp học, hãy thử lại!");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/cap_nhat")
	public String updateLopHoc(@ModelAttribute("lopHoc") LopHoc lopHoc, 
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "hangID") Integer hangID,
			@RequestParam(value = "giangVienID", required = false) List<Integer> giangVienID,
			Authentication authentication) {

		try {
			String nguoiThucHien = authentication.getName();
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

			List<GiangVien> giangViens;
			if (giangVienID != null && !giangVienID.isEmpty()) {
				giangViens = giangVienService.findAllByID(giangVienID);
			} else {
				giangViens = giangVienService.findGiangVienByLichHocBuoiHocHang(
					lopHoc.getLichHoc(), 
					lopHoc.getBuoiHoc(),
					lopHoc.getHang().getTenHang()
				);
			}
			lopHoc.setListGiangVien(giangViens);

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
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/admin/lop_hoc";
		}
	}
} 