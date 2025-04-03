// Hàm xác nhận xóa
function confirmDelete(event, message = 'Bạn có chắc chắn muốn xóa không?') {
    event.preventDefault();
    const form = event.target;
    
    return Swal.fire({
        title: 'Xác nhận xóa',
        text: message,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Đồng ý',
        cancelButtonText: 'Hủy',
        customClass: {
            popup: 'custom-popup-class'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
        return false;
    });
}

// Xử lý thông báo từ server
document.addEventListener("DOMContentLoaded", function() {
    const messageElement = document.querySelector('[data-message]');
    if (messageElement) {
        const message = messageElement.getAttribute('data-message');
        const type = messageElement.getAttribute('data-type');
        
        if (message) {
            let icon, title;
            switch(type) {
                case 'success':
                    icon = 'success';
                    title = 'Thành công!';
                    break;
                case 'error':
                    icon = 'error';
                    title = 'Lỗi!';
                    break;
                default:
                    icon = 'info';
                    title = 'Thông báo';
            }
            
            Swal.fire({
                title: title,
                text: message,
                icon: icon,
                confirmButtonColor: '#28a745',
                confirmButtonText: 'Đóng',
                customClass: {
                    popup: 'custom-popup-class'
                }
            });
        }
    }
}); 