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
import javax.swing.JFrame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; 

/**
 * Form untuk mengelola Data Barang.
 * @author Acer
 */
public class DataBarang extends javax.swing.JFrame {

    private JFrame parentFrame;
    private DefaultTableModel model;
    private String idBarangTerpilih = null; // Untuk mode Edit/Hapus
    
    // --- CONSTRUCTORS ---
    
    public DataBarang() {
        this(null);
    }
    
    /**
     * Constructor utama untuk inisialisasi form.
     * @param parent Frame DashboardAdmin yang memanggil.
     */
    public DataBarang(JFrame parent) {
        this.parentFrame = parent;
        initComponents();
        this.setLocationRelativeTo(null); 
        
        setupTable();
        load_table();
        kosong();
        setupEscapeKey();
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

    private void setupTable() {
        model = new DefaultTableModel();
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Stok");
        model.addColumn("Harga Beli");
        model.addColumn("Harga Jual");
        jTableDataBarang.setModel(model);
    }
    
    // --- LOGIKA UTAMA ---

    /**
     * PENTING: Dideklarasikan public agar bisa dipanggil dari TransaksiRestock.
     */
    public void load_table() {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM barang ORDER BY kode_barang ASC";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("stok"),
                    rs.getInt("harga_beli"),
                    rs.getInt("harga_jual")
                });
            }
            
            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void kosong() {
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        txtStok.setText("");
        txtHargaBeli.setText("");
        txtHargaJual.setText("");
        txtKodeBarang.setEditable(true); // Kode barang bisa diisi saat mode tambah
        idBarangTerpilih = null;
        
        btnSimpan.setText("SIMPAN");
        btnSimpan.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }
    
    private void cari_data() {
        model.setRowCount(0);
        String keyword = txtPencarian.getText().trim();
        if (keyword.isEmpty()) {
            load_table();
            return;
        }

        try {
            String sql = "SELECT * FROM barang WHERE kode_barang ILIKE ? OR nama_barang ILIKE ?";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("stok"),
                    rs.getInt("harga_beli"),
                    rs.getInt("harga_jual")
                });
            }
            
            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error pencarian: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- LOGIKA CRUD ---
    
    private void simpanData() {
        String kode = txtKodeBarang.getText();
        String nama = txtNamaBarang.getText();
        String stokStr = txtStok.getText();
        String beliStr = txtHargaBeli.getText();
        String jualStr = txtHargaJual.getText();

        if (kode.isEmpty() || nama.isEmpty() || stokStr.isEmpty() || beliStr.isEmpty() || jualStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int stok = Integer.parseInt(stokStr);
            int beli = Integer.parseInt(beliStr);
            int jual = Integer.parseInt(jualStr);
            
            String sql = "INSERT INTO barang (kode_barang, nama_barang, stok, harga_beli, harga_jual) VALUES (?, ?, ?, ?, ?)";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kode);
            ps.setString(2, nama);
            ps.setInt(3, stok);
            ps.setInt(4, beli);
            ps.setInt(5, jual);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            load_table();
            kosong();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok/Harga harus berupa angka.", "Error Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Gagal menyimpan data.\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editData() {
        if (idBarangTerpilih == null) return;
        
        String kode = txtKodeBarang.getText();
        String nama = txtNamaBarang.getText();
        String stokStr = txtStok.getText();
        String beliStr = txtHargaBeli.getText();
        String jualStr = txtHargaJual.getText();

        if (kode.isEmpty() || nama.isEmpty() || stokStr.isEmpty() || beliStr.isEmpty() || jualStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int stok = Integer.parseInt(stokStr);
            int beli = Integer.parseInt(beliStr);
            int jual = Integer.parseInt(jualStr);
            
            String sql = "UPDATE barang SET nama_barang = ?, stok = ?, harga_beli = ?, harga_jual = ? WHERE kode_barang = ?";
            Connection conn = KoneksiDB.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setInt(2, stok);
            ps.setInt(3, beli);
            ps.setInt(4, jual);
            ps.setString(5, kode); // Menggunakan kode yang sama sebagai kunci update
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
            load_table();
            kosong();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok/Harga harus berupa angka.", "Error Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Gagal mengedit data.\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusData() {
        if (idBarangTerpilih == null) return;
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus barang dengan kode: " + idBarangTerpilih + "?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
             try {
                String sql = "DELETE FROM barang WHERE kode_barang = ?";
                Connection conn = KoneksiDB.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, idBarangTerpilih);
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                load_table();
                kosong();
                
            } catch (SQLException e) {
                 JOptionPane.showMessageDialog(this, "Gagal menghapus data.\nDetail: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- LOGIKA EVENT HANDLER ---

    private void btnRestockActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Membuka form TransaksiRestock (versi multi-item)
        int selectedRow = jTableDataBarang.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih satu baris barang dari tabel yang akan di-restock.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // 1. Ambil data dari baris terpilih
    String kodeBarangDipilih = jTableDataBarang.getValueAt(selectedRow, 0).toString();
    String namaBarangDipilih = jTableDataBarang.getValueAt(selectedRow, 1).toString();
    
    int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Mulai Transaksi Restock Baru untuk barang: " + namaBarangDipilih + "?", 
            "Konfirmasi Restock", 
            JOptionPane.YES_NO_OPTION);
    
    if (konfirmasi == JOptionPane.YES_OPTION) {
        try {
            // 2. Panggil konstruktor TransaksiRestock BARU dengan membawa data
            // Mengirim DataBarang (this) sebagai parent frame
            // PARENT FRAME (this), KODE BARANG, NAMA BARANG
TransaksiRestock restockForm = new TransaksiRestock(this, kodeBarangDipilih, namaBarangDipilih);
this.setVisible(false);
restockForm.setVisible(true);

        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Gagal memuat form Restock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}                                 

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

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {                                      
        cari_data();
    }                                     

    private void txtPencarianKeyPressed(java.awt.event.KeyEvent evt) {                                      
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cari_data();
        }
    }                                     
    
    private void jTableDataBarangMouseClicked(java.awt.event.MouseEvent evt) {                                            
        int selectedRow = jTableDataBarang.getSelectedRow();
        if (selectedRow == -1) return;

        idBarangTerpilih = jTableDataBarang.getValueAt(selectedRow, 0).toString();
        txtKodeBarang.setText(idBarangTerpilih);
        txtNamaBarang.setText(jTableDataBarang.getValueAt(selectedRow, 1).toString());
        txtStok.setText(jTableDataBarang.getValueAt(selectedRow, 2).toString());
        txtHargaBeli.setText(jTableDataBarang.getValueAt(selectedRow, 3).toString());
        txtHargaJual.setText(jTableDataBarang.getValueAt(selectedRow, 4).toString());
        
        txtKodeBarang.setEditable(false); // Kode barang tidak bisa diedit
        btnSimpan.setEnabled(false); // Nonaktifkan tombol simpan
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
    }                                           

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (this.parentFrame != null) {
            this.parentFrame.setVisible(true);
        }
        this.dispose(); 
    }                                        


    // --- NETBEANS GENERATED CODE (Design) ---
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        btnKembali = new javax.swing.JButton();
        jPanelInput = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtKodeBarang = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtHargaBeli = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtHargaJual = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        
        jPanelTabel = new javax.swing.JPanel();
        txtPencarian = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnRestock = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataBarang = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manajemen Data Barang");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setLayout(new java.awt.BorderLayout());

        // --- HEADER ---
        jPanelHeader.setBackground(new java.awt.Color(58, 151, 151));
        jPanelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        java.awt.GridBagLayout jPanelHeaderLayout = new java.awt.GridBagLayout();
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 10, 5, 10);
        
        jPanelHeader.setLayout(jPanelHeaderLayout);

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 32));
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setText("MANAJEMEN DATA BARANG");
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = java.awt.GridBagConstraints.CENTER;
        jPanelHeader.add(jLabelTitle, gbc);

        btnKembali.setText("Kembali (ESC)");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = java.awt.GridBagConstraints.WEST;
        jPanelHeader.add(btnKembali, gbc);

        jPanelMain.add(jPanelHeader, java.awt.BorderLayout.NORTH);

        // --- CONTENT WRAPPER (CENTER) ---
        javax.swing.JPanel jPanelContentWrapper = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        jPanelContentWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- INPUT PANEL (LEFT) ---
        jPanelInput.setBackground(new java.awt.Color(245, 245, 245));
        jPanelInput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Barang", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14)));
        jPanelInput.setPreferredSize(new java.awt.Dimension(350, 450));
        
        java.awt.GridBagLayout jPanelInputLayout = new java.awt.GridBagLayout();
        java.awt.GridBagConstraints gbcInput = new java.awt.GridBagConstraints();
        gbcInput.insets = new java.awt.Insets(10, 10, 10, 10);
        gbcInput.anchor = java.awt.GridBagConstraints.WEST;
        gbcInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelInput.setLayout(jPanelInputLayout);

        // Kode Barang
        jLabel1.setText("Kode Barang:");
        gbcInput.gridx = 0; gbcInput.gridy = 0; gbcInput.weightx = 0;
        jPanelInput.add(jLabel1, gbcInput);
        txtKodeBarang.setPreferredSize(new java.awt.Dimension(200, 30));
        gbcInput.gridx = 1; gbcInput.gridy = 0; gbcInput.weightx = 1.0;
        jPanelInput.add(txtKodeBarang, gbcInput);

        // Nama Barang
        jLabel2.setText("Nama Barang:");
        gbcInput.gridx = 0; gbcInput.gridy = 1; gbcInput.weightx = 0;
        jPanelInput.add(jLabel2, gbcInput);
        txtNamaBarang.setPreferredSize(new java.awt.Dimension(200, 30));
        gbcInput.gridx = 1; gbcInput.gridy = 1; gbcInput.weightx = 1.0;
        jPanelInput.add(txtNamaBarang, gbcInput);

        // Stok
        jLabel3.setText("Stok Awal:");
        gbcInput.gridx = 0; gbcInput.gridy = 2; gbcInput.weightx = 0;
        jPanelInput.add(jLabel3, gbcInput);
        txtStok.setPreferredSize(new java.awt.Dimension(200, 30));
        gbcInput.gridx = 1; gbcInput.gridy = 2; gbcInput.weightx = 1.0;
        jPanelInput.add(txtStok, gbcInput);

        // Harga Beli
        jLabel4.setText("Harga Beli (Modal):");
        gbcInput.gridx = 0; gbcInput.gridy = 3; gbcInput.weightx = 0;
        jPanelInput.add(jLabel4, gbcInput);
        txtHargaBeli.setPreferredSize(new java.awt.Dimension(200, 30));
        gbcInput.gridx = 1; gbcInput.gridy = 3; gbcInput.weightx = 1.0;
        jPanelInput.add(txtHargaBeli, gbcInput);

        // Harga Jual
        jLabel5.setText("Harga Jual:");
        gbcInput.gridx = 0; gbcInput.gridy = 4; gbcInput.weightx = 0;
        jPanelInput.add(jLabel5, gbcInput);
        txtHargaJual.setPreferredSize(new java.awt.Dimension(200, 30));
        gbcInput.gridx = 1; gbcInput.gridy = 4; gbcInput.weightx = 1.0;
        jPanelInput.add(txtHargaJual, gbcInput);

        // Buttons
        gbcInput.fill = java.awt.GridBagConstraints.NONE;
        gbcInput.anchor = java.awt.GridBagConstraints.CENTER;
        gbcInput.gridwidth = 2;
        gbcInput.insets = new java.awt.Insets(20, 10, 5, 10);
        
        javax.swing.JPanel jPanelButtons = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        jPanelButtons.setBackground(new java.awt.Color(245, 245, 245));
        
        btnSimpan.setText("SIMPAN");
        btnSimpan.setBackground(new java.awt.Color(40, 167, 69)); // Hijau
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);
        jPanelButtons.add(btnSimpan);
        
        btnEdit.setText("EDIT");
        btnEdit.setBackground(new java.awt.Color(255, 193, 7)); // Kuning
        btnEdit.setForeground(new java.awt.Color(0, 0, 0));
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        jPanelButtons.add(btnEdit);
        
        btnHapus.setText("HAPUS");
        btnHapus.setBackground(new java.awt.Color(220, 53, 69)); // Merah
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        jPanelButtons.add(btnHapus);
        
        btnBatal.setText("BATAL");
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        jPanelButtons.add(btnBatal);

        gbcInput.gridx = 0; gbcInput.gridy = 5; 
        jPanelInput.add(jPanelButtons, gbcInput);

        jPanelContentWrapper.add(jPanelInput, java.awt.BorderLayout.WEST);

        // --- TABEL PANEL (RIGHT/CENTER) ---
        jPanelTabel.setBackground(new java.awt.Color(255, 255, 255));
        jPanelTabel.setLayout(new java.awt.BorderLayout(0, 10));
        jPanelTabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Search and Restock Panel
        javax.swing.JPanel jPanelSearch = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        jPanelSearch.setBackground(new java.awt.Color(255, 255, 255));
        
        txtPencarian.setPreferredSize(new java.awt.Dimension(250, 30));
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPencarianKeyPressed(evt);
            }
        });
        jPanelSearch.add(txtPencarian);

        btnCari.setText("Cari");
        btnCari.addActionListener(this::btnCariActionPerformed);
        jPanelSearch.add(btnCari);
        
        btnRestock.setText("🛒 Restock Barang");
        btnRestock.setBackground(new java.awt.Color(0, 123, 255)); // Biru
        btnRestock.setForeground(new java.awt.Color(255, 255, 255));
        btnRestock.addActionListener(this::btnRestockActionPerformed);
        jPanelSearch.add(btnRestock);
        
        jPanelTabel.add(jPanelSearch, java.awt.BorderLayout.NORTH);
        
        // JTable
        jTableDataBarang.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"Kode Barang", "Nama Barang", "Stok", "Harga Beli", "Harga Jual"}
        ));
        jTableDataBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDataBarangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableDataBarang);
        jPanelTabel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanelContentWrapper.add(jPanelTabel, java.awt.BorderLayout.CENTER);
        
        jPanelMain.add(jPanelContentWrapper, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

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
            java.util.logging.Logger.getLogger(DataBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataBarang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRestock;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelInput;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelTabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDataBarang;
    private javax.swing.JTextField txtHargaBeli;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtKodeBarang;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtPencarian;
    private javax.swing.JTextField txtStok;
    // End of variables declaration//GEN-END:variables
}