package com.example.DoAn.Service.IMPL;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.DoAn.Model.AdminAccount;
import com.example.DoAn.Repository.AdminRepository;
import com.example.DoAn.Service.AdminService;

@Service
public class AdminServiceImpl implements AdminService, UserDetailsService {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Tải thông tin admin trong database bằng username để đăng nhập
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AdminAccount> adminOptional = adminRepository.findByUserName(username);
		if (adminOptional.isPresent()) {
			AdminAccount admin = adminOptional.get();
			System.out.println("Mật khẩu trong cơ sở dữ liệu: " + admin.getPassword());
			return org.springframework.security.core.userdetails.User.builder().username(admin.getUserName())
					.password(admin.getPassword()).roles(admin.getRole()) // Gắn vai trò cho admin
					.build();
		} else {
			throw new UsernameNotFoundException("Không tìm thấy người dùng " + username);
		}
	}

	// Tìm tài khoản Admin theo username
	@Override
	public Optional<AdminAccount> findByUserName(String userName) {
		return adminRepository.findByUserName(userName);
	}

	// Tìm tài khoản theo sdt
	@Override
	public Optional<AdminAccount> findByPhone(String phone) {
		return adminRepository.findByPhone(phone);
	}

	// Tìm tài khoản theo username và sdt
	@Override
	public Optional<AdminAccount> findByUserNameAndPhone(String userName, String phone) {
		return adminRepository.findByUserNameAndPhone(userName, phone);
	}

	// Lưu tài khoản admin
	@Override
	public void saveAdmin(AdminAccount admin) {
//		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		adminRepository.save(admin);
	}

	// Cập nhật thông tin admin
	@Override
	public AdminAccount updateAdminProfile(AdminAccount admin, String newPassword) {
		Optional<AdminAccount> existingAdmin = adminRepository.findByUserName(admin.getUserName());

		if (existingAdmin.isPresent()) {
			AdminAccount adminToUpdate = existingAdmin.get();

			adminToUpdate.setEmail(admin.getEmail());
			adminToUpdate.setPhone(admin.getPhone());

			if (newPassword != null && !newPassword.trim().isEmpty()) {
				adminToUpdate.setPassword(passwordEncoder.encode(newPassword));
			}

			return adminRepository.save(adminToUpdate);
		}

		return null;
	}

}
