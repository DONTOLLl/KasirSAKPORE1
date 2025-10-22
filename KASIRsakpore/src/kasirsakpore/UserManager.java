/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JFrame; // Digunakan untuk parent frame
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; // Asumsi package koneksi Anda bernama koneksi

/**
 *
 * @author Acer
 */
public class UserManager extends javax.swing.JFrame {

    // MODIFIKASI: Variabel untuk menyimpan referensi parent frame
    private JFrame parentFrame; 

    /**
     * Creates new form UserManager
     */
    public UserManager() {
        // Panggil constructor utama dengan nilai null (untuk NetBeans Designer)
        this(null); 
    }

    /**
     * MODIFIKASI: Constructor baru yang menerima parent frame
     */
    public UserManager(JFrame parent) {
        this.parentFrame = parent; // Simpan referensi parent
        initComponents();
        this.setLocationRelativeTo(null); // Posisikan di tengah layar
        // FIX PENTING: Atur agar frame dibuka dalam mode MAXIMIZED_BOTH
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        load_table();
        kosong(); // Reset field saat pertama kali dibuka
        setupEscapeKey(); // Panggil method shortcut
    }

    /**
     * FUNGSI UTAMA: Logika untuk menutup frame ini dan menampilkan Dashboard.
     */
    private void kembaliKeDashboard() {
        // 1. Tutup jendela UserManager saat ini
        this.dispose();

        // 2. Tampilkan kembali parent frame (Dashboard)
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }

    /**
     * MODIFIKASI: Method untuk mengaktifkan shortcut tombol ESC dan kembali ke Dashboard.
     */
    private void setupEscapeKey() {
        // Ambil InputMap untuk frame ketika frame sedang fokus
        InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Bind tombol ESCAPE (VK_ESCAPE)
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(escapeKeyStroke, "escapeAction");

        // Definisikan aksi yang terjadi saat ESC ditekan
        actionMap.put("escapeAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Panggil logika kembali
                kembaliKeDashboard();
            }
        });
    }

    // --- DATABASE OPERATIONS ---

    /**
     * Mengosongkan field input dan mengatur ulang tampilan.
     */
    private void kosong() {
        // Mengosongkan Field
        txtIdPengguna.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        
        // Mengatur ulang Combobox
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);

        // Mengaktifkan tombol yang sesuai
        btnSimpan.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        txtIdPengguna.setEditable(true); // ID bisa diisi saat mode simpan
    }
    
    /**
     * Memuat data pengguna dari database ke dalam jTable1, diurutkan ASC.
     */
    private void load_table() {
        // Nama-nama kolom untuk JTable
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password"); 
        model.addColumn("Role");
        model.addColumn("Status");
        
        try {
            Connection conn = KoneksiDB.getKoneksi();
            // Ambil semua data pengguna dan pastikan diurutkan berdasarkan ID terkecil (ASC)
            String sql = "SELECT id_pengguna, username, password, role, status FROM pengguna ORDER BY id_pengguna ASC"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pengguna"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                });
            }
            jTable1.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mencari data pengguna berdasarkan kata kunci, tetap diurutkan ASC.
     */
    private void cariData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Role");
        model.addColumn("Status");
        
        String keyword = pencarian.getText();
        
        try {
            Connection conn = KoneksiDB.getKoneksi();
            // Mencari data dan mengurutkannya berdasarkan ID terkecil (ASC)
            String sql = "SELECT id_pengguna, username, password, role, status FROM pengguna "
                       + "WHERE CAST(id_pengguna AS TEXT) ILIKE ? OR username ILIKE ? OR role ILIKE ? "
                       + "ORDER BY id_pengguna ASC"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pengguna"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                });
            }
            jTable1.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mencari data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdPengguna = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        cmbRole = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        pencarian = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnKembali = new javax.swing.JButton(); 

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); 
        setTitle("Manajemen Pengguna");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("ID Pengguna");

        jLabel2.setText("Username");

        jLabel3.setText("Password");

        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "KASIR", "LAPORAN" }));

        jLabel4.setText("Role");

        jLabel5.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Not active" }));

        btnSimpan.setText("SIMPAN");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnEdit.setText("EDIT");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("HAPUS");
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnBatal.setText("BATAL");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jLabel6.setText("Cari Data:");

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(58, 151, 151));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("MANAJEMEN PENGGUNA");

        // Perbaikan: Ikon dihilangkan sementara untuk menghindari NullPointerException
        jLabel9.setText(""); 
        
        // MODIFIKASI: Konfigurasi tombol Kembali
        btnKembali.setText("KEMBALI [ESC]"); 
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt); // Panggil fungsi kembali
            }
        });


        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE) 
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Jarak
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE) // Tambahkan tombol kembali
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)) // Atur tinggi tombol
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        // --- FIX LAYOUT HORIZONTAL: Membuat semua komponen merentang penuh ---
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // FIX: Membuat JScrollPane merentang penuh secara horizontal
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE) // Set size minimal 750, lalu MAX_VALUE
                    
                    // Mengatur area input dan tombol agar merentang penuh secara horizontal
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdPengguna, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txtUsername)
                            .addComponent(txtPassword))
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbRole, 0, 160, Short.MAX_VALUE)
                            .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Jarak fleksibel (penting!)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(30, 30, 30))
            
            // Mengatur area pencarian agar tetap di kanan atas
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        
        // --- FIX LAYOUT VERTICAL: Membuat JScrollPane mengambil sisa tinggi ---
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdPengguna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btnSimpan)
                    .addComponent(btnHapus))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit)
                    .addComponent(btnBatal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // FIX: Menggunakan Short.MAX_VALUE untuk JScrollPane agar mengisi sisa ruang
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE) 
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Panggil fungsi kembali saat tombol KEMBALI [ESC] ditekan
        kembaliKeDashboard();
    }
    
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // --- CREATE OPERATION ---
        try {
            Connection conn = KoneksiDB.getKoneksi();
            
            if (txtIdPengguna.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cek jika ID sudah ada
            String checkSql = "SELECT COUNT(*) FROM pengguna WHERE id_pengguna = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, Integer.parseInt(txtIdPengguna.getText()));
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "ID Pengguna sudah ada. Gunakan ID lain.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Query INSERT
            String sql = "INSERT INTO pengguna (id_pengguna, username, password, role, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(txtIdPengguna.getText()));
            ps.setString(2, txtUsername.getText());
            ps.setString(3, txtPassword.getText());
            ps.setString(4, cmbRole.getSelectedItem().toString());
            ps.setString(5, cmbStatus.getSelectedItem().toString());
            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
            load_table();
            kosong();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data:\n" + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "ID Pengguna harus berupa angka.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // --- UPDATE OPERATION ---
        try {
            Connection conn = KoneksiDB.getKoneksi();
            
            if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // ID tidak boleh diubah, hanya field lain
            String sql = "UPDATE pengguna SET username=?, password=?, role=?, status=? WHERE id_pengguna=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, txtUsername.getText());
            ps.setString(2, txtPassword.getText());
            ps.setString(3, cmbRole.getSelectedItem().toString());
            ps.setString(4, cmbStatus.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtIdPengguna.getText()));
            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Data berhasil diupdate.");
            load_table();
            kosong();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data:\n" + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // --- DELETE OPERATION ---
        int konfirmasi = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == 0) { // Jika user memilih YES
            try {
                Connection conn = KoneksiDB.getKoneksi();
                String sql = "DELETE FROM pengguna WHERE id_pengguna=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(txtIdPengguna.getText()));
                
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus.");
                load_table();
                kosong();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data:\n" + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // Mengembalikan form ke keadaan awal
        kosong();
        load_table(); // Muat ulang tabel untuk menghilangkan filter pencarian
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // Memicu fungsi pencarian
        cariData();
    }//GEN-LAST:event_btnCariActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // --- READ OPERATION (Click Row) ---
        // Mengisi field dengan data dari baris yang diklik
        int baris = jTable1.rowAtPoint(evt.getPoint());
        
        String id = jTable1.getValueAt(baris, 0).toString();
        txtIdPengguna.setText(id);
        
        String user = jTable1.getValueAt(baris, 1).toString();
        txtUsername.setText(user);
        
        String pass = jTable1.getValueAt(baris, 2).toString();
        txtPassword.setText(pass);
        
        String role = jTable1.getValueAt(baris, 3).toString();
        cmbRole.setSelectedItem(role);
        
        String status = jTable1.getValueAt(baris, 4).toString();
        cmbStatus.setSelectedItem(status);
        
        // Mengatur status tombol setelah data dimuat
        btnSimpan.setEnabled(false);
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
        txtIdPengguna.setEditable(false); // ID tidak bisa diubah saat edit
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali; // MODIFIKASI: Deklarasi Tombol Kembali
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField pencarian;
    private javax.swing.JTextField txtIdPengguna;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}