package org.example;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateManager {
    
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