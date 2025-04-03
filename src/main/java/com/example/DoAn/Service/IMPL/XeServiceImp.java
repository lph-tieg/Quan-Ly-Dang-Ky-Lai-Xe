package com.example.DoAn.Service.IMPL;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.XeTapLai;
import com.example.DoAn.Repository.XeRepository;
import com.example.DoAn.Service.LichSuHoatDongService;
import com.example.DoAn.Service.XeService;

@Service
public class XeServiceImp implements XeService {

	@Autowired
	private XeRepository xeRepo;

	@Autowired
	private LichSuHoatDongService lichSuService;

	// Lấy danh sách xe
	@Override
	public List<XeTapLai> findAllXe() {
		// Lấy tất cả xe trong hệ thống
		return xeRepo.findAll();
	}

	/**
	 * Xoá xe Lưu lại lịch sử hoạt động
	 */
	@Override
	public void deleteXe(Integer xeID, String nguoiThucHien) {
		// Xóa xe theo ID
		XeTapLai xeTapLai = xeRepo.findById(xeID).orElse(null);
		if (xeTapLai != null) {
			String noiDung = "Xoá xe " + xeTapLai.getTenXe();
			xeRepo.deleteById(xeID);
			lichSuService.themLichSu(nguoiThucHien, "Xoá", "Xe", noiDung);

		} else {
			throw new IllegalArgumentException("Không tìm thấy xe với ID: " + xeID);
		}
	}

	// Tìm xe theo ID
	@Override
	public XeTapLai findByID(Integer xeID) {
		// Tìm xe theo ID
		return xeRepo.findById(xeID)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + xeID));
	}

	/**
	 * Cập nhật xe Lưu lại lịch sử hoạt động
	 */
	@Override
	public XeTapLai updateXe(XeTapLai xeTapLai, String nguoiThucHien) {
		// Kiểm tra thông tin xe
		if (xeTapLai == null) {
			throw new IllegalArgumentException("Thông tin xe không được để trống");
		}

		// Kiểm tra ID xe
		if (xeTapLai.getXeID() == null) {
			throw new IllegalArgumentException("ID xe không được để trống");
		}

		// Kiểm tra số lượng
		if (xeTapLai.getSoLuong() == null || xeTapLai.getSoLuong() < 0) {
			throw new IllegalArgumentException("Số lượng xe phải lớn hơn hoặc bằng 0");
		}

		// Lấy xe hiện tại từ database
		XeTapLai existingXe = xeRepo.findById(xeTapLai.getXeID())
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + xeTapLai.getXeID()));

		// Đảm bảo cập nhật đúng xe
		if (!existingXe.getXeID().equals(xeTapLai.getXeID())) {
			throw new IllegalArgumentException("ID xe không khớp với xe cần cập nhật");
		}

		// Tính toán số lượng còn lại
		int soLuongMoi = xeTapLai.getSoLuong();
		int soLuongCu = existingXe.getSoLuong();
		int soLuongConLaiCu = existingXe.getSoLuongConLai();

		// Tính toán số lượng còn lại mới
		int soLuongConLaiMoi;
		if (soLuongMoi > soLuongCu) {
			// Nếu tăng số lượng, tăng số lượng còn lại tương ứng
			soLuongConLaiMoi = soLuongConLaiCu + (soLuongMoi - soLuongCu);
		} else if (soLuongMoi < soLuongCu) {
			// Nếu giảm số lượng, giảm số lượng còn lại tương ứng
			soLuongConLaiMoi = Math.max(0, soLuongConLaiCu - (soLuongCu - soLuongMoi));
		} else {
			// Nếu số lượng không đổi, giữ nguyên số lượng còn lại
			soLuongConLaiMoi = soLuongConLaiCu;
		}

		// Cập nhật thông tin xe
		xeTapLai.setSoLuongConLai(soLuongConLaiMoi);

		// Tạo nội dung chi tiết các thay đổi
		StringBuilder noiDung = new StringBuilder();
		noiDung.append("Cập nhật xe ").append(existingXe.getTenXe()).append(" (ID: ").append(xeTapLai.getXeID())
				.append("):\n");

		// Kiểm tra và thêm các thay đổi vào nội dung
		if (!existingXe.getTenXe().equals(xeTapLai.getTenXe())) {
			noiDung.append("- Tên xe: ").append(existingXe.getTenXe()).append(" -> ").append(xeTapLai.getTenXe())
					.append("\n");
		}
		if (!existingXe.getLoaiXe().equals(xeTapLai.getLoaiXe())) {
			noiDung.append("- Loại xe: ").append(existingXe.getLoaiXe()).append(" -> ").append(xeTapLai.getLoaiXe())
					.append("\n");
		}
		if (!existingXe.getLoaiSoXe().equals(xeTapLai.getLoaiSoXe())) {
			noiDung.append("- Loại số xe: ").append(existingXe.getLoaiSoXe()).append(" -> ")
					.append(xeTapLai.getLoaiSoXe()).append("\n");
		}
		if (!existingXe.getSoLuong().equals(xeTapLai.getSoLuong())) {
			noiDung.append("- Số lượng: ").append(existingXe.getSoLuong()).append(" -> ").append(xeTapLai.getSoLuong())
					.append("\n");
		}

		if (!existingXe.getHang().getHangID().equals(xeTapLai.getHang().getHangID())) {
			noiDung.append("- Hạng xe: ").append(existingXe.getHang().getTenHang()).append(" -> ")
					.append(xeTapLai.getHang().getTenHang()).append("\n");
		}
		if (!existingXe.getGiaThue().equals(xeTapLai.getGiaThue())) {
			noiDung.append("- Giá thuê: ").append(existingXe.getGiaThue()).append(" -> ").append(xeTapLai.getGiaThue())
					.append("\n");
		}

		// Ghi log lịch sử hoạt động
		lichSuService.themLichSu(nguoiThucHien, "Cập Nhật", "Xe", noiDung.toString());

		return xeRepo.save(xeTapLai);
	}

	/**
	 * Thêm xe mới Lưu lại lịch sử hạot động
	 */
	@Override
	public void createXe(XeTapLai xeTapLai, String nguoiThucHien) {
		// Thêm xe mới
		if (xeTapLai != null) {
			try {
				// Kiểm tra các trường bắt buộc
				if (xeTapLai.getTenXe() == null || xeTapLai.getTenXe().trim().isEmpty()) {
					throw new IllegalArgumentException("Tên xe không được để trống");
				}
				if (xeTapLai.getLoaiXe() == null || xeTapLai.getLoaiXe().trim().isEmpty()) {
					throw new IllegalArgumentException("Loại xe không được để trống");
				}
				if (xeTapLai.getSoLuong() == null || xeTapLai.getSoLuong() < 0) {
					throw new IllegalArgumentException("Số lượng xe phải lớn hơn hoặc bằng 0");
				}

				xeTapLai.setSoLuongConLai(xeTapLai.getSoLuong());

				String noiDung = "Thêm xe " + xeTapLai.getTenXe();

				xeRepo.save(xeTapLai);

				lichSuService.themLichSu(nguoiThucHien, "Thêm Mới", "Xe", noiDung);
			} catch (Exception e) {
				throw new RuntimeException("Lỗi khi thêm xe mới: " + e.getMessage());
			}
		} else {
			throw new IllegalArgumentException("Thông tin xe không được để trống");
		}
	}

	// Tìm xe theo tên không phân biệt chữ hoa chữ thường trong phân trang
	@Override
	public Page<XeTapLai> findByTenXeContainingIgnoreCase(String tenXe, Pageable pageable) {
		// Tìm xe theo tên (không phân biệt hoa thường)
		return xeRepo.findByTenXeContainingIgnoreCase(tenXe, pageable);
	}

	// Tìm xe theo loại xe không phân biệt chữ hoa chữ thường trong phân trang
	@Override
	public Page<XeTapLai> findByLoaiXeContainingIgnoreCase(String loaiXe, Pageable pageable) {
		// Tìm xe theo loại xe (không phân biệt hoa thường)
		return xeRepo.findByLoaiXeContainingIgnoreCase(loaiXe, pageable);
	}

	// Tìm xe theo loại số xe không phân biệt chữ hoa chữ thường. trong phân trang
	@Override
	public Page<XeTapLai> findByLoaiSoXeContainingIgnoreCase(String loaiSoXe, Pageable pageable) {
		// Tìm xe theo loại số xe (không phân biệt hoa thường)
		return xeRepo.findByLoaiSoXeContainingIgnoreCase(loaiSoXe, pageable);
	}

	// Lấy danh sách xe trong phân trang
	@Override
	public Page<XeTapLai> findAllListXe(Integer pageNo) {
		// Phân trang danh sách xe (5 xe mỗi trang)
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
		return xeRepo.findAll(pageable);
	}

	// Tìm xe theo ID (ID duy nhất trong database)
	@Override
	public Optional<XeTapLai> findByXeID(Integer xeID) {
		// Tìm xe theo ID (trả về Optional)
		return xeRepo.findById(xeID);
	}

	// Tìm xe theo loại xe không phân biệt chữ hoa chữ thường
	@Override
	public List<XeTapLai> findByLoaiXe(String loaiXe) {
		// Tìm xe theo loại xe (hạng)
		return xeRepo.findByLoaiXeContainingIgnoreCase(loaiXe);
	}

	// Tìm xe theo ID của hạng
	@Override
	public List<XeTapLai> findByHangID(Integer hangID) {
		return xeRepo.findByHangHangID(hangID);
	}

	// Lấy danh sách xe (loại bỏ các tên trùng lặp và sắp xếp tăng dần)
	@Override
	public List<String> findAllLoaiXe() {
		// TODO Auto-generated method stub
		return xeRepo.findDistinctLoaiXe();
	}

	// Lấy danh sách xe (loại bỏ các loại số xe trùng lặp và sắp xếp tăng dần)
	@Override
	public List<String> findAllLoaiSoXe() {
		// TODO Auto-generated method stub
		return xeRepo.findDistinctLoaiSoXe();
	}

	// Cập nhật số lượng xe mỗi khi được sử dụng hoặc cập nhật
	@Override
	public void updateSoLuongXe(XeTapLai xeTapLai) {
		if (xeTapLai == null) {
			throw new IllegalArgumentException("Thông tin xe không được để trống");
		}
		xeRepo.save(xeTapLai);
	}

}
