package com.example.DoAn.Service.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.example.DoAn.Entity.GiangVien;
import com.example.DoAn.Entity.LopHoc;
import com.example.DoAn.Repository.GiangVienRepository;
import com.example.DoAn.Repository.HocVienRepository;
import com.example.DoAn.Service.GiangVienService;
import com.example.DoAn.Service.LopHocService;
import com.example.DoAn.Service.LichSuService;

@Service
public class GiangVienServiceImp implements GiangVienService {

	@Autowired
	private GiangVienRepository giangVienRepo;

	@Autowired
	private LopHocService lopHocService;

	@Autowired
	private HocVienRepository hocVienRepository;

	@Autowired
	private LichSuService lichSuService;

	@Override
	@Transactional
	public void deleteGiangVien(Integer giangVienID, String nguoiThucHien) {
		GiangVien giangVien = giangVienRepo.findById(giangVienID).orElse(null);
		if (giangVien != null) {
			// 1. Xóa giảng viên khỏi các lớp học trước
			List<LopHoc> lopHocs = giangVien.getLopHocs();
			if (lopHocs != null && !lopHocs.isEmpty()) {
				for (LopHoc lopHoc : lopHocs) {
					// Lưu thông tin trước khi xóa để ghi log
					String tenGiangVien = giangVien.getHoTenGV();
					String tenLop = lopHoc.getTenLop();
					
					lopHoc.getListGiangVien().remove(giangVien);
					lopHocService.updateLopHoc(lopHoc, 
						lopHoc.getListGiangVien().stream()
							.map(gv -> gv.getGiangVienID())
							.collect(Collectors.toList()),
						nguoiThucHien);
							
					// Ghi log cho việc xóa giảng viên khỏi lớp
					String noiDungLopHoc = String.format("Xóa giảng viên %s khỏi lớp %s", tenGiangVien, tenLop);
					lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Lớp Học", noiDungLopHoc);
				}
			}

			// 2. Set giảng viên chính của học viên về null
			hocVienRepository.updateGiangVienChinh(giangVien);

			// 3. Xóa giảng viên
			giangVienRepo.deleteById(giangVienID);

			// Ghi log cho việc xóa giảng viên
			String noiDung = String.format("Xóa giảng viên: %s (Email: %s, SĐT: %s)", 
				giangVien.getHoTenGV(),
				giangVien.getEmailGV(),
				giangVien.getSdtGV());
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Giảng Viên", noiDung);
		}
	}
} 