package com.example.DoAn.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.DoAn.Model.GiangVien;

public interface GiangVienRepository extends JpaRepository<GiangVien, Integer> {

	// Tìm giảng viên theo tên không phân biệt chữ hoa, thường
	List<GiangVien> findByHoTenGVContainingIgnoreCase(String hoTenGV);

	// Tìm giảng viên theo sdt gần giống
	List<GiangVien> findBySdtGVContaining(String sdt);

	// Tìm giảng viên theo email không phân biệt chữ hoa, thường
	List<GiangVien> findByEmailGVContainingIgnoreCase(String emailGV);

	// Tìm giảng viên theo sdt
	List<GiangVien> findBySdtGV(String sdt);

	// Tìm giảng viên theo email
	List<GiangVien> findByEmailGV(String emailGV);

	// Tìm giảng viên theo lịch dạy
	List<GiangVien> findByLichDay(String lichDay);

	// Tìm giảng viên theo buổi dạy
	List<GiangVien> findByBuoiDay(String buoiDay);

	// Tìm giảnh viên theo tên
	GiangVien findByHoTenGV(String hoTenGV);

	// Tìm giảng viên theo tên không phân biệt chữ hoa, thường cho phân trang
	Page<GiangVien> findByHoTenGVContainingIgnoreCase(String hoTenGV, Pageable pageable);

	// Tìm giảng viên theo email không phân biệt chữ hoa, thường cho phân trang
	Page<GiangVien> findByEmailGVContainingIgnoreCase(String email, Pageable pageable);

	// Tìm giảng viên theo sdt gần giống
	Page<GiangVien> findBySdtGVContaining(String sdt, Pageable pageable);

	// Tìm giảng viên theo lịch dạy và buổi dạy
	List<GiangVien> findByLichDayAndBuoiDay(String lichDay, String buoiDay);

	// Tìm giảng viên theo lịch dạy, buổi dạy, hạng
	List<GiangVien> findByLichDayAndBuoiDayAndHang_HangID(String lichDay, String buoiDay, Integer hangID);

	// Tìm giảng viên theo ID
	Optional<GiangVien> findById(Integer giangVienID);

//	List<GiangVien> findAllById(List<Integer> giangVienID);

	// Tìm giảnh viên theo lớp học
	@EntityGraph(attributePaths = "lopHocs")
	@Query("SELECT gv FROM GiangVien gv")
	Page<GiangVien> findAllWithLopHocs(Pageable pageable);

	// Tìm giảng viên theo lịch học, buổi học, hạng không trùng lặp
	@Query("SELECT DISTINCT gv FROM GiangVien gv " + "JOIN gv.hang h " + "WHERE gv.lichDay = :lichHoc "
			+ "AND gv.buoiDay = :buoiHoc " + "AND h.tenHang = :hang")
	List<GiangVien> findGiangVienByLichHocBuoiHocHang(@Param("lichHoc") String lichHoc,
			@Param("buoiHoc") String buoiHoc, @Param("hang") String hang);

	// Tìm giảng viên theo lịch học và buỏi học
	@Query("SELECT g FROM GiangVien g WHERE g.lichDay = :lichDay AND g.buoiDay = :buoiDay")
	List<GiangVien> findGiangVienByLichAndBuoi(@Param("lichDay") String lichDay, @Param("buoiDay") String buoiDay);

	// Kiểm tra email đã tồn tại hay chưa
	boolean existsByEmailGV(String emailGV);

	// Kiểm tra sdt đã tồn tại hay chưa
	boolean existsBySdtGV(String sdtGV);

	// Kiểm tra email đã tồn tại hay chưa nhưng bỏ qua giảng viên có ID
	boolean existsByEmailGVAndGiangVienIDNot(String emailGV, Integer id);

	// Kiểm tra sdt đã tồn tại hay chưa nhưng bỏ qua giảng viên có ID
	boolean existsBySdtGVAndGiangVienIDNot(String emailGV, Integer id);

}
