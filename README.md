-- Tạo Tài Khoản Admin--
1. File AdminServiceImpl:
mở comment "admin.setPassword(passwordEncoder.encode(admin.getPassword()));" ở saveAdmin để mã hoá mật khẩu khi lưu
2. File AccountAdminController:
mở comment trong getAccount để tạo tài khoản admin.
3. Chạy localhost:8080/account để tạo tài khoản.
4. File AdminServiceImpl comment lại đoạn "admin.setPassword(passwordEncoder.encode(admin.getPassword()));" để tránh xung đột mật khẩu khi cập nhật
5. Tài khoản để đăng nhập sau khi triển khai:
   userName : admin
   password: 123

   
-- Chức Năng Nổi Bật--
1. Trang đăng ký khoá học:
   - Loại xe sẽ được hiển thị theo hạng mà người dùng chọn. VD: Chọn hạng A1 thì xe sẽ hiển thị Vison.
   - Tên giảng viên sẽ hiển thị khi người dùng chọn lịch học, buổi học và hạng. VD: lịch học: T2-T4-T6, buổi học: Sáng, Hạng: A1. Sau khi chọn các mục trên thì giảng viên sẽ hiển thị ra các giảng viên có lịch học, buổi học và hạng trùng khớp với các mục mà người dùng chọn.

2. Trang Danh Sách Đăng Ký:
   - Phân lớp cho người dùng đăng ký dựa trên lịch học, buổi học và hạng. VD: người dùng có lịch học: T2-T4-T6, buổi học: Sáng, Hạng: A1 thì lớp học sẽ chỉ hiển thị ra các lớp có dữ liệu trùng khớp với người dùng.

3. Trang Lớp Học:
   - Thêm giảng viên vào lớp dựa trên buổi dạy, lịch dạy và hạng của giảng viên. VD: lớp có hạng: A1, buổi học: Sáng, lịch học: T2-T4-T6 thì mục giảng viên sẽ chỉ hiển thị ra các giảng viên có dữ liệu trùng khớp.

4. Trang Login:
   - Khi quên mật khẩu sẽ yêu cầu nhập SĐT để xác minh, nếu SĐT nhập vào không trùng khớp với SĐT trong db thì sẽ thông báo lỗi.
