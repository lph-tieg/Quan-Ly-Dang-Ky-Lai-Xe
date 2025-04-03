1. File AdminServiceImpl:
mở comment "admin.setPassword(passwordEncoder.encode(admin.getPassword()));" ở saveAdmin để mã hoá mật khẩu khi lưu
2. File AccountAdminController:
mở comment trong getAccount để tạo tài khoản admin.
3. Chạy localhost:8080/account để tạo tài khoản.
4. File AdminServiceImpl comment lại đoạn "admin.setPassword(passwordEncoder.encode(admin.getPassword()));" để tránh xung đột mật khẩu khi cập nhật
5. Tài khoản để đăng nhập sau khi triển khai:
   userName : admin
   password: 123

   
