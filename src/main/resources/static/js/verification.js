// Hàm gửi mã xác nhận email
function sendEmailVerification(email) {
    fetch('/api/verification/send-email?email=' + email, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data);
    })
    .catch(error => {
        alert('Có lỗi xảy ra: ' + error);
    });
}

// Hàm gửi mã xác nhận SMS
function sendSMSVerification(phone) {
    fetch('/api/verification/send-sms?phone=' + phone, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data);
    })
    .catch(error => {
        alert('Có lỗi xảy ra: ' + error);
    });
}

// Hàm xác nhận email
function verifyEmail(email, code) {
    fetch('/api/verification/verify-email?email=' + email + '&code=' + code, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data);
        if (data.includes('thành công')) {
            document.getElementById('emailVerified').value = 'true';
            document.getElementById('emailVerificationSection').style.display = 'none';
        }
    })
    .catch(error => {
        alert('Có lỗi xảy ra: ' + error);
    });
}

// Hàm xác nhận số điện thoại
function verifyPhone(phone, code) {
    fetch('/api/verification/verify-phone?phone=' + phone + '&code=' + code, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data);
        if (data.includes('thành công')) {
            document.getElementById('phoneVerified').value = 'true';
            document.getElementById('phoneVerificationSection').style.display = 'none';
        }
    })
    .catch(error => {
        alert('Có lỗi xảy ra: ' + error);
    });
}

// Đếm ngược thời gian gửi lại mã
function startCountdown(buttonId, seconds) {
    const button = document.getElementById(buttonId);
    button.disabled = true;
    let remainingSeconds = seconds;
    
    const countdown = setInterval(() => {
        button.textContent = `Gửi lại mã (${remainingSeconds}s)`;
        remainingSeconds--;
        
        if (remainingSeconds < 0) {
            clearInterval(countdown);
            button.disabled = false;
            button.textContent = 'Gửi lại mã';
        }
    }, 1000);
} 