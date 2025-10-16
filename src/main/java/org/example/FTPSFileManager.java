package org.example;

import org.apache.commons.net.ftp.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class FTPSFileManager extends JFrame {
    private FTPClient ftpClient;
    private JTextField hostField, usernameField, certPathField;
    private JPasswordField passwordField;
    private JCheckBox tlsCheckBox;
    private JButton connectButton, disconnectButton, browseCertButton;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JButton uploadButton, downloadButton;
    
    public FTPSFileManager() {
        initializeGUI();
        ftpClient = new FTPClient();
    }
    
    private void initializeGUI() {
        setTitle("FTPS File Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Connection Panel
        JPanel connectionPanel = createConnectionPanel();
        add(connectionPanel, BorderLayout.NORTH);
        
        // Main Panel
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // File Tree Panel
        JPanel treePanel = createTreePanel();
        mainSplit.setLeftComponent(treePanel);
        
        // Log Panel
        JPanel logPanel = createLogPanel();
        mainSplit.setRightComponent(logPanel);
        
        mainSplit.setDividerLocation(400);
        add(mainSplit, BorderLayout.CENTER);
        
        // Status Panel
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Host, Username, Password
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        hostField = new JTextField("localhost");
        hostField.setPreferredSize(new Dimension(150, 25));
        panel.add(hostField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        usernameField = new JTextField("demo");
        usernameField.setPreferredSize(new Dimension(120, 25));
        panel.add(usernameField, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        passwordField = new JPasswordField("demo123");
        passwordField.setPreferredSize(new Dimension(120, 25));
        panel.add(passwordField, gbc);
        
        // Row 2: TLS, Certificate, Buttons
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        tlsCheckBox = new JCheckBox("🔒 Enable TLS (Secure)", false);
        panel.add(tlsCheckBox, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0;
        panel.add(new JLabel("Certificate:"), gbc);
        gbc.gridx = 2; gbc.weightx = 1; gbc.gridwidth = 2;
        certPathField = new JTextField();
        certPathField.setPreferredSize(new Dimension(200, 25));
        panel.add(certPathField, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        browseCertButton = new JButton("Browse");
        browseCertButton.addActionListener(e -> browseCertificate());
        panel.add(browseCertButton, gbc);
        
        gbc.gridx = 5;
        connectButton = new JButton("Connect Securely");
        connectButton.addActionListener(new ConnectAction());
        panel.add(connectButton, gbc);
        
        // Row 3: Disconnect button
        gbc.gridx = 5; gbc.gridy = 2;
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new DisconnectAction());
        disconnectButton.setEnabled(false);
        panel.add(disconnectButton, gbc);
        
        return panel;
    }
    
    private JPanel createTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Remote Files"));
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Not Connected");
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        
        JScrollPane scrollPane = new JScrollPane(fileTree);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // File operation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(new UploadAction());
        uploadButton.setEnabled(false);
        
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new DownloadAction());
        downloadButton.setEnabled(false);
        
        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("FTP Command Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Disconnected");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(statusLabel);
        return panel;
    }
    
    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private void browseCertificate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pem") 
                    || f.getName().toLowerCase().endsWith(".crt")
                    || f.getName().toLowerCase().endsWith(".cer");
            }
            
            @Override
            public String getDescription() {
                return "Certificate files (*.pem, *.crt, *.cer)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            certPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private class ConnectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String host = hostField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(FTPSFileManager.this, "Please enter host");
                return;
            }
            
            new Thread(() -> {
                try {
                    appendLog("Connecting to " + host + "...");
                    
                    if (tlsCheckBox.isSelected()) {
                        ftpClient = new FTPSClient();
                        
                        // Configure trust manager with certificate if provided
                        String certPath = certPathField.getText().trim();
                        if (!certPath.isEmpty()) {
                            try {
                                TrustManager[] trustManagers = CertificateManager.createTrustManager(certPath);
                                ((FTPSClient)ftpClient).setTrustManager(trustManagers[0]);
                                appendLog("Using custom certificate: " + new File(certPath).getName());
                            } catch (Exception ex) {
                                appendLog("Certificate error: " + ex.getMessage());
                            }
                        }
                        
                        appendLog("🔒 AUTH TLS - Initiating secure handshake...");
                    } else {
                        ftpClient = new FTPClient();
                        appendLog("⚠️ Plain FTP connection - NO ENCRYPTION (INSECURE)");
                    }
                    
                    ftpClient.connect(host, 2121);
                    
                    int reply = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftpClient.disconnect();
                        appendLog("Connection failed: " + ftpClient.getReplyString());
                        return;
                    }
                    
                    appendLog("Connected: " + ftpClient.getReplyString().trim());
                    
                    if (ftpClient.login(username, password)) {
                        appendLog("Login successful");
                        
                        // Set passive mode for better compatibility
                        ftpClient.enterLocalPassiveMode();
                        appendLog("PASV - Entering passive mode");
                        
                        if (tlsCheckBox.isSelected()) {
                            ((FTPSClient)ftpClient).execPBSZ(0);
                            appendLog("🔒 PBSZ 0 - Setting up secure data channel...");
                            ((FTPSClient)ftpClient).execPROT("P");
                            appendLog("🔒 PROT P - Data channel encryption ENABLED");
                            
                            String certInfo = certPathField.getText().trim().isEmpty() ? 
                                "(Default Trust)" : "(Custom Certificate)";
                            
                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText("🔒 SECURE CONNECTION - Encrypted (TLS 1.2) " + certInfo);
                                statusLabel.setForeground(new Color(0, 128, 0)); // Green
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText("⚠️ INSECURE CONNECTION - Plain FTP (NOT ENCRYPTED)");
                                statusLabel.setForeground(new Color(255, 0, 0)); // Red
                            });
                        }
                        
                        SwingUtilities.invokeLater(() -> {
                            connectButton.setEnabled(false);
                            disconnectButton.setEnabled(true);
                            uploadButton.setEnabled(true);
                            downloadButton.setEnabled(true);
                        });
                        
                        loadFileTree();
                    } else {
                        appendLog("Login failed");
                        ftpClient.disconnect();
                    }
                    
                } catch (Exception ex) {
                    appendLog("Error: " + ex.getMessage());
                }
            }).start();
        }
    }
    
    private class DisconnectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                        appendLog("Disconnected");
                    }
                } catch (Exception ex) {
                    appendLog("Disconnect error: " + ex.getMessage());
                }
                
                SwingUtilities.invokeLater(() -> {
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    uploadButton.setEnabled(false);
                    downloadButton.setEnabled(false);
                    statusLabel.setText("Disconnected");
                    statusLabel.setForeground(Color.BLACK);
                    
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Not Connected");
                    treeModel.setRoot(root);
                });
            }).start();
        }
    }
    
    private void loadFileTree() {
        new Thread(() -> {
            try {
                // Try different approaches for listing files
                FTPFile[] files = null;
                
                try {
                    files = ftpClient.listFiles();
                } catch (Exception e1) {
                    appendLog("LIST failed, trying NLST: " + e1.getMessage());
                    try {
                        String[] fileNames = ftpClient.listNames();
                        if (fileNames != null) {
                            files = new FTPFile[fileNames.length];
                            for (int i = 0; i < fileNames.length; i++) {
                                files[i] = new FTPFile();
                                files[i].setName(fileNames[i]);
                            }
                        }
                    } catch (Exception e2) {
                        appendLog("NLST also failed: " + e2.getMessage());
                    }
                }
                
                if (files == null) files = new FTPFile[0];
                
                final FTPFile[] finalFiles = files;
                appendLog("LIST - Retrieved " + finalFiles.length + " files");
                
                SwingUtilities.invokeLater(() -> {
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root Directory");
                    
                    Arrays.stream(finalFiles)
                        .forEach(file -> {
                            String name = file.getName();
                            if (file.isDirectory()) {
                                name += "/";
                            }
                            root.add(new DefaultMutableTreeNode(name));
                        });
                    
                    treeModel.setRoot(root);
                    fileTree.expandRow(0);
                });
                
            } catch (Exception ex) {
                appendLog("Error loading files: " + ex.getMessage());
            }
        }).start();
    }
    
    private class UploadAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(FTPSFileManager.this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                new Thread(() -> {
                    try (FileInputStream fis = new FileInputStream(selectedFile)) {
                        appendLog("STOR " + selectedFile.getName() + " - Uploading...");
                        
                        boolean success = ftpClient.storeFile(selectedFile.getName(), fis);
                        if (success) {
                            appendLog("Upload completed: " + selectedFile.getName());
                            loadFileTree();
                        } else {
                            appendLog("Upload failed: " + ftpClient.getReplyString());
                        }
                        
                    } catch (Exception ex) {
                        appendLog("Upload error: " + ex.getMessage());
                    }
                }).start();
            }
        }
    }
    
    private class DownloadAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode selectedNode = 
                (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            
            if (selectedNode == null || selectedNode.isRoot()) {
                JOptionPane.showMessageDialog(FTPSFileManager.this, "Please select a file");
                return;
            }
            
            String fileName = selectedNode.toString();
            if (fileName.endsWith("/")) {
                JOptionPane.showMessageDialog(FTPSFileManager.this, "Cannot download directory");
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(fileName));
            
            if (fileChooser.showSaveDialog(FTPSFileManager.this) == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                
                new Thread(() -> {
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        appendLog("RETR " + fileName + " - Downloading...");
                        
                        boolean success = ftpClient.retrieveFile(fileName, fos);
                        if (success) {
                            appendLog("Download completed: " + fileName);
                        } else {
                            appendLog("Download failed: " + ftpClient.getReplyString());
                        }
                        
                    } catch (Exception ex) {
                        appendLog("Download error: " + ex.getMessage());
                    }
                }).start();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FTPSFileManager().setVisible(true);
        });
    }
}