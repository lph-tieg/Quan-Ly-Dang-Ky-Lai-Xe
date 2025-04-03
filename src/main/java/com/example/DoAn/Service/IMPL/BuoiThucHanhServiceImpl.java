package com.example.DoAn.Service.IMPL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.BuoiThucHanh;
import com.example.DoAn.Repository.BuoiThucHanhRepository;
import com.example.DoAn.Service.BuoiThucHanhService;

@Service
public class BuoiThucHanhServiceImpl implements BuoiThucHanhService {
	@Autowired
	private BuoiThucHanhRepository buoiThucHanhRepository;

	@Override
	public List<BuoiThucHanh> findByBuoiHoc(String buoiHoc) {
		// TODO Auto-generated method stub
		return buoiThucHanhRepository.findByBuoiHoc(buoiHoc);
	}

	@Override
	public List<BuoiThucHanh> findByKhoaHoc(Integer khoaHocID) {
		// TODO Auto-generated method stub
		return buoiThucHanhRepository.findByKhoaHoc_KhoaHocID(khoaHocID);
	}

}
