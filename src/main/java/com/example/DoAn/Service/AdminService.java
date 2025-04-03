package com.example.DoAn.Service;

import java.util.Optional;

import com.example.DoAn.Model.AdminAccount;

public interface AdminService {

	// Tìm tài khoản Admin dựa trên userName
	Optional<AdminAccount> findByUserName(String userName);

	// Tìm tài khoản Admin dựa trên sdt
	Optional<AdminAccount> findByPhone(String phone);

	// Tìm tài khoản Admin dựa trên userName và sdt
	Optional<AdminAccount> findByUserNameAndPhone(String userName, String phone);

	// Lưu hoặc cập nhật tài khoản Admin
	public void saveAdmin(AdminAccount admin);

	// Cập nhật tài khoản và mật khẩu mới
	AdminAccount updateAdminProfile(AdminAccount admin, String newPassword);
}
