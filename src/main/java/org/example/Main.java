package org.example;

/**
 * 🚀 FTPS File Manager - Application Entry Point
 * 
 * Ứng dụng demo minh họa sự khác biệt bảo mật giữa Plain FTP và FTPS.
 * 
 * CHỨC NĂNG CHÍNH:
 * - Kết nối Plain FTP (không mã hóa) vs FTPS (có mã hóa TLS)
 * - Upload/Download file với visual security indicators
 * - Real-time log hiển thị FTP commands và TLS handshake
 * - Certificate validation cho enhanced security
 * 
 * MỤC ĐÍCH GIÁO DỤC:
 * - Minh họa tại sao Plain FTP không an toàn
 * - Hiển thị cách FTPS bảo vệ dữ liệu bằng TLS encryption
 * - Demo quá trình AUTH TLS, PBSZ, PROT commands
 * 
 * CÁCH SỬ DỤNG:
 * 1. Chạy demo server: start_python_ftp.bat (Plain) hoặc start_python_ftps.bat (Secure)
 * 2. Chạy client: gradlew.bat run
 * 3. Tick/untick "🔒 Enable TLS" để so sánh bảo mật
 * 
 * @author Demo Application
 * @version 1.0
 * @see FTPSFileManager Main GUI application
 */
public class Main {
    /**
     * Application entry point - khởi động FTPS File Manager GUI
     * 
     * @param args Command line arguments (không sử dụng)
     */
    public static void main(String[] args) {
        FTPSFileManager.main(args);
    }
}