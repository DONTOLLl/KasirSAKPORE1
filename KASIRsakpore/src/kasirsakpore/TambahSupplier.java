/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JFrame; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; 

/**
 * Form untuk mengelola Data Supplier (CRUD) - Versi Fullscreen, Rapi, AutoNumber, dan Navigasi.
 * @author Acer
 */
public class TambahSupplier extends javax.swing.JFrame {

    // --- VARIABEL LOGIKA DATABASE ---
    private javax.swing.JFrame parentFrame; // <<< FIELD UNTUK MENYIMPAN DASHBOARD
    private DefaultTableModel modelTabelSupplier;
    private String selectedKodeSupplier; 
    // --------------------------------

    // --- Komponen yang Diperlukan oleh Logika ---
    // Deklarasi ini diperlukan agar method di luar initComponents dapat mengakses komponen
    private javax.swing.JTextField txtKodeSupplier;
    private javax.swing.JTextField txtNamaSupplier;
    private javax.swing.JTextField txtTelepon;
    private javax.swing.JTextArea txtAlamat;
    private javax.swing.JTable jTableSupplier;
    private javax.swing.JTextField txtPencarian;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnCari;
    
    /**
     * Creates new form TambahSupplier
     */
    
    // Constructor Default (untuk testing/NetBeans designer)
    public TambahSupplier() {
        this(null); // Panggil constructor utama dengan parent null
    }
    
    // <<< CONSTRUCTOR BARU: Menerima Frame Dashboard Admin
    public TambahSupplier(javax.swing.JFrame parent) {
        this.parentFrame = parent; // Simpan referensi Dashboard Admin
        initComponents();
        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Mengatur Fullscreen
        this.setLocationRelativeTo(null);
        setupTable();
        load_table();
        autonumber(); 
        kosong();
        setupEscapeKey(); // <<< Mengaktifkan shortcut ESC
    }
    // ------------------------------------------
    
    // --- METODE BARU: AUTONUMBER ---
    public void autonumber() {
        try {
            Connection conn = KoneksiDB.getKoneksi();
            // Menggunakan CAST(SUBSTRING(...)) untuk mendapatkan nilai angka setelah prefix "SPL"
            String sql = "SELECT MAX(CAST(SUBSTRING(kode_supplier, 4) AS INT)) FROM supplier";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int maxKode = rs.getInt(1);
                int nextKode = maxKode + 1;
                String kodeBaru = String.format("SPL%03d", nextKode); // SPL001, SPL002, dst.
                txtKodeSupplier.setText(kodeBaru);
            } else {
                txtKodeSupplier.setText("SPL001"); // Jika tabel kosong
            }
            
            txtKodeSupplier.setEditable(false); 
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.out.println("Error AutoNumber: " + e.getMessage());
            txtKodeSupplier.setText("SPL001"); 
        }
    }
    
    // --- METODE BARU: SETUP & UTILITY ---
    
    private void setupTable() {
        modelTabelSupplier = new DefaultTableModel();
        modelTabelSupplier.addColumn("Kode Supplier");
        modelTabelSupplier.addColumn("Nama Supplier");
        modelTabelSupplier.addColumn("No. Telepon");
        modelTabelSupplier.addColumn("Alamat");
        jTableSupplier.setModel(modelTabelSupplier); 
        jTableSupplier.setRowHeight(30); 
    }
    
    // <<< METODE BARU: SETUP ESCAPE KEY
    private void setupEscapeKey() {
        InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnKembaliActionPerformed(e); // Dijalankan saat ESC ditekan
            }
        });
    }

    private void kosong() {
        txtNamaSupplier.setText("");
        txtTelepon.setText("");
        txtAlamat.setText("");
        txtPencarian.setText("");
        
        autonumber(); 
        
        selectedKodeSupplier = null;
        btnSimpan.setText("SIMPAN");
        btnSimpan.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }
    
    // --- LOGIKA UTAMA (CRUD) ---

    public void load_table() {
        modelTabelSupplier.setRowCount(0);
        try {
            String sql = "SELECT kode_supplier, nama_supplier, no_telepon, alamat FROM supplier ORDER BY kode_supplier ASC"; 
            Connection conn = KoneksiDB.getKoneksi();
            
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi ke database GAGAL! (Cek KoneksiDB.java Anda)", "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelTabelSupplier.addRow(new Object[]{
                    rs.getString("kode_supplier"),
                    rs.getString("nama_supplier"),
                    rs.getString("no_telepon"), 
                    rs.getString("alamat")
                });
            }
            
            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "‼️ GAGAL MEMUAT DATA SUPPLIER ‼️\n"
                                          + "Detail Error SQL: " + e.getMessage(), 
                                          "Error SQL: Kesalahan Query/Schema", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Simpan Data
    private void simpanData() {
        String kode = txtKodeSupplier.getText().trim();
        String nama = txtNamaSupplier.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || telepon.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String sql = "INSERT INTO supplier (kode_supplier, nama_supplier, no_telepon, alamat) VALUES (?, ?, ?, ?)";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kode);
            ps.setString(2, nama);
            ps.setString(3, telepon);
            ps.setString(4, alamat);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            load_table();
            kosong();
            
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Gagal menyimpan data (Kode Supplier mungkin sudah ada atau Error SQL).\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Edit Data
    private void editData() {
        if (selectedKodeSupplier == null) return;
        
        String nama = txtNamaSupplier.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String alamat = txtAlamat.getText().trim();
        
        if (nama.isEmpty() || telepon.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String sql = "UPDATE supplier SET nama_supplier = ?, no_telepon = ?, alamat = ? WHERE kode_supplier = ?";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, telepon);
            ps.setString(3, alamat);
            ps.setString(4, selectedKodeSupplier); 
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
            load_table();
            kosong();
            
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Gagal mengedit data.\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Hapus Data
    private void hapusData() {
        if (selectedKodeSupplier == null) return;
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus supplier dengan kode: " + selectedKodeSupplier + "?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
             try {
                String sql = "DELETE FROM supplier WHERE kode_supplier = ?";
                Connection conn = KoneksiDB.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, selectedKodeSupplier);
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                load_table();
                kosong();
                
            } catch (SQLException e) {
                 JOptionPane.showMessageDialog(this, "Gagal menghapus data.\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Cari Data
    private void cariData() {
        modelTabelSupplier.setRowCount(0);
        String keyword = txtPencarian.getText().trim();
        if (keyword.isEmpty()) {
            load_table();
            return;
        }

        try {
            // Menggunakan ILIKE untuk pencarian case-insensitive
            String sql = "SELECT * FROM supplier WHERE kode_supplier ILIKE ? OR nama_supplier ILIKE ?";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelTabelSupplier.addRow(new Object[]{
                    rs.getString("kode_supplier"),
                    rs.getString("nama_supplier"),
                    rs.getString("no_telepon"),
                    rs.getString("alamat")
                });
            }
            
            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error pencarian: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }


    // --- LOGIKA EVENT HANDLER ---

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                        
        if ("SIMPAN".equals(btnSimpan.getText())) {
            simpanData();
        }
    }                                       

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {                                      
        editData();
    }                                     

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {                                       
        hapusData();
    }                                      

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {                                       
        kosong();
    }                                      

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // <<< IMPLEMENTASI KEMBALI KE DASHBOARD
        if (parentFrame != null) {
            parentFrame.setVisible(true); // Tampilkan Dashboard Admin
        }
        this.dispose(); // Tutup form TambahSupplier
    }                                        
    
    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {                                      
        cariData();
    }                                     
    
    private void jTableSupplierMouseClicked(java.awt.event.MouseEvent evt) {                                            
        int selectedRow = jTableSupplier.getSelectedRow();
        if (selectedRow == -1) return;

        selectedKodeSupplier = jTableSupplier.getValueAt(selectedRow, 0).toString();
        txtKodeSupplier.setText(selectedKodeSupplier);
        txtNamaSupplier.setText(jTableSupplier.getValueAt(selectedRow, 1).toString());
        txtTelepon.setText(jTableSupplier.getValueAt(selectedRow, 2).toString());
        txtAlamat.setText(jTableSupplier.getValueAt(selectedRow, 3).toString());
        
        txtKodeSupplier.setEditable(false);
        btnSimpan.setEnabled(false);
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
    } 

    private void txtPencarianKeyPressed(java.awt.event.KeyEvent evt) {                                      
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cariData();
        }
    }                                     


    // --- NETBEANS GENERATED CODE (Manual Layout for Cleanliness) ---
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // --- INISIALISASI KOMPONEN ---
        javax.swing.JPanel jPanelBackground = new javax.swing.JPanel();
        javax.swing.JPanel jPanelHeader = new javax.swing.JPanel();
        javax.swing.JLabel jLabelTitle = new javax.swing.JLabel();
        btnKembali = new javax.swing.JButton();
        javax.swing.JPanel jPanelMainContent = new javax.swing.JPanel();
        javax.swing.JPanel jPanelInput = new javax.swing.JPanel();
        javax.swing.JLabel jLabelKode = new javax.swing.JLabel();
        txtKodeSupplier = new javax.swing.JTextField();
        javax.swing.JLabel jLabelNama = new javax.swing.JLabel();
        txtNamaSupplier = new javax.swing.JTextField();
        javax.swing.JLabel jLabelTelepon = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        javax.swing.JLabel jLabelAlamat = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPaneAlamat = new javax.swing.JScrollPane();
        txtAlamat = new javax.swing.JTextArea();
        javax.swing.JPanel jPanelAksi = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        javax.swing.JPanel jPanelTabel = new javax.swing.JPanel();
        txtPencarian = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPaneTable = new javax.swing.JScrollPane();
        jTableSupplier = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manajemen Data Supplier");
        
        // --- LAYOUT UTAMA (BORDER LAYOUT) ---
        
        jPanelBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPanelBackground.setLayout(new java.awt.BorderLayout()); 

        // --- HEADER (NORTH) ---
        jPanelHeader.setBackground(new java.awt.Color(58, 151, 151));
        jPanelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        java.awt.GridBagLayout jPanelHeaderLayout = new java.awt.GridBagLayout();
        java.awt.GridBagConstraints gbcHeader = new java.awt.GridBagConstraints();
        gbcHeader.insets = new java.awt.Insets(5, 10, 5, 10);
        
        jPanelHeader.setLayout(jPanelHeaderLayout);

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 32));
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setText("MANAJEMEN DATA SUPPLIER");
        gbcHeader.gridx = 1; gbcHeader.gridy = 0; gbcHeader.anchor = java.awt.GridBagConstraints.CENTER; gbcHeader.weightx = 1.0;
        jPanelHeader.add(jLabelTitle, gbcHeader);

        btnKembali.setText("Kembali (ESC)");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);
        gbcHeader.gridx = 0; gbcHeader.gridy = 0; gbcHeader.anchor = java.awt.GridBagConstraints.WEST; gbcHeader.weightx = 0;
        jPanelHeader.add(btnKembali, gbcHeader);

        jPanelBackground.add(jPanelHeader, java.awt.BorderLayout.NORTH);

        // --- MAIN CONTENT (CENTER) ---
        jPanelMainContent.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMainContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanelMainContent.setLayout(new java.awt.BorderLayout(15, 0)); 

        // --- INPUT PANEL (WEST) ---
        jPanelInput.setBackground(new java.awt.Color(245, 245, 245));
        jPanelInput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detail Supplier", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14)));
        jPanelInput.setPreferredSize(new java.awt.Dimension(380, 500)); 
        
        java.awt.GridBagLayout jPanelInputLayout = new java.awt.GridBagLayout();
        java.awt.GridBagConstraints gbcInput = new java.awt.GridBagConstraints();
        gbcInput.insets = new java.awt.Insets(8, 10, 8, 10);
        gbcInput.anchor = java.awt.GridBagConstraints.WEST;
        jPanelInput.setLayout(jPanelInputLayout);
        
        final int TXT_HEIGHT = 35;

        // Kode Supplier
        jLabelKode.setText("Kode Supplier:");
        gbcInput.gridx = 0; gbcInput.gridy = 0; gbcInput.weightx = 0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(jLabelKode, gbcInput);
        txtKodeSupplier.setPreferredSize(new java.awt.Dimension(250, TXT_HEIGHT)); 
        gbcInput.gridx = 1; gbcInput.gridy = 0; gbcInput.weightx = 1.0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(txtKodeSupplier, gbcInput);

        // Nama Supplier
        jLabelNama.setText("Nama Supplier:");
        gbcInput.gridx = 0; gbcInput.gridy = 1; gbcInput.weightx = 0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(jLabelNama, gbcInput);
        txtNamaSupplier.setPreferredSize(new java.awt.Dimension(250, TXT_HEIGHT)); 
        gbcInput.gridx = 1; gbcInput.gridy = 1; gbcInput.weightx = 1.0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(txtNamaSupplier, gbcInput);

        // No. Telepon
        jLabelTelepon.setText("No. Telepon:");
        gbcInput.gridx = 0; gbcInput.gridy = 2; gbcInput.weightx = 0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(jLabelTelepon, gbcInput);
        txtTelepon.setPreferredSize(new java.awt.Dimension(250, TXT_HEIGHT)); 
        gbcInput.gridx = 1; gbcInput.gridy = 2; gbcInput.weightx = 1.0; gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.add(txtTelepon, gbcInput);

        // Alamat
        jLabelAlamat.setText("Alamat:");
        gbcInput.gridx = 0; gbcInput.gridy = 3; gbcInput.weightx = 0; gbcInput.anchor = java.awt.GridBagConstraints.NORTHWEST; gbcInput.insets = new Insets(10, 10, 8, 10);
        jPanelInput.add(jLabelAlamat, gbcInput);
        
        txtAlamat.setColumns(20);
        txtAlamat.setRows(5);
        jScrollPaneAlamat.setViewportView(txtAlamat);
        jScrollPaneAlamat.setPreferredSize(new java.awt.Dimension(250, 100));
        gbcInput.gridx = 1; gbcInput.gridy = 3; gbcInput.weightx = 1.0; gbcInput.weighty = 0.5; 
        gbcInput.fill = java.awt.GridBagConstraints.BOTH; 
        jPanelInput.add(jScrollPaneAlamat, gbcInput);

        // Buttons Aksi
        jPanelAksi.setBackground(new java.awt.Color(245, 245, 245));
        jPanelAksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        
        btnSimpan.setText("SIMPAN");
        btnSimpan.setBackground(new java.awt.Color(40, 167, 69)); 
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setPreferredSize(new java.awt.Dimension(80, 40));
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);
        jPanelAksi.add(btnSimpan);
        
        btnEdit.setText("EDIT");
        btnEdit.setBackground(new java.awt.Color(255, 193, 7)); 
        btnEdit.setForeground(new java.awt.Color(0, 0, 0));
        btnEdit.setPreferredSize(new java.awt.Dimension(80, 40));
        btnEdit.addActionListener(this::btnEditActionPerformed);
        jPanelAksi.add(btnEdit);
        
        btnHapus.setText("HAPUS");
        btnHapus.setBackground(new java.awt.Color(220, 53, 69)); 
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setPreferredSize(new java.awt.Dimension(80, 40));
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        jPanelAksi.add(btnHapus);
        
        btnBatal.setText("BATAL");
        btnBatal.setPreferredSize(new java.awt.Dimension(80, 40));
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        jPanelAksi.add(btnBatal);

        gbcInput.gridx = 0; gbcInput.gridy = 4; gbcInput.gridwidth = 2; 
        gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbcInput.weighty = 0.5; 
        gbcInput.anchor = java.awt.GridBagConstraints.NORTH; 
        gbcInput.insets = new java.awt.Insets(20, 10, 5, 10);
        jPanelInput.add(jPanelAksi, gbcInput);


        jPanelMainContent.add(jPanelInput, java.awt.BorderLayout.WEST);

        // --- TABEL PANEL (CENTER) ---
        jPanelTabel.setBackground(new java.awt.Color(255, 255, 255));
        jPanelTabel.setLayout(new java.awt.BorderLayout(0, 10)); 
        jPanelTabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

        // Search Panel (NORTH Tabel)
        javax.swing.JPanel jPanelSearch = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        jPanelSearch.setBackground(new java.awt.Color(255, 255, 255));
        
        txtPencarian.setPreferredSize(new java.awt.Dimension(300, 35));
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPencarianKeyPressed(evt);
            }
        });
        jPanelSearch.add(txtPencarian);

        btnCari.setText("Cari");
        btnCari.setPreferredSize(new java.awt.Dimension(80, 35));
        btnCari.addActionListener(this::btnCariActionPerformed);
        jPanelSearch.add(btnCari);
        
        jPanelTabel.add(jPanelSearch, java.awt.BorderLayout.NORTH);
        
        // JTable (CENTER Tabel)
        jTableSupplier.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"Kode Supplier", "Nama Supplier", "No. Telepon", "Alamat"}
        ));
        jTableSupplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSupplierMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(jTableSupplier);
        jPanelTabel.add(jScrollPaneTable, java.awt.BorderLayout.CENTER); 

        jPanelMainContent.add(jPanelTabel, java.awt.BorderLayout.CENTER);
        
        jPanelBackground.add(jPanelMainContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelBackground, java.awt.BorderLayout.CENTER); 

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // --- MAIN METHOD UNTUK TESTING ---
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
            java.util.logging.Logger.getLogger(TambahSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TambahSupplier().setVisible(true);
            }
        });
    }
}