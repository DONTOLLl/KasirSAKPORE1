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
import java.util.Date; 
import java.text.SimpleDateFormat; 
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; 

/**
 * Form untuk menangani Transaksi Restock (Multi-Item Restock).
 * @author Acer
 */
public class TransaksiRestock extends javax.swing.JFrame {

    private JFrame parentFrame; 
    private DefaultTableModel modelTabelRestock;

    // --- CONSTRUCTORS ---

    // TEMPORARY FIX: Constructor 4-argumen untuk mengatasi error kompilasi DataBarang
    public TransaksiRestock(JFrame parent, String initialKode, String initialNama, String placeholder) {
        // Panggil constructor 3-argumen yang menerima initial item
        this(parent, initialKode, initialNama); 
    }
    
    // Constructor 1: Default (untuk Testing/NetBeans Designer)
    public TransaksiRestock() {
        this(null, null, null);
    }
    
    // Constructor 2: Dipanggil dari Dashboard/Menu (Transaksi Kosong)
    public TransaksiRestock(JFrame parent) {
        this(parent, null, null);
    }
    
    // Constructor 3: Dipanggil dari DataBarang (Pre-filled Item).
    public TransaksiRestock(JFrame parent, String initialKode, String initialNama) {
        this.parentFrame = parent;
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setLocationRelativeTo(null);
        
        setupTable();
        setTanggalOtomatis();
        setupEscapeKey();
        
        // Panggil loadSuppliers di awal untuk mengisi JComboBox
        loadSuppliers();
        
        txtIDRestock.setText("TRSTK-AUTO"); 
        
        // LOGIKA PENGISIAN FIELD INPUT
        if (initialKode != null && initialNama != null) {
            txtKodeBarang.setText(initialKode);
            txtNamaBarang.setText(initialNama);
            
            // Logika pengisian supplier akan ditangani oleh muatDataBarang()
            muatDataBarang(); 
            
            txtJmlRestock.requestFocus();
        } else {
            txtKodeBarang.requestFocus(); 
        }
    }
    
    // --- UTILITY & SETUP ---

    private void setupEscapeKey() {
        InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnKembaliActionPerformed(e); 
            }
        });
    }
    
    private void setTanggalOtomatis() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        String tanggalSekarang = sdf.format(new Date());
        txtTanggal.setText(tanggalSekarang);
    }
    
    private void setupTable() {
        modelTabelRestock = new DefaultTableModel();
        modelTabelRestock.addColumn("Kode Barang");
        modelTabelRestock.addColumn("Nama Barang");
        modelTabelRestock.addColumn("Nama Supplier"); // KOLOM BARU
        modelTabelRestock.addColumn("Jml Restock");
        jTableRestock.setModel(modelTabelRestock);
    }
    
    /**
     * Metode untuk memuat daftar supplier dari database ke JComboBox.
     * ASUMSI: Ada tabel 'supplier' dengan kolom 'nama_supplier'.
     */
    private void loadSuppliers() {
        // Hapus item lama dan tambahkan placeholder
        cmbNamaSupplier.removeAllItems(); 
        cmbNamaSupplier.addItem("- Pilih Supplier -"); 

        try {
            // QUERY: SELECT nama_supplier DARI TABEL supplier
            String sql = "SELECT nama_supplier FROM supplier ORDER BY nama_supplier ASC";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbNamaSupplier.addItem(rs.getString("nama_supplier"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            // Pesan error ini tidak akan menghentikan program, hanya memberi tahu jika load gagal.
            System.err.println("Gagal memuat daftar Supplier: " + e.getMessage());
        }
    }
    
    // --- LOGIKA MENCARI BARANG ---
    
    /**
     * Mengambil nama barang dan nama supplier default/terakhir 
     * dan memilihnya di JComboBox.
     * ASUMSI: Tabel `barang` memiliki kolom `nama_supplier`.
     */
    private void muatDataBarang() {
        String kode = txtKodeBarang.getText().trim();
        if (kode.isEmpty()) return;
        
        // Kosongkan field supplier sebelum memuat
        cmbNamaSupplier.setSelectedItem("- Pilih Supplier -");
        
        try {
            // QUERY: Ambil nama barang dan nama supplier
            String sql = "SELECT nama_barang, nama_supplier FROM barang WHERE kode_barang = ?";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kode);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                txtNamaBarang.setText(rs.getString("nama_barang")); 
                
                // Pilih Nama Supplier di ComboBox
                String supplierName = rs.getString("nama_supplier");
                if (supplierName != null && !supplierName.isEmpty()) {
                    // Mencari dan memilih item di ComboBox. 
                    // Jika nama_supplier di database tidak ada di daftar ComboBox, 
                    // item tidak akan terpilih.
                    cmbNamaSupplier.setSelectedItem(supplierName); 
                }
                
                txtJmlRestock.requestFocus(); 
            } else {
                JOptionPane.showMessageDialog(this, "Kode Barang tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                txtNamaBarang.setText("");
                txtKodeBarang.requestFocus();
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- LOGIKA TAMBAH KE TABEL SEMENTARA ---
    
    /**
     * Mengambil data dari field input, termasuk supplier dari JComboBox, dan menambahkannya ke tabel.
     */
    private void tambahKeTabel() {
        String kode = txtKodeBarang.getText().trim();
        String nama = txtNamaBarang.getText().trim();
        
        // AMBIL DATA SUPPLIER DARI COMBOBOX
        String supplier = (String) cmbNamaSupplier.getSelectedItem(); 
        
        // Cek validasi untuk Supplier
        if (supplier == null || supplier.equals("- Pilih Supplier -")) {
             JOptionPane.showMessageDialog(this, "Silakan pilih Nama Supplier dari daftar.", "Peringatan", JOptionPane.WARNING_MESSAGE);
             cmbNamaSupplier.requestFocus();
             return;
        }
        
        int jumlah;

        if (kode.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode atau Nama Barang tidak valid.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            jumlah = Integer.parseInt(txtJmlRestock.getText());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah Restock harus lebih dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah restock harus berupa angka.", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cek duplikasi di tabel sementara (berdasarkan Kode Barang)
        for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
            if (modelTabelRestock.getValueAt(i, 0).equals(kode)) {
                int jumlahLama = (int) modelTabelRestock.getValueAt(i, 3); // Indeks 3: Jml Restock
                int konfirmasi = JOptionPane.showConfirmDialog(this, 
                        "Barang ini sudah ada di daftar. Tambahkan " + jumlah + " lagi?", 
                        "Konfirmasi", JOptionPane.YES_NO_OPTION);
                
                if (konfirmasi == JOptionPane.YES_OPTION) {
                    modelTabelRestock.setValueAt(jumlahLama + jumlah, i, 3);
                    bersihkanInputRestock();
                    return;
                } else {
                    return;
                }
            }
        }
        
        // Tambahkan baris baru (dengan data Supplier)
        modelTabelRestock.addRow(new Object[]{kode, nama, supplier, jumlah});
        bersihkanInputRestock();
    }
    
    /**
     * Membersihkan field input restock.
     */
    private void bersihkanInputRestock() {
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        cmbNamaSupplier.setSelectedItem("- Pilih Supplier -"); // RESET COMBOBOX
        txtJmlRestock.setText("1");
        txtKodeBarang.requestFocus();
    }
    
    // --- LOGIKA SIMPAN TRANSAKSI RESTOCK KE DATABASE ---
    private void simpanTransaksiRestock() {
        if (modelTabelRestock.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Daftar restock masih kosong. Silakan tambahkan barang.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin untuk memproses transaksi Restock ini?", "Konfirmasi Restock", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement psUpdateStok = null;
            
            try {
                conn = KoneksiDB.getKoneksi();
                conn.setAutoCommit(false); 
                
                // HANYA UPDATE STOK (tidak menyimpan log restock)
                String sqlUpdateStok = "UPDATE barang SET stok = stok + ? WHERE kode_barang = ?";
                psUpdateStok = conn.prepareStatement(sqlUpdateStok);
                
                for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
                    String kodeBarang = modelTabelRestock.getValueAt(i, 0).toString();
                    // String namaSupplier = modelTabelRestock.getValueAt(i, 2).toString(); 
                    int jumlahRestock = (int) modelTabelRestock.getValueAt(i, 3); 
                    
                    psUpdateStok.setInt(1, jumlahRestock);
                    psUpdateStok.setString(2, kodeBarang);
                    psUpdateStok.addBatch(); 
                }
                
                psUpdateStok.executeBatch();
                
                conn.commit(); 
                
                JOptionPane.showMessageDialog(this, "Transaksi Restock Berhasil Diproses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                // Jika form ini dipanggil dari DataBarang, panggil metode muat ulang tabel di DataBarang
                // (Baris ini akan menyebabkan error jika DataBarang.java belum diupdate atau tidak tersedia)
                /*
                if (parentFrame instanceof DataBarang) {
                    ((DataBarang) parentFrame).load_table();
                }
                */
                
                bersihkanInputRestock();
                modelTabelRestock.setRowCount(0); 
                
            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback(); 
                } catch (SQLException ex) {
                    // ignore rollback error
                }
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi Restock. Detail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (psUpdateStok != null) psUpdateStok.close();
                    if (conn != null) {
                        conn.setAutoCommit(true); 
                    }
                } catch (SQLException e) {
                    // ignore close error
                }
            }
        }
    }
    

    // --- LOGIKA EVENT HANDLER ---
    
    private void txtKodeBarangKeyPressed(java.awt.event.KeyEvent evt) {                                         
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            muatDataBarang();
        }
    }                                        

    private void txtJmlRestockKeyPressed(java.awt.event.KeyEvent evt) {                                         
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tambahKeTabel();
        }
    }                                        

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                          
        tambahKeTabel();
    }                                         

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        int selectedRow = jTableRestock.getSelectedRow();
        if (selectedRow != -1) {
            modelTabelRestock.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }                                            

    private void btnSimpanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        simpanTransaksiRestock();
    }                                                  

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (this.parentFrame != null) {
            this.parentFrame.setVisible(true);
        }
        this.dispose(); 
    }                                        

    // --- METODE OTOMATIS NETBEANS (Desain) ---
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBackground = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtIDRestock = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        btnKembali = new javax.swing.JButton();
        jPanelInput = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtKodeBarang = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabelSupplier = new javax.swing.JLabel(); 
        // DEKLARASI BARU JComboBox DI LAYOUT
        cmbNamaSupplier = new javax.swing.JComboBox<>(); 
        // Akhir Deklarasi Baru
        jLabel5 = new javax.swing.JLabel();
        txtJmlRestock = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        jPanelTabel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRestock = new javax.swing.JTable();
        btnHapusItem = new javax.swing.JButton();
        jPanelFooter = new javax.swing.JPanel();
        btnSimpanTransaksi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Form Transaksi Restock Barang");

        jPanelBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPanelBackground.setLayout(new java.awt.BorderLayout(10, 10));

        // =================================================================
        // HEADER PANEL (Tanggal, ID, Title) 
        // =================================================================
        jPanelHeader.setBackground(new java.awt.Color(58, 151, 151));
        jPanelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        jPanelHeader.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 28));
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setText("TRANSAKSI RESTOCK BARANG");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        jPanelHeader.add(jLabelTitle, gbc);

        // ID Restock
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ID Restock:");
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        jPanelHeader.add(jLabel1, gbc);

        txtIDRestock.setEditable(false);
        txtIDRestock.setBackground(new java.awt.Color(230, 230, 230));
        txtIDRestock.setPreferredSize(new java.awt.Dimension(150, 30));
        gbc.gridx = 3; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        jPanelHeader.add(txtIDRestock, gbc);

        // Tanggal
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Tanggal:");
        gbc.gridx = 2; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        jPanelHeader.add(jLabel2, gbc);

        txtTanggal.setEditable(false);
        txtTanggal.setBackground(new java.awt.Color(230, 230, 230));
        txtTanggal.setPreferredSize(new java.awt.Dimension(150, 30));
        gbc.gridx = 3; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        jPanelHeader.add(txtTanggal, gbc);
        
        // Tombol Kembali
        btnKembali.setText("Kembali (ESC)");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        jPanelHeader.add(btnKembali, gbc);


        // =================================================================
        // INPUT BARANG PANEL - MODIFIKASI: Penggantian field Supplier
        // =================================================================
        jPanelInput.setBackground(new java.awt.Color(245, 245, 245));
        jPanelInput.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Barang Restock"));
        jPanelInput.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 10));

        // Kode Barang
        jLabel3.setText("Kode Barang:");
        jPanelInput.add(jLabel3);
        
        txtKodeBarang.setPreferredSize(new java.awt.Dimension(120, 30));
        txtKodeBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKodeBarangKeyPressed(evt);
            }
        });
        jPanelInput.add(txtKodeBarang);

        // Nama Barang (Display)
        jLabel4.setText("Nama Barang:");
        jPanelInput.add(jLabel4);
        
        txtNamaBarang.setEditable(false);
        txtNamaBarang.setPreferredSize(new java.awt.Dimension(250, 30));
        jPanelInput.add(txtNamaBarang);
        
        // NAMA SUPPLIER BARU (JCOMBOBOX)
        jLabelSupplier.setText("Nama Supplier:");
        jPanelInput.add(jLabelSupplier);
        
        // MODIFIKASI: Mengganti txtNamaSupplier dengan cmbNamaSupplier
        cmbNamaSupplier.setPreferredSize(new java.awt.Dimension(200, 30));
        jPanelInput.add(cmbNamaSupplier);

        // Jumlah Restock
        jLabel5.setText("Jumlah Restock:");
        jPanelInput.add(jLabel5);
        
        txtJmlRestock.setText("1");
        txtJmlRestock.setPreferredSize(new java.awt.Dimension(80, 30));
        txtJmlRestock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtJmlRestockKeyPressed(evt);
            }
        });
        jPanelInput.add(txtJmlRestock);

        // Tombol Tambah
        btnTambah.setText("➕ Tambah");
        btnTambah.setBackground(new java.awt.Color(40, 167, 69));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        jPanelInput.add(btnTambah);

        // =================================================================
        // TABEL PANEL
        // =================================================================
        jPanelTabel.setBackground(new java.awt.Color(255, 255, 255));
        jPanelTabel.setLayout(new java.awt.BorderLayout(0, 5));

        // MODIFIKASI: Tambah kolom di model tabel
        jTableRestock.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"Kode Barang", "Nama Barang", "Nama Supplier", "Jml Restock"}
        ));
        jScrollPane1.setViewportView(jTableRestock);
        jPanelTabel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        
        // Tombol Hapus Item di bawah tabel
        btnHapusItem.setText("🗑️ Hapus Item Terpilih");
        btnHapusItem.addActionListener(this::btnHapusItemActionPerformed);
        javax.swing.JPanel panelHapus = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelHapus.add(btnHapusItem);
        jPanelTabel.add(panelHapus, java.awt.BorderLayout.SOUTH);


        // =================================================================
        // FOOTER PANEL (Simpan Transaksi)
        // =================================================================
        jPanelFooter.setBackground(new java.awt.Color(245, 245, 245));
        jPanelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 20));

        btnSimpanTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 18));
        btnSimpanTransaksi.setText("💾 SIMPAN TRANSAKSI RESTOCK");
        btnSimpanTransaksi.setBackground(new java.awt.Color(255, 193, 7)); // Kuning
        btnSimpanTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        btnSimpanTransaksi.setPreferredSize(new java.awt.Dimension(350, 50));
        btnSimpanTransaksi.addActionListener(this::btnSimpanTransaksiActionPerformed);
        jPanelFooter.add(btnSimpanTransaksi);


        // =================================================================
        // ASSEMBLY
        // =================================================================
        javax.swing.JPanel jPanelContentWrapper = new javax.swing.JPanel();
        jPanelContentWrapper.setLayout(new java.awt.BorderLayout(10, 10));
        jPanelContentWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanelContentWrapper.add(jPanelInput, java.awt.BorderLayout.NORTH);
        jPanelContentWrapper.add(jPanelTabel, java.awt.BorderLayout.CENTER);
        
        jPanelBackground.add(jPanelHeader, java.awt.BorderLayout.NORTH);
        jPanelBackground.add(jPanelContentWrapper, java.awt.BorderLayout.CENTER);
        jPanelBackground.add(jPanelFooter, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelBackground, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // --- MAIN METHOD UNTUK TESTING ---
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TransaksiRestock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Untuk testing, panggil constructor tanpa parameter
                new TransaksiRestock().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpanTransaksi;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbNamaSupplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelSupplier;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JPanel jPanelFooter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelInput;
    private javax.swing.JPanel jPanelTabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableRestock;
    private javax.swing.JTextField txtIDRestock;
    private javax.swing.JTextField txtJmlRestock;
    private javax.swing.JTextField txtKodeBarang;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}