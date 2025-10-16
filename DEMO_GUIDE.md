# 🔒 FTPS Security Demo Guide

## Mục đích Demo
Minh họa sự khác biệt giữa **Plain FTP** (không bảo mật) và **FTPS** (có mã hóa TLS).

## 🎯 Kịch bản Demo

### Bước 1: Demo Plain FTP (KHÔNG AN TOÀN)
1. **Chạy server**: `start_python_ftp.bat`
2. **Chạy client**: `gradlew.bat run`
3. **Kết nối**: 
   - ❌ **KHÔNG tick "🔒 Enable TLS"**
   - Click "Connect Securely"
4. **Quan sát**:
   - Status: `⚠️ INSECURE CONNECTION - Plain FTP (NOT ENCRYPTED)` (màu đỏ)
   - Log: `⚠️ Plain FTP connection - NO ENCRYPTION (INSECURE)`
   - Upload/Download: Dữ liệu truyền dạng plain text

### Bước 2: Demo FTPS (AN TOÀN)
1. **Dừng Plain FTP server** (Ctrl+C)
2. **Chạy FTPS server**: `start_python_ftps.bat`
3. **Trong client**:
   - ✅ **TICK "🔒 Enable TLS (Secure)"**
   - Click "Connect Securely"
4. **Quan sát**:
   - Status: `🔒 SECURE CONNECTION - Encrypted (TLS 1.2)` (màu xanh)
   - Log hiển thị TLS handshake:
     ```
     🔒 AUTH TLS - Initiating secure handshake...
     Connected: 234 AUTH TLS successful.
     🔒 PBSZ 0 - Setting up secure data channel...
     🔒 PROT P - Data channel encryption ENABLED
     ```

## 🔍 Điểm nhấn Demo

### Visual Indicators:
- **🔒 Icon xanh**: Kết nối an toàn
- **⚠️ Icon đỏ**: Kết nối không an toàn
- **Status bar màu sắc**: Xanh = An toàn, Đỏ = Nguy hiểm

### Technical Details:
- **Plain FTP**: Username/password và data truyền dạng plain text
- **FTPS**: Mọi thứ được mã hóa bằng TLS 1.2
- **Log chi tiết**: Hiển thị từng bước TLS handshake

### Security Comparison:
| Aspect | Plain FTP | FTPS |
|--------|-----------|------|
| Username/Password | ❌ Plain text | ✅ Encrypted |
| File Transfer | ❌ No encryption | ✅ TLS encrypted |
| Eavesdropping | ❌ Vulnerable | ✅ Protected |
| Man-in-middle | ❌ Vulnerable | ✅ Protected |

## 🎪 Demo Script

**"Hôm nay tôi sẽ demo sự khác biệt giữa FTP và FTPS..."**

1. **"Đầu tiên, Plain FTP - không bảo mật"**
   - Chỉ status đỏ, log cảnh báo
   - "Mọi dữ liệu truyền dạng plain text, ai cũng đọc được"

2. **"Bây giờ, FTPS với mã hóa TLS"**
   - Chỉ status xanh, log TLS handshake
   - "Dữ liệu được mã hóa, an toàn trước hacker"

3. **"Upload file để thấy sự khác biệt"**
   - Plain FTP: File truyền không mã hóa
   - FTPS: File được mã hóa TLS

**Kết luận**: "FTPS bảo vệ dữ liệu khỏi bị đánh cắp trong quá trình truyền tải"