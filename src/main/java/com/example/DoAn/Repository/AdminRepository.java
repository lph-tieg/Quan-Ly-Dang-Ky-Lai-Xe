package com.example.DoAn.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.DoAn.Model.AdminAccount;

@Repository
public interface AdminRepository extends JpaRepository<AdminAccount, Integer> {

	// Tìm tài khoản Admin theo tên
	@Query(value = "SELECT * FROM admin WHERE BINARY userName = :userName", nativeQuery = true)
	Optional<AdminAccount> findByUserName(@Param(value = "userName") String userName);

	// Tìm tài khoản Admin theo sdt
	Optional<AdminAccount> findByPhone(String phone);

	// Tìm tài khoản Admin theo tên và sdt
	Optional<AdminAccount> findByUserNameAndPhone(String userName, String phone);
}
