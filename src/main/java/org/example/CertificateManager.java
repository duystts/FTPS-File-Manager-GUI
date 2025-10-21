package org.example;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 🔒 Certificate Manager - SSL Certificate Handling
 * 
 * Quản lý SSL certificates cho FTPS connections với 2 modes:
 * 
 * 1. CUSTOM CERTIFICATE MODE:
 *    • Load certificate từ file (.pem, .crt, .cer)
 *    • Validate server identity against provided cert
 *    • Enhanced security - chỉ trust specific server
 *    • Protect against man-in-the-middle attacks
 * 
 * 2. ACCEPT-ALL MODE (Demo only):
 *    • Accept bất kỳ certificate nào
 *    • Dùng khi không có custom certificate
 *    • Vẫn có TLS encryption nhưng không validate server identity
 *    • CHI DÙNG CHO DEMO - KHÔNG AN TOÀN CHO PRODUCTION
 * 
 * CERTIFICATE VALIDATION PROCESS:
 * 1. Load certificate file vào X509Certificate object
 * 2. Tạo KeyStore và add certificate vào trusted store
 * 3. Implement custom X509TrustManager
 * 4. Trong checkServerTrusted(): so sánh server cert với loaded cert
 * 5. Nếu match -> trust, nếu không -> reject connection
 * 
 * DEMO SCENARIOS:
 * • No certificate: Shows "(Default Trust)" - accepts any cert
 * • With certificate: Shows "(Custom Certificate)" - validates server
 * 
 * @author Demo Application
 * @version 1.0
 * @see FTPSFileManager Main application using this certificate manager
 */
public class CertificateManager {
    
    /**
     * Tạo TrustManager cho FTPS connection với certificate validation
     * 
     * LOGIC:
     * - Nếu có certificatePath: Load và validate against specific cert
     * - Nếu không có certificatePath: Accept-all mode (demo only)
     * 
     * CERTIFICATE FORMATS HỖ TRỢ:
     * • .pem: Privacy-Enhanced Mail format
     * • .crt: Certificate file
     * • .cer: Certificate file (alternative extension)
     * 
     * @param certificatePath Đường dẫn đến certificate file (có thể null/empty)
     * @return TrustManager array cho FTPSClient
     */
    public static TrustManager[] createTrustManager(String certificatePath) {
        if (certificatePath == null || certificatePath.isEmpty()) {
            return createAcceptAllTrustManager();
        }
        
        try {
            File certFile = new File(certificatePath);
            if (!certFile.exists()) {
                JOptionPane.showMessageDialog(null, 
                    "Certificate file not found: " + certificatePath);
                return createAcceptAllTrustManager();
            }
            
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert;
            
            try (FileInputStream fis = new FileInputStream(certFile)) {
                cert = (X509Certificate) cf.generateCertificate(fis);
            }
            
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("server", cert);
            
            return new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        // Verify against loaded certificate
                        for (X509Certificate c : chain) {
                            if (c.equals(cert)) {
                                return;
                            }
                        }
                        throw new RuntimeException("Server certificate not trusted");
                    }
                    
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[] { cert };
                    }
                }
            };
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error loading certificate: " + e.getMessage());
            return createAcceptAllTrustManager();
        }
    }
    
    /**
     * Tạo Accept-All TrustManager (CHI DÙNG CHO DEMO)
     * 
     * ⚠️ BẢO MẬT WARNING:
     * TrustManager này accept bất kỳ certificate nào mà không validation.
     * Vẫn có TLS encryption nhưng không protect khỏi man-in-the-middle attacks.
     * 
     * Sử dụng khi:
     * • Demo với self-signed certificates
     * • Test environment không có proper CA
     * • Không có certificate file để validate
     * 
     * KHÔNG BAO GIỞ sử dụng trong production!
     * 
     * @return TrustManager array chấp nhận mọi certificate
     */
    private static TrustManager[] createAcceptAllTrustManager() {
        return new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
    }
}