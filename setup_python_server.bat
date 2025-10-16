@echo off
echo Installing Python FTP server...

REM Kiểm tra Python
python --version >nul 2>&1
if errorlevel 1 (
    echo Python không tìm thấy!
    echo Chạy install_python.bat để cài Python trước.
    pause
    exit /b 1
)

echo Python đã cài đặt.

REM Cài đặt dependencies
echo Installing pyftpdlib...
pip install pyftpdlib >nul 2>&1
if errorlevel 1 (
    echo pip không hoạt động, thử python -m pip...
    python -m pip install pyftpdlib
) else (
    echo pyftpdlib installed successfully
)

echo Installing pyopenssl for TLS support...
pip install pyopenssl >nul 2>&1
if errorlevel 1 (
    python -m pip install pyopenssl
) else (
    echo pyopenssl installed successfully
)

echo Installing cryptography for certificate generation...
pip install cryptography >nul 2>&1
if errorlevel 1 (
    python -m pip install cryptography
) else (
    echo cryptography installed successfully
)

REM Tạo thư mục demo trong project
if not exist demo_files mkdir demo_files
echo Created demo directory: demo_files

REM Tạo certificate script
echo Creating certificate generator...
echo import os > create_cert.py
echo from cryptography import x509 >> create_cert.py
echo from cryptography.x509.oid import NameOID >> create_cert.py
echo from cryptography.hazmat.primitives import hashes, serialization >> create_cert.py
echo from cryptography.hazmat.primitives.asymmetric import rsa >> create_cert.py
echo import datetime >> create_cert.py
echo. >> create_cert.py
echo if not os.path.exists('server.pem'): >> create_cert.py
echo     key = rsa.generate_private_key(public_exponent=65537, key_size=2048) >> create_cert.py
echo     subject = issuer = x509.Name([x509.NameAttribute(NameOID.COMMON_NAME, u'localhost')]) >> create_cert.py
echo     cert = x509.CertificateBuilder().subject_name(subject).issuer_name(issuer).public_key(key.public_key()).serial_number(x509.random_serial_number()).not_valid_before(datetime.datetime.now(datetime.timezone.utc)).not_valid_after(datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(days=365)).sign(key, hashes.SHA256()) >> create_cert.py
echo     with open('server.pem', 'wb') as f: >> create_cert.py
echo         f.write(key.private_bytes(encoding=serialization.Encoding.PEM, format=serialization.PrivateFormat.PKCS8, encryption_algorithm=serialization.NoEncryption())) >> create_cert.py
echo         f.write(cert.public_bytes(serialization.Encoding.PEM)) >> create_cert.py
echo     print('Certificate created: server.pem') >> create_cert.py
echo else: >> create_cert.py
echo     print('Certificate already exists: server.pem') >> create_cert.py

REM Chạy certificate generator
echo Generating SSL certificate...
python create_cert.py

REM Dọn dẹp
del create_cert.py

echo.
echo ========================================
echo Setup completed successfully!
echo ========================================
echo.
echo Available commands:
echo   start_python_ftp.bat   - Start Plain FTP server
echo   start_python_ftps.bat  - Start FTPS server with TLS
echo   gradlew.bat run        - Start Java client GUI
echo.
echo Demo credentials:
echo   Host: localhost
echo   Username: demo
echo   Password: demo123
echo   Directory: demo_files
echo.
pause