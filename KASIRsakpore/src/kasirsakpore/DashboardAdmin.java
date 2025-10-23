/*private void jPanel
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.JFrame; 
import kasirsakpore.UserManager;
// Tambahkan import jika LaporanPenjualan tidak berada di package yang sama
// import kasirsakpore.LaporanPenjualan; 
// Karena berada di package yang sama, tidak perlu import eksplisit.


/**
 *
 * @author Acer
 */
public class DashboardAdmin extends javax.swing.JFrame {
    
    private int currentUserId; // Variabel untuk menyimpan ID Pengguna
    private String currentUserLevel; // Variabel untuk menyimpan Role/Level Pengguna

    // =======================================================
    // CONSTRUCTOR
    // =======================================================
    public DashboardAdmin(int userId, String userRole) {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.currentUserId = userId; 
        this.currentUserLevel = userRole; 
        this.setLocationRelativeTo(null); 
        setTanggalDanWaktu(); 
        checkAccess(); 
        setupEscapeKey(); 
    }

    public DashboardAdmin() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.currentUserLevel = "ADMIN"; 
        this.setLocationRelativeTo(null); 
        setTanggalDanWaktu();
        checkAccess();
        setupEscapeKey(); 
    }
    
    public void performLogout() {
    // 1. Membersihkan Sesi (PENTING, jika ada)
    
    // 2. Menutup Jendela Dashboard Saat Ini
    this.dispose(); 
    
    // 3. Menampilkan Halaman Login
    try {
        // Asumsi kelas Login ada di package kasirsakpore
        Login loginFrame = new Login();
        loginFrame.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // =======================================================
    // LOGIC LOGOUT DAN SHORTCUT ESC
    // =======================================================
    
    private void logoutAksi() {
        // Menggunakan 4 argumen: Parent, Pesan, Judul, Opsi (Telah diperbaiki)
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin keluar dan Logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION); 
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                this.dispose();
                // Ganti Login() dengan constructor Login Anda jika berbeda
                Login loginForm = new Login(); 
                loginForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                loginForm.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat Form Login. Aplikasi akan ditutup.", "Error Logout", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
    
    private void setupEscapeKey() {
        InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(escapeKeyStroke, "escapeAction");
        actionMap.put("escapeAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logoutAksi();
            }
        });
    }

    // --- LOGIC TANGGAL DAN WAKTU (Otomatis berganti) ---
    
    private void setTanggalDanWaktu() {
        Timer timer = new Timer(1000, e -> {
            Date now = new Date();
            // Format ID, HH:mm:ss ditambahkan
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"));
            lblTanggal.setText(sdf.format(now)); 
        });
        timer.start();
    }
    
    // --- LOGIC KONTROL AKSES (KATEGORI DAN TRANSAKSI DIHAPUS) ---

    private void checkAccess() {
        String role = this.currentUserLevel.toUpperCase();
        
        // 1. Update Tampilan Header 
        jLabel3.setText(role);
        this.setTitle("Dashboard - Sakpore! Login sebagai: " + role);

        // 2. Tentukan Tipe Role
        boolean isAdmin = role.equals("ADMIN");
        boolean isKasir = role.startsWith("KASIR"); 
        boolean isLaporan = role.equals("LAPORAN");
        
        // 3. Atur Visibilitas Kartu
        
        jPanel1.setVisible(isAdmin); // USER MANAGER 
        jPanel3.setVisible(isAdmin); // SUPPLIER 
        // jPanel5 (KATEGORI) DIHAPUS

        jPanel4.setVisible(isAdmin || isKasir); // DATA BARANG
        // jPanel6 (TRANSAKSI) DIHAPUS

        jPanel7.setVisible(isAdmin || isLaporan); // LAPORAN TRANSAKSI
        jPanel8.setVisible(isAdmin || isLaporan); // LAPORAN PENJUALAN
        
        this.revalidate();
        this.repaint();
    }

    // --- LOGIC NAVIGASI (Pengalihan Form) ---
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        // jLabel4 (Angka '0') DIHAPUS
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnUserManager = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        // jLabel6 (Angka '0') DIHAPUS
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnSupplier = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        // jLabel10 (Angka '0') DIHAPUS
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnDataBarang = new javax.swing.JButton();
        // jPanel5 (Kategori) DIHAPUS
        // jPanel6 (Transaksi) DIHAPUS
        jPanel7 = new javax.swing.JPanel();
        // jLabel19 (Angka '0') DIHAPUS
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        btnLaporanTransaksi = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        // jLabel22 (Angka '0') DIHAPUS
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnLaporanPenjualan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(109, 148, 197));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logosakpore-small.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logologin-removebg-preview-resize.png"))); // NOI18N

        lblTanggal.setFont(new java.awt.Font("GeoSlab703 Md BT", 0, 24)); // NOI18N
        lblTanggal.setText("Rabu, 10 September 2025"); 

        jLabel3.setFont(new java.awt.Font("GeoSlab703 Md BT", 0, 18)); // NOI18N
        jLabel3.setText("Admin"); 

        btnLogout.setBackground(new java.awt.Color(161, 194, 189));
        btnLogout.setText("LOGOUT [ESC]"); // Menggabungkan teks dari kedua versi
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        
        // LAYOUT HEADER - Horizontal
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                // Logo Sakpore & Logout
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE) 
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                
                // Icon, Role, dan Tanggal di pojok kanan
                .addComponent(jLabel2) // Icon Orang
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3) // Role
                .addGap(30, 30, 30) // Jarak antara Role dan Tanggal
                .addComponent(lblTanggal) // Tanggal
                .addGap(67, 67, 67)) // Padding kanan
        );
        
        // LAYOUT HEADER - Vertical
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20) // Padding atas
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Baris 1: Logo, Icon, Role, Tanggal
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(lblTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Baris 2: Tombol Logout (di bawah logo)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE) 
                .addContainerGap(20, Short.MAX_VALUE)) // Padding bawah
        );

        jPanel1.setBackground(new java.awt.Color(161, 194, 189));

        // Angka 0 (jLabel4) DIHAPUS

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/user (1) (1).png"))); // NOI18N

        btnUserManager.setBackground(new java.awt.Color(161, 194, 189));
        btnUserManager.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUserManager.setText("USER MANAGER");
        btnUserManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserManagerActionPerformed(evt); 
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel5))
                    //.addComponent(jLabel4) // DIHAPUS
                    .addComponent(btnUserManager))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(jLabel4) // DIHAPUS
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE) // Jarak disesuaikan
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(btnUserManager)
                .addGap(49, 49, 49))
        );

        jPanel3.setBackground(new java.awt.Color(161, 194, 189));

        // Angka 0 (jLabel6) DIHAPUS

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/supplier (1).png"))); // NOI18N

        btnSupplier.setBackground(new java.awt.Color(161, 194, 189));
        btnSupplier.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSupplier.setText("SUPPLIER");
        btnSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel8))
                    //.addComponent(jLabel6) // DIHAPUS
                    .addComponent(btnSupplier))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(31, 31, 31))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(jLabel6) // DIHAPUS
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE) // Jarak disesuaikan
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(btnSupplier)
                .addGap(49, 49, 49))
        );

        jPanel4.setBackground(new java.awt.Color(161, 194, 189));

        // Angka 0 (jLabel10) DIHAPUS

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/boxes (1).png"))); // NOI18N

        btnDataBarang.setBackground(new java.awt.Color(161, 194, 189));
        btnDataBarang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDataBarang.setText("DATA BARANG");
        btnDataBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataBarangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel11))
                    //.addComponent(jLabel10) // DIHAPUS
                    .addComponent(btnDataBarang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(31, 31, 31))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(jLabel10) // DIHAPUS
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE) // Jarak disesuaikan
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(btnDataBarang)
                .addGap(49, 49, 49))
        );
        
        // BAGIAN KODE UNTUK JPanels 5 (KATEGORI) dan 6 (TRANSAKSI) DIHAPUS.

        jPanel7.setBackground(new java.awt.Color(161, 194, 189));

        // Angka 0 (jLabel19) DIHAPUS

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/report.png"))); // NOI18N

        btnLaporanTransaksi.setBackground(new java.awt.Color(161, 194, 189));
        btnLaporanTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLaporanTransaksi.setText("LAPORAN TRANSAKSI");
        btnLaporanTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporanTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel20))
                    //.addComponent(jLabel19) // DIHAPUS
                    .addComponent(btnLaporanTransaksi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(31, 31, 31))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(jLabel19) // DIHAPUS
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE) // Jarak disesuaikan
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(btnLaporanTransaksi)
                .addGap(49, 49, 49))
        );

        jPanel8.setBackground(new java.awt.Color(161, 194, 189));

        // Angka 0 (jLabel22) DIHAPUS

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/bar-chart.png"))); // NOI18N

        btnLaporanPenjualan.setBackground(new java.awt.Color(161, 194, 189));
        btnLaporanPenjualan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLaporanPenjualan.setText("LAPORAN PENJUALAN");
        btnLaporanPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporanPenjualanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel23))
                    //.addComponent(jLabel22) // DIHAPUS
                    .addComponent(btnLaporanPenjualan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addGap(31, 31, 31))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //.addComponent(jLabel22) // DIHAPUS
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE) // Jarak disesuaikan
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(btnLaporanPenjualan)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        // HORIZONTAL GROUP UTAMA (Layout disesuaikan menjadi 2 kolom simetris)
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup() 
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) 
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    
                    // Baris 1: User Manager | Data Barang
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40) // Jarak antar kolom
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    
                    // Baris 2: Supplier | Laporan Transaksi
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40) // Jarak antar kolom
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))

                    // Baris 3: Laporan Penjualan (Rata Tengah)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) 
                )
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        // VERTICAL GROUP UTAMA (Layout disesuaikan)
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40) // Jarak atas ke baris 1
                
                // Baris 1: User Manager | Data Barang
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40) // Jarak baris 1 ke baris 2
                
                // Baris 2: Supplier | Laporan Transaksi
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40) // Jarak baris 2 ke baris 3

                // Baris 3: Laporan Penjualan
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE)) // Jarak bawah
        );

        pack();
    }// </editor-fold>

    // =======================================================
    // ACTION LISTENERS (Fungsionalitas fitur lain TIDAK DIUBAH)
    // =======================================================
    

    // LOGIKA NAVIGASI USER MANAGER (TIDAK DIUBAH)
    private void btnUserManagerActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.setVisible(false); 
            
            // Panggil constructor UserManager yang menerima parent frame (this)
            // PASTIKAN KELAS UserManager ADA DAN MEMILIKI CONSTRUCTOR UserManager(JFrame parent)
            UserManager userManagerForm = new UserManager(this); 
            userManagerForm.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            userManagerForm.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace(); 
            
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat Form User Manager. Cek koneksi database atau error di UserManager.java.", 
                "Error Navigasi", 
                JOptionPane.ERROR_MESSAGE);
                
            this.setVisible(true); // Tampilkan kembali dashboard jika navigasi gagal
        }
    }

    // Aksi untuk tombol Supplier (Diperbaiki agar sesuai dengan skenario nyata)
    private void btnSupplierActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumsi nama kelas untuk Supplier adalah TambahSupplier, jika berbeda, harap sesuaikan.
        try {
            // Gunakan reflection jika kelas TambahSupplier belum diimport atau tidak ada
            Class<?> supplierClass = Class.forName("kasirsakpore.TambahSupplier");
            java.lang.reflect.Constructor<?> constructor = supplierClass.getConstructor(JFrame.class);
            JFrame formSupplier = (JFrame) constructor.newInstance(this);
            formSupplier.setVisible(true);
            this.dispose(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat form Supplier. Pastikan kelas TambahSupplier tersedia: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Aksi untuk tombol Data Barang (TIDAK DIUBAH)
    private void btnDataBarangActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // PASTIKAN KELAS DataBarang ADA DAN MEMILIKI CONSTRUCTOR DataBarang(JFrame parent)
            // Asumsi DataBarang ada di package kasirsakpore
            Class<?> barangClass = Class.forName("kasirsakpore.DataBarang");
            java.lang.reflect.Constructor<?> constructor = barangClass.getConstructor(JFrame.class);
            JFrame formBarang = (JFrame) constructor.newInstance(this);
            formBarang.setVisible(true);
            this.setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat form Data Barang: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // btnKategoriActionPerformed DIHAPUS

    // btnTransaksiActionPerformed DIHAPUS

    // Aksi untuk tombol Laporan Transaksi (TIDAK DIUBAH)
    private void btnLaporanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Membuat instance LaporanTransaksi
            // PASTIKAN KELAS LaporanTransaksi ADA dan memiliki constructor default
            LaporanTransaksi laporanForm = new LaporanTransaksi(); 
            // Menampilkan form Laporan Transaksi
            laporanForm.setVisible(true);
            
            // Jika Anda ingin Dashboard Admin tetap terbuka, jangan gunakan:
            // this.setVisible(false); 
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat Form Laporan Transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Aksi untuk tombol Laporan Penjualan (Telah Diubah)
    private void btnLaporanPenjualanActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Sembunyikan frame Dashboard saat ini
            this.setVisible(false);
            
            // Buat instance dari LaporanPenjualan, berikan 'this' (DashboardAdmin) sebagai parent
            LaporanPenjualan laporan = new LaporanPenjualan(this); 
            
            // Tampilkan frame LaporanPenjualan
            laporan.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maksimalkan jendela
            laporan.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat form Laporan Penjualan. Pastikan file LaporanPenjualan.java tersedia di package 'kasirsakpore': " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true); // Tampilkan kembali dashboard jika navigasi gagal
            e.printStackTrace();
        }
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        performLogout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            // Menggunakan constructor default untuk testing
            new DashboardAdmin().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDataBarang;
    private javax.swing.JButton btnLaporanPenjualan;
    private javax.swing.JButton btnLaporanTransaksi;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSupplier;
    private javax.swing.JButton btnUserManager;
    private javax.swing.JLabel jLabel1;
    // jLabel4, jLabel6, jLabel10, jLabel13, jLabel16, jLabel19, jLabel22 (Angka '0') DIHAPUS
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    // jLabel13, 14, 15 (Kategori) dan 16, 17, 18 (Transaksi) DIHAPUS
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // jPanel5 (Kategori) dan jPanel6 (Transaksi) DIHAPUS
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lblTanggal;
    // End of variables declaration//GEN-END:variables
}