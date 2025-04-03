package com.example.DoAn.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.Hang;
import com.example.DoAn.Model.KhoaHoc;
import com.example.DoAn.Model.LopHoc;

@Repository
public interface LopHocRepository extends JpaRepository<LopHoc, Integer> {

	// Tìm lớp học theo tên lớp không phân biệt chữ hoa, thường cho phân trang
	Page<LopHoc> findByTenLopContainingIgnoreCase(String tenLop, Pageable pageable);

	// Tìm lớp học theo hạng
	List<LopHoc> findByHang(Hang hang);

	// Tìm lớp học theo buổi học
	List<LopHoc> findByBuoiHoc(String buoiHoc);

	// Tìm lớp học theo lịch học
	List<LopHoc> findByLichHoc(String lichHoc);

//	LopHoc findByTenLop(String tenLop);

	// Tìm lớp học theo tên lớp
	Optional<LopHoc> findByTenLop(String tenLop);

	// Tìm lớp học theo khoá học
	LopHoc findByKhoaHoc(KhoaHoc khoaHoc);

	// Kiểm tra tên lớp đã tồn tại hay chưa
	public boolean existsByTenLop(String tenLop);

	// Tìm lớp học theo ID
	Optional<LopHoc> findById(Integer lopHocID);

	// Tìm lớp học theo lịch học, buổi học, hạng
	List<LopHoc> findByLichHocAndBuoiHocAndHang(String lichHoc, String buoiHoc, Hang hang);

//	@Query("SELECT l FROM LopHoc l WHERE l.hang = :hang AND l.lichHoc = :lichHoc AND l.buoiHoc = :buoiHoc AND l.giangVien.hoTenGV = :giangVien")
//	List<LopHoc> findByCriteria(@Param("hang") String hang, @Param("lichHoc") String lichHoc,
//			@Param("buoiHoc") String buoiHoc, @Param("giangVien") String giangVien);

	// Tìm lớp học theo hạng cho phân trang
	Page<LopHoc> findByHangHangID(Integer hangID, Pageable pageable);

	// Tìm lớp học theo buổi học cho phân trang
	Page<LopHoc> findByBuoiHoc(String buoiHoc, Pageable pageable);
}
