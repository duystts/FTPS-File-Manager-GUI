# 🔒 FTPS File Manager - Security Demo Application

**Desktop application minh họa sự khác biệt bảo mật giữa Plain FTP và FTPS với mã hóa TLS**

##  Mục đích

Ứng dụng Java Swing để **demo trực quan** sự khác biệt về bảo mật giữa:
- **⚠️ Plain FTP**: Truyền dữ liệu không mã hóa (KHÔNG AN TOÀN)
- **🔒 FTPS**: Truyền dữ liệu có mã hóa TLS 1.2 (AN TOÀN)

##  Kiến trúc hệ thống

### **Client (Java Desktop App)**
- **Ngôn ngữ**: Java + Swing GUI
- **Build tool**: Gradle
- **FTP Library**: Apache Commons Net 3.10.0
- **Chức năng**: Kết nối FTP/FTPS, upload/download file

### **Demo Server (Python)**
- **Ngôn ngữ**: Python + pyftpdlib
- **Chức năng**: Cung cấp Plain FTP và FTPS server để test
- **TLS Support**: pyopenssl + cryptography

## Quick Start

### 1. Setup Demo Server
```bash
setup_python_server.bat    # Cài pyftpdlib + tạo SSL certificate
```

### 2. Demo Plain FTP (Không bảo mật)
```bash
start_python_ftp.bat       # Chạy Plain FTP server
gradlew.bat run            # Chạy client GUI
```
**Trong GUI**: ❌ KHÔNG tick "🔒 Enable TLS" → Connect

### 3. Demo FTPS (Có bảo mật)
```bash
start_python_ftps.bat      # Chạy FTPS server
```
**Trong GUI**: ✅ TICK "🔒 Enable TLS" → Connect

##  Demo Features

### **Visual Security Indicators**
- **🔒 Status xanh**: "SECURE CONNECTION - Encrypted (TLS 1.2)"
- **⚠️ Status đỏ**: "INSECURE CONNECTION - Plain FTP (NOT ENCRYPTED)"
- **Real-time log**: Hiển thị TLS handshake process

### **Security Comparison**
| Aspect | Plain FTP | FTPS |
|--------|-----------|------|
| Username/Password | ❌ Plain text | ✅ TLS Encrypted |
| File Transfer | ❌ No encryption | ✅ TLS Encrypted |
| Eavesdropping | ❌ Vulnerable | ✅ Protected |
| Man-in-middle | ❌ Vulnerable | ✅ Protected |

### **Technical Demo Points**
- **AUTH TLS**: Khởi tạo TLS handshake
- **PBSZ 0**: Setup secure data channel
- **PROT P**: Enable data encryption
- **Certificate validation**: Custom cert support

##  Project Structure

```
FTPS/
├── src/main/java/org/example/
│   ├── Main.java                 # Application entry point
│   ├── FTPSFileManager.java      # Main GUI + FTP/FTPS logic
│   └── CertificateManager.java   # SSL certificate handling
├── ftp_server.py                 # Plain FTP demo server
├── ftps_server.py                # FTPS demo server with TLS
├── setup_python_server.bat       # Server setup script
├── start_python_ftp.bat          # Start Plain FTP
├── start_python_ftps.bat         # Start FTPS
├── DEMO_GUIDE.md                 # Detailed demo instructions
└── build.gradle.kts              # Gradle build configuration
```

##  Technical Stack

### **Client Dependencies**
```gradle
implementation("commons-net:commons-net:3.10.0")  // FTP/FTPS client
// Java Swing (built-in)                           // GUI framework
// Java SSL (built-in)                             // TLS/SSL support
```

### **Server Dependencies**
```bash
pip install pyftpdlib      # FTP server framework
pip install pyopenssl      # TLS/SSL support
```

##  Demo Scenarios

### **Scenario 1: Insecure Plain FTP**
1. Start `ftp_server.py`
2. Connect without TLS
3. **Result**: Red warning, plain text transmission

### **Scenario 2: Secure FTPS**
1. Start `ftps_server.py`
2. Connect with TLS enabled
3. **Result**: Green secure status, encrypted transmission

### **Scenario 3: Certificate Validation**
1. Use custom certificate file
2. Demonstrate server identity verification
3. **Result**: Enhanced security with cert validation

## Learning Outcomes

**Sau khi demo, người xem hiểu được:**
- Tại sao Plain FTP không an toàn
- Cách FTPS bảo vệ dữ liệu bằng TLS
- Quá trình TLS handshake trong thực tế
- Tầm quan trọng của mã hóa trong truyền tải file

## Security Notes

- **Plain FTP**: Mọi dữ liệu (username, password, file) truyền dạng plain text
- **FTPS**: Toàn bộ communication được mã hóa TLS 1.2
- **Certificate**: Tùy chọn để xác thực server identity
- **Demo purpose**: Chỉ dùng để học tập, không deploy production

---

**🎓 Educational Project**: Minh họa tầm quan trọng của mã hóa trong bảo mật mạng