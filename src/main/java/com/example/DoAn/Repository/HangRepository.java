package com.example.DoAn.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.Hang;

@Repository
public interface HangRepository extends JpaRepository<Hang, Integer> {
	// Tìm hạng theo
	Optional<Hang> findByTenHang(String tenHang);

	// Tìm hạng theo tên gần giống
	List<Hang> findByTenHangContaining(String tenHang);

	// Tìm hạng theo ID
	Optional<Hang> findByHangID(Integer hangID);

	// Tìm hạng theo tên hạng và được sắp xếp theo tên hạng
	@Query("SELECT h FROM Hang h ORDER BY h.tenHang")
	List<Hang> findAllOrderByTenHang();

}
