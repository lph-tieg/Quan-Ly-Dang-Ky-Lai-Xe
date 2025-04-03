package com.example.DoAn.Service.IMPL;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DoAn.Model.Hang;
import com.example.DoAn.Repository.HangRepository;
import com.example.DoAn.Service.HangService;

@Service
@Transactional
public class HangServiceImpl implements HangService {
	@Autowired
	private HangRepository hangRepository;

	// Tìm hạng theo tên gần giống
	@Override
	public List<Hang> findByTenHangContaining(String tenHang) {
		// TODO Auto-generated method stub
		return hangRepository.findByTenHangContaining(tenHang);
	}

	// Lấy danh sách hạng trong database
	@Override
	public List<Hang> findAll() {
		// TODO Auto-generated method stub

//		listHang.sort((h1, h2) -> hangComparator().compare(h1.getTenHang(), h2.getTenHang()));

		return hangRepository.findAll();
	}

	// Tìm hạng theo tên (duy nhất trong database)
	@Override
	public Optional<Hang> findByTenHang(String tenHang) {
		// TODO Auto-generated method stub
		return hangRepository.findByTenHang(tenHang);
	}

	// Thêm hạng
	@Override
	public void createHang(Hang hang) {
		// TODO Auto-generated method stub
		hangRepository.save(hang);
	}

	// Tìm hạng theo ID
	@Override
	public Hang findByID(Integer hangID) {
		return hangRepository.findById(hangID)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hạng với ID: " + hangID));
	}

	// Sắp xếp hạng theo thứ tự
	private Comparator<String> hangComparator() {
		return new Comparator<String>() {

			@Override
			public int compare(String hang1, String hang2) {
				// TODO Auto-generated method stub
				Map<String, Integer> thuTuHang = new HashMap<>();
				thuTuHang.put("A1", 1);
				thuTuHang.put("A2", 2);
				thuTuHang.put("A3", 3);
				thuTuHang.put("A4", 4);
				thuTuHang.put("B1", 5);
				thuTuHang.put("B2", 6);
				thuTuHang.put("C", 7);
				thuTuHang.put("D", 8);
				thuTuHang.put("E", 9);
				thuTuHang.put("FB2", 10);
				thuTuHang.put("FC", 11);
				thuTuHang.put("FD", 12);
				thuTuHang.put("FE", 13);
				return Integer.compare(thuTuHang.getOrDefault(hang1, Integer.MAX_VALUE),
						thuTuHang.getOrDefault(hang2, Integer.MAX_VALUE));
			}
		};
	}

	// Tìm kiếm hạng theo ID (ID duy nhất trong database)
	@Override
	public Optional<Hang> findByHangID(Integer hangID) {
		return hangRepository.findById(hangID);
	}

	// Lấy danh sách theo thứ tự tăng dần
	@Override
	public List<Hang> findAllOrderByTenHang() {
		// TODO Auto-generated method stub
		return hangRepository.findAllOrderByTenHang();
	}

}
