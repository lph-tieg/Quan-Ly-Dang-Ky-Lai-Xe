package com.example.DoAn.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.BuoiThucHanh;

@Repository
public interface BuoiThucHanhRepository extends JpaRepository<BuoiThucHanh, Integer> {
	List<BuoiThucHanh> findByBuoiHoc(String buoiHoc);

	List<BuoiThucHanh> findByKhoaHoc_KhoaHocID(Integer khoaHocID);
}
