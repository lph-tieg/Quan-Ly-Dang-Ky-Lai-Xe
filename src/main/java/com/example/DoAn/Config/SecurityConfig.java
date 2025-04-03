package com.example.DoAn.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.DoAn.Service.IMPL.AdminServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	@Lazy // Tránh vòng lặp khi gọi adminServiceImpl
	private AdminServiceImpl adminServiceImpl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Vô hiệu hoá csrf
				.authorizeHttpRequests(auth -> auth
						// Cho phép truy cập không cần đăng nhập với các đường dẫn tĩnh
						.requestMatchers("/css/**", "/img/**").permitAll()
						// Cho phép truy cập không cần đăng nhập với các API cụ thể
						.requestMatchers("/admin/login", "/admin/login/so_dien_thoai", "/admin/login/so_dien_thoai/ma",
								"/admin/login/cap_nhat_mat_khau", "/account", "/getKhoaHocByBuoiHoc")
						.permitAll()
						// Cho phép truy cập công khai cho trang chính và các dịch vụ liên quan
						.requestMatchers("/trung-tam-day-lai-xe-Thanh-Cong", "/trung-tam-day-lai-xe-Thanh-Cong/dich_vu",
								"/trung-tam-day-lai-xe-Thanh-Cong/xe_may", "/trung-tam-day-lai-xe-Thanh-Cong/dang_ky",
								"/trung-tam-day-lai-xe-Thanh-Cong/xe_oto", "/trung-tam-day-lai-xe-Thanh-Cong/tu_van",
								"/trung-tam-day-lai-xe-Thanh-Cong/xe_tai",
								"/trung-tam-day-lai-xe-Thanh-Cong/dang_ky_tu_van",
								"/trung-tam-day-lai-xe-Thanh-Cong/filter-xe",
								"/trung-tam-day-lai-xe-Thanh-Cong/dang_ky_khoa_hoc")
						.permitAll()
						// Chỉ admin mới được truy cập vào các trang quản trị
						.requestMatchers("/admin/**").hasRole("ADMIN")
						// Các request còn lại yêu cầu xác thực
						.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/admin/login") // Đặt trang login
						.failureUrl("/admin/login?error=true") // Chuyển hướng nếu đăng nhập thất bại
						.defaultSuccessUrl("/admin/dashboard", true) // Chuyển hướng khi đăng nhập thành công
						.permitAll())
				.logout(logout -> logout.logoutUrl("/admin/logout") // URL để đăng xuất
						.logoutSuccessUrl("/admin/login") // Chuyển hướng khi đăng xuất thành công
						.invalidateHttpSession(true) // Hủy session khi đăng xuất
						.deleteCookies("JSESSIONID") // Xóa cookie phiên đăng nhập
						.permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Tạo
																												// session
																												// khi
																												// cần
																												// thiết

						.sessionFixation().newSession()); // Tạo session mới sau khi đăng nhập để tránh tấn công
															// fixation
		return http.build();

	}

	/**
	 * Bean dùng để mã hóa mật khẩu bằng BCrypt
	 * 
	 * @return PasswordEncoder đã cấu hình
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Cấu hình AuthenticationManager để xác thực người dùng
	 * 
	 * @param http HttpSecurity để lấy AuthenticationManagerBuilder
	 * @return AuthenticationManager đã cấu hình
	 * @throws Exception nếu có lỗi khi cấu hình
	 */
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(adminServiceImpl) // Sử dụng
																												// AdminServiceImpl
																												// để
																												// lấy
																												// thông
																												// tin
				.passwordEncoder(passwordEncoder()) // Dùng BCrypt để kiểm tra mật khẩu
				.and().build();
	}
}
