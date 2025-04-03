package com.example.DoAn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.DoAn.Service.AdminService;

@Controller
public class AccountAdminController {
	@Autowired
	private AdminService adminService;

	// Dùng để tạo tài khoản Admin ban đầu
	@GetMapping("/account")
	@ResponseBody
	public String getAccount() {
//		AdminAccount admin = new AdminAccount();
//		admin.setUserName("admin");
//		admin.setPassword("123");
//		admin.setEmail("admin1@gmail.com");
//		admin.setPhone("0987631236");
//		admin.setRole("ADMIN");
//		adminService.saveAdmin(admin);
		return "Tài khoản đã được tạo";
	}
}
