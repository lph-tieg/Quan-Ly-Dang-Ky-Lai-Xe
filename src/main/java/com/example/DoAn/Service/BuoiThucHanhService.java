package com.example.DoAn.Service;

import java.util.List;

import com.example.DoAn.Model.BuoiThucHanh;

public interface BuoiThucHanhService {
	public List<BuoiThucHanh> findByBuoiHoc(String buoiHoc);

	public List<BuoiThucHanh> findByKhoaHoc(Integer khoaHocID);

}
