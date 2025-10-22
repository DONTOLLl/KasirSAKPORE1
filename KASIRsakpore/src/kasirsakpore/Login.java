/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import koneksi.KoneksiDB; 
import javax.swing.JFrame; 
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout; // Menggunakan BorderLayout untuk Wrapper

/**
 *
 * @author Acer
 */
public class Login extends javax.swing.JFrame {
    
    // Asumsi: Tabel pengguna memiliki kolom 'id_pengguna', 'role', 'password', dan 'status'.

    private void handleLoginSuccess(int userId, String userRole) {
        try {
            DashboardAdmin dashboard = new DashboardAdmin(userId, userRole); 
            dashboard.setVisible(true);
            this.dispose(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat Dashboard Admin: " + e.getMessage(), "Error Pengalihan", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
        // --- Fullscreen dan Centering ---
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setLocationRelativeTo(null); 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // =================================================================
        // DEKLARASI KOMPONEN
        // =================================================================
        jPanelBackground = new javax.swing.JPanel(); // Panel Latar Belakang Fullscreen
        jPanelContent = new javax.swing.JPanel();    // Panel Konten Utama (Ditengahkan)
        
        // Komponen di Content
        jLabelLogo1 = new javax.swing.JLabel(); // Hanya satu logo
        jLabelTitle = new javax.swing.JLabel();
        jLabelUsername = new javax.swing.JLabel();
        jLabelPassword = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        
        // =================================================================
        // KONFIGURASI FRAME UTAMA (FULLSCREEN & WARNA LATAR PENUH)
        // =================================================================
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout()); 
        
        // Setting jPanelBackground (Warna Latar Belakang Fullscreen)
        jPanelBackground.setBackground(new java.awt.Color(58, 151, 151)); // Warna Hijau/Biru Anda
        jPanelBackground.setLayout(new java.awt.GridBagLayout()); // Digunakan untuk menengahkan konten
        
        // Setting jPanelContent (Kontainer Login Utama - Ukuran tetap)
        jPanelContent.setPreferredSize(new java.awt.Dimension(450, 500)); // Ukuran kotak login yang lebih ringkas
        jPanelContent.setBackground(new java.awt.Color(255, 255, 255)); // Latar Putih
        jPanelContent.setLayout(new java.awt.GridBagLayout()); 

        GridBagConstraints gbcContent = new GridBagConstraints();
        gbcContent.insets = new Insets(10, 40, 10, 40);
        gbcContent.fill = GridBagConstraints.HORIZONTAL;
        
        // =================================================================
        // LOGO SAKPORE (Hanya Satu)
        // =================================================================
        
        // --- LOGO: logosakpore-small.png ---
        // PENTING: Ganti path resource
        jLabelLogo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logosakpore-small.png"))); 
        jLabelLogo1.setText(""); 
        gbcContent.gridx = 0;
        gbcContent.gridy = 0;
        gbcContent.insets = new Insets(40, 40, 10, 40);
        gbcContent.fill = GridBagConstraints.NONE;
        jPanelContent.add(jLabelLogo1, gbcContent);

        // --- TITLE LOGIN ---
        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabelTitle.setText("Selamat Datang");
        gbcContent.gridy = 1;
        gbcContent.insets = new Insets(10, 40, 30, 40);
        jPanelContent.add(jLabelTitle, gbcContent);
        
        // --- USERNAME LABEL ---
        jLabelUsername.setFont(new java.awt.Font("Segoe UI", 0, 14));
        jLabelUsername.setText("Username:");
        gbcContent.gridy = 2;
        gbcContent.insets = new Insets(10, 40, 5, 40);
        gbcContent.anchor = GridBagConstraints.WEST;
        jPanelContent.add(jLabelUsername, gbcContent);

        // --- USERNAME FIELD ---
        txtUsername.setPreferredSize(new java.awt.Dimension(100, 40));
        gbcContent.gridy = 3;
        gbcContent.insets = new Insets(0, 40, 10, 40);
        gbcContent.fill = GridBagConstraints.HORIZONTAL;
        jPanelContent.add(txtUsername, gbcContent);

        // --- PASSWORD LABEL ---
        jLabelPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        jLabelPassword.setText("Password:");
        gbcContent.gridy = 4;
        gbcContent.insets = new Insets(10, 40, 5, 40);
        gbcContent.anchor = GridBagConstraints.WEST;
        jPanelContent.add(jLabelPassword, gbcContent);

        // --- PASSWORD FIELD ---
        txtPassword.setPreferredSize(new java.awt.Dimension(100, 40));
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        gbcContent.gridy = 5;
        gbcContent.insets = new Insets(0, 40, 30, 40);
        gbcContent.fill = GridBagConstraints.HORIZONTAL;
        jPanelContent.add(txtPassword, gbcContent);

        // --- LOGIN BUTTON ---
        btnLogin.setBackground(new java.awt.Color(58, 151, 151));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 18));
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("LOGIN");
        btnLogin.setPreferredSize(new java.awt.Dimension(100, 50));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        gbcContent.gridy = 6;
        gbcContent.insets = new Insets(10, 40, 40, 40);
        gbcContent.fill = GridBagConstraints.HORIZONTAL;
        jPanelContent.add(btnLogin, gbcContent);

        // =================================================================
        // FINAL WRAPPER CONFIGURATION
        // =================================================================
        
        // Tambahkan konten utama ke panel latar belakang (ditengahkan)
        jPanelBackground.add(jPanelContent, new GridBagConstraints());

        // Tambahkan panel latar belakang ke ContentPane (agar merentang penuh)
        getContentPane().add(jPanelBackground, BorderLayout.CENTER);
        
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        
        String user = txtUsername.getText().trim(); 
        String pass = new String(txtPassword.getPassword()); 
        txtPassword.setText(""); 

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Input Kosong", JOptionPane.WARNING_MESSAGE);
            return; 
        }

        try {
            Connection conn = KoneksiDB.getKoneksi();
            
            String sql = "SELECT id_pengguna, role, password FROM \"pengguna\" WHERE username = ? AND password = ? AND status='Active'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass); 
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id_pengguna");
                String userRole = rs.getString("role");
                
                JOptionPane.showMessageDialog(null, "Login Berhasil! Selamat datang sebagai " + userRole, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                handleLoginSuccess(userId, userRole);
                
            } else {
                JOptionPane.showMessageDialog(null, "Username atau Password salah atau akun tidak aktif!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan Database. Pastikan server PostgreSQL sudah berjalan.\nDetail: " + e.getMessage(), "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        btnLoginActionPerformed(evt);
    }//GEN-LAST:event_txtPasswordActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabelLogo1;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUsername;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}