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
import java.util.Date; 
import java.text.SimpleDateFormat; 
import java.text.DecimalFormat; 
import java.text.DecimalFormatSymbols; 
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; // ASUMSI: Class koneksi Anda
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Form untuk menangani Transaksi Restock (Multi-Item Restock).
 * FIX: Implementasi penuh fungsi Tambah, Hapus, Simpan Transaksi (DB: pembelian, detail_pembelian, update stock barang).
 * FIX: Perbaikan Singleton agar data tabel tidak hilang saat form dibuka kembali (ESC/Kembali).
 * @author Acer
 */
public class TransaksiRestock extends javax.swing.JFrame {

    // --- VARIABEL GLOBAL ---
    private static TransaksiRestock instance = null; // Variabel Singleton
    private JFrame parentFrame; 
    private DefaultTableModel modelTabelRestock;
    private final DecimalFormat formatter;
    // -----------------------

    // =======================================================
    // CONSTRUCTORS DAN SINGLETON METHOD
    // =======================================================

    /**
     * Constructor Private untuk Singleton.
     */
    private TransaksiRestock(JFrame parent, String kodeBarang, String namaBarang) {
        this.parentFrame = parent;
        
        // Inisialisasi DecimalFormat
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        this.formatter = new DecimalFormat("#,##0", symbols);
        
        initComponents(); 

        initCustomComponents(); 
        
        // Set data awal dari DataBarang
        setInitialItem(kodeBarang, namaBarang);
    }
    
    /**
     * Constructor default (tanpa parameter).
     */
    public TransaksiRestock() {
        this(null, null, null); 
    }

    /**
     * Static factory method (Singleton pattern) untuk TransaksiRestock.
     */
    public static TransaksiRestock getInstance(DataBarang parent, String kodeBarang, String namaBarang) {
        if (instance == null) {
            instance = new TransaksiRestock(parent, kodeBarang, namaBarang);
        } else {
            instance.parentFrame = parent;
            // >>>>>> PERBAIKAN UTAMA: JANGAN KOSONGKAN SELURUH FORM/TABEL <<<<<<
            instance.clearItemInput(); 
            instance.generateIDRestock(); 
            instance.setInitialItem(kodeBarang, namaBarang); 
            // Tabel dan total harga beli tetap utuh (tidak direset)
        }
        return instance;
    }
    
    // =======================================================
    // CUSTOM UTILITY METHODS
    // =======================================================

    /**
     * Mengubah nilai numerik menjadi string dengan format Rupiah (tanpa simbol Rp)
     * Contoh: 10000 -> 10.000
     */
    private String formatRupiah(double value) {
        return formatter.format(value);
    }

    /**
     * Mengubah string format Rupiah (misal: 10.000) menjadi nilai double.
     */
    private double parseRupiah(String value) {
        try {
            // Hilangkan semua karakter selain digit
            String cleanString = value.replaceAll("[^\\d]", "");
            return Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    // =======================================================
    // CUSTOM DATA & UI METHODS
    // =======================================================

    private void initCustomComponents() {
        // Logika untuk mengatur tabel
        modelTabelRestock = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Kode Barang", "Nama Barang", "Supplier", "Jml Restock", "Harga Beli", "Subtotal"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableRestock.setModel(modelTabelRestock);
        
        generateIDRestock(); 
        setTanggalSekarang();
        loadSupplier(); // Panggil untuk mengisi ComboBox supplier
        setupAkselerator();
    }
    
    /**
     * Mengambil data Harga Beli dari tabel 'barang' berdasarkan Kode Barang
     * dan mengisi field txtHargaBeli.
     */
    private void loadDataBarang(String kodeBarang) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        String hargaBeli = "0"; 

        try {
            conn = KoneksiDB.getKoneksi(); 
            
            // Query untuk mengambil harga_beli
            String sql = "SELECT harga_beli FROM barang WHERE kode_barang = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, kodeBarang);
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                int harga = rs.getInt("harga_beli");
                hargaBeli = String.valueOf(harga); 
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil Harga Beli: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pst != null) pst.close(); } catch (SQLException e) { /* ignore */ }
        }
        
        txtHargaBeli.setText(hargaBeli);
    }
    
    /**
     * Mengisi field input dengan data barang yang dipilih dari DataBarang.
     */
    private void setInitialItem(String kodeBarang, String namaBarang) {
        if (kodeBarang != null && namaBarang != null) {
            txtKodeBarang.setText(kodeBarang);
            txtNamaBarang.setText(namaBarang);
            txtJmlRestock.setText("1"); 
            
            loadDataBarang(kodeBarang); 
            
            txtKodeBarang.setEnabled(false);
            txtNamaBarang.setEnabled(false);
            txtJmlRestock.requestFocus();
        } else {
            txtKodeBarang.setEnabled(true);
            txtNamaBarang.setEnabled(true);
            // Jika dipanggil dari menu utama dan tabel sudah berisi, 
            // jangan hapus ID restock yang sudah terisi.
            if (modelTabelRestock.getRowCount() == 0) {
                 generateIDRestock(); 
            }
            setTanggalSekarang();
        }
    }
    
    /**
     * Mengambil semua nama supplier dari tabel 'supplier' dan mengisikannya ke cmbNamaSupplier.
     */
    private void loadSupplier() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        // Bersihkan item yang sudah ada
        cmbNamaSupplier.removeAllItems();
        cmbNamaSupplier.addItem("-- Pilih Supplier --"); 

        try {
            conn = KoneksiDB.getKoneksi(); 
            
            // Query untuk mengambil semua nama supplier
            String sql = "SELECT nama_supplier FROM supplier ORDER BY nama_supplier ASC";
            pst = conn.prepareStatement(sql);
            
            rs = pst.executeQuery();
            
            while (rs.next()) {
                // Tambahkan nama supplier ke ComboBox
                cmbNamaSupplier.addItem(rs.getString("nama_supplier"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data Supplier: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pst != null) pst.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
    
    private void generateIDRestock() {
        txtIDRestock.setText("RCK" + new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
    }
    
    private void setTanggalSekarang() {
        txtTanggal.setText(new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
    }
    
    /**
     * Mengosongkan field input untuk satu item yang akan direstock.
     */
    private void clearItemInput() {
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        txtJmlRestock.setText("0");
        txtHargaBeli.setText("0");
        cmbNamaSupplier.setSelectedIndex(0); 
        
        // Aktifkan kembali input Kode Barang
        txtKodeBarang.setEnabled(true);
        txtNamaBarang.setEnabled(true); 
        txtKodeBarang.requestFocus();
    }
    
    /**
     * Mengosongkan SEMUA input dan tabel. HANYA dipanggil setelah transaksi berhasil disimpan.
     */
    public void kosongkanForm() {
        clearItemInput();
        modelTabelRestock.setRowCount(0);
        hitungTotalHargaBeli();
        generateIDRestock();
    }
    
    private void hitungTotalHargaBeli() {
        double total = 0;
        for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
            String subtotalStr = (String) modelTabelRestock.getValueAt(i, 5);
            try {
                // Hapus pemisah ribuan sebelum parsing
                total += parseRupiah(subtotalStr);
            } catch (Exception e) {
                // Abaikan baris yang formatnya salah
            }
        }
        jLabelTotalHargaBeli.setText("Total Harga Beli: Rp. " + formatRupiah(total));
    }
    
    private void setupAkselerator() {
        InputMap inputMap = this.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        // Shortcut F5 untuk Tambah Item
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "TambahItem");
        actionMap.put("TambahItem", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnTambahActionPerformed(e);
            }
        });
        
        // Shortcut F10 untuk Simpan Transaksi
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "SimpanTransaksi");
        actionMap.put("SimpanTransaksi", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnSimpanTransaksiActionPerformed(e);
            }
        });
        
        // Shortcut ESCAPE untuk Kembali
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Kembali");
        actionMap.put("Kembali", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnKembaliActionPerformed(e);
            }
        });
    }

    // =======================================================
    // EVENT HANDLERS (ACTION PERFORMED)
    // =======================================================

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // 1. Ambil data dari input field
        String kodeBarang = txtKodeBarang.getText().trim();
        String namaBarang = txtNamaBarang.getText().trim();
        String supplier = (String) cmbNamaSupplier.getSelectedItem();
        String jmlRestockStr = txtJmlRestock.getText().trim();
        String hargaBeliStr = txtHargaBeli.getText().trim();

        // 2. Validasi Input
        if (kodeBarang.isEmpty() || namaBarang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Nama Barang harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtKodeBarang.requestFocus();
            return;
        }
        if (supplier == null || supplier.equals("-- Pilih Supplier --")) {
            JOptionPane.showMessageDialog(this, "Supplier harus dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            cmbNamaSupplier.requestFocus();
            return;
        }

        int jmlRestock;
        double hargaBeli;

        try {
            hargaBeli = parseRupiah(hargaBeliStr); 
            jmlRestock = Integer.parseInt(jmlRestockStr);
            if (jmlRestock <= 0 || hargaBeli <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah restock dan harga beli harus lebih dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah restock dan Harga Beli harus berupa angka valid.", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 3. Hitung Subtotal
        double subtotal = jmlRestock * hargaBeli;
        
        // 4. Cek duplikasi item di tabel
        for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
            String kodeDiTabel = (String) modelTabelRestock.getValueAt(i, 0);
            String supplierDiTabel = (String) modelTabelRestock.getValueAt(i, 2);
            
            // Cek jika barang dan supplier yang sama sudah ada
            if (kodeDiTabel.equals(kodeBarang) && supplierDiTabel.equals(supplier)) {
                int konf = JOptionPane.showConfirmDialog(this, 
                        "Item ini dengan supplier yang sama sudah ada di daftar. Apakah Anda ingin menambahkan jumlah restock?", 
                        "Duplikasi Item", JOptionPane.YES_NO_OPTION);
                
                if (konf == JOptionPane.YES_OPTION) {
                    // Ambil nilai lama
                    int jmlLama = Integer.parseInt((String) modelTabelRestock.getValueAt(i, 3));
                    double subtotalLama = parseRupiah((String) modelTabelRestock.getValueAt(i, 5));
                    
                    // Hitung nilai baru
                    int jmlBaru = jmlLama + jmlRestock;
                    double subtotalBaru = subtotalLama + subtotal;
                    
                    // Update baris di tabel
                    modelTabelRestock.setValueAt(String.valueOf(jmlBaru), i, 3);
                    modelTabelRestock.setValueAt(formatRupiah(subtotalBaru), i, 5);
                    
                    // Update total dan bersihkan input
                    hitungTotalHargaBeli();
                    clearItemInput();
                    return;
                } else {
                    // Jika memilih NO, anggap transaksi baru diabaikan dan keluar dari method.
                    return;
                }
            }
        }
        
        // 5. Tambahkan baris baru 
        modelTabelRestock.addRow(new Object[]{
            kodeBarang, 
            namaBarang, 
            supplier, 
            String.valueOf(jmlRestock),
            formatRupiah(hargaBeli), // Tampilkan harga beli dengan format
            formatRupiah(subtotal)  // Tampilkan subtotal dengan format
        });

        // 6. Update Total dan Reset Input
        hitungTotalHargaBeli();
        clearItemInput();
    }                                       

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {                                         
        int selectedRow = jTableRestock.getSelectedRow();
        if (selectedRow >= 0) {
            int konf = JOptionPane.showConfirmDialog(this, 
                    "Yakin ingin menghapus item ini dari daftar?", 
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
            if (konf == JOptionPane.YES_OPTION) {
                modelTabelRestock.removeRow(selectedRow);
                hitungTotalHargaBeli();
                JOptionPane.showMessageDialog(this, "Item berhasil dihapus dari daftar restock.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }                                        

    private void btnSimpanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {                                                 
    if (modelTabelRestock.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Daftar restock masih kosong. Tambahkan item terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menyimpan transaksi Restock ini?", 
            "Konfirmasi Simpan", 
            JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.NO_OPTION) {
        return;
    }

    Connection conn = null;
    PreparedStatement pstPembelian = null;
    PreparedStatement pstDetail = null;
    PreparedStatement pstUpdateStock = null;
    String noPembelian = txtIDRestock.getText(); // Mengganti idRestock menjadi noPembelian

// >>> KODE PERBAIKAN: Gunakan java.sql.Timestamp <<<
java.util.Date utilDate = new java.util.Date();
java.sql.Timestamp tanggal = new java.sql.Timestamp(utilDate.getTime());

    // >>> PERBAIKAN 1: HITUNG ULANG TOTAL HARGA BELI DARI TABEL (Mengatasi Error NULL) <<<
    double totalHargaBeli = 0.0;
    for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
        // Ambil subtotal dari kolom ke-5 (indeks 5) yang berisi String terformat
        String subtotalStr = (String) modelTabelRestock.getValueAt(i, 5);
        // Pastikan method parseRupiah() Anda dapat memproses format "10.000" menjadi 10000.0
        totalHargaBeli += parseRupiah(subtotalStr);
    }
    
    // Validasi tambahan: Jika total nol, batalkan transaksi
    if (totalHargaBeli <= 0.0) {
        JOptionPane.showMessageDialog(this, "Total Harga Beli tidak valid (Rp. 0). Pastikan data item sudah benar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    // =================================================================================

    // ASUMSI: ID User yang melakukan transaksi. Ganti '1' dengan ID User yang sebenarnya.
    int idUser = 1; 

    try {
        conn = KoneksiDB.getKoneksi();
        conn.setAutoCommit(false); // Mulai transaksi

        // 1. SIMPAN KE TABEL PEMBELIAN
        // >>> PERBAIKAN 2: Ganti id_pembelian menjadi no_pembelian di Query SQL <<<
        String sqlPembelian = "INSERT INTO pembelian (no_pembelian, tanggal, total_harga, pengguna_id) VALUES (?, ?, ?, ?)";
        pstPembelian = conn.prepareStatement(sqlPembelian);
        pstPembelian.setString(1, noPembelian); // Menggunakan noPembelian
        pstPembelian.setTimestamp(2, tanggal);
        pstPembelian.setDouble(3, totalHargaBeli); // Menggunakan nilai yang sudah dihitung ulang
        pstPembelian.setInt(4, idUser); 
        pstPembelian.executeUpdate();

        // 2. LOOP DAN SIMPAN KE DETAIL_PEMBELIAN & UPDATE STOCK BARANG
        // >>> PERBAIKAN 3: Ganti id_pembelian menjadi no_pembelian di Query SQL <<<
        String sqlDetail = "INSERT INTO detail_pembelian (no_pembelian, kode_barang, kode_supplier, jumlah_barang, harga, total_harga) VALUES (?, ?, ?, ?, ?, ?)";
        // ASUMSI: Tabel barang memiliki kolom kode_barang dan stock
        String sqlUpdateStock = "UPDATE barang SET stock = stock + ? WHERE kode_barang = ?";
        
        pstDetail = conn.prepareStatement(sqlDetail);
        pstUpdateStock = conn.prepareStatement(sqlUpdateStock);
        
        for (int i = 0; i < modelTabelRestock.getRowCount(); i++) {
            String kodeBarang = (String) modelTabelRestock.getValueAt(i, 0);
            String supplier = (String) modelTabelRestock.getValueAt(i, 2);
            int jmlRestock = Integer.parseInt((String) modelTabelRestock.getValueAt(i, 3));
            
            // Ambil harga beli dan subtotal dengan parseRupiah dari String terformat di tabel
            double hargaBeli = parseRupiah((String) modelTabelRestock.getValueAt(i, 4));
            double subtotal = parseRupiah((String) modelTabelRestock.getValueAt(i, 5));

            // Simpan ke detail_pembelian
            pstDetail.setString(1, noPembelian); // Menggunakan noPembelian
            pstDetail.setString(2, kodeBarang);
            pstDetail.setString(3, supplier);
            pstDetail.setInt(4, jmlRestock);
            pstDetail.setInt(5, hargaBeli);
            pstDetail.setInt(6, subtotal);
            pstDetail.executeUpdate();

            // Update Stock di tabel barang
            pstUpdateStock.setInt(1, jmlRestock);
            pstUpdateStock.setString(2, kodeBarang);
            pstUpdateStock.executeUpdate();
        }

        conn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Transaksi Restock berhasil disimpan. Stok barang telah diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset Form TOTAL (termasuk tabel) setelah simpan
        kosongkanForm();

    } catch (SQLException e) {
        try {
            if (conn != null) conn.rollback(); // Rollback jika ada error
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi ne: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } finally {
        // --- BLOK INI ADALAH PERBAIKANNYA ---
        try { if (pstPembelian != null) pstPembelian.close(); } catch (SQLException e) { /* ignore */ }
        try { if (pstDetail != null) pstDetail.close(); } catch (SQLException e) { /* ignore */ }
        try { if (pstUpdateStock != null) pstUpdateStock.close(); } catch (SQLException e) { /* ignore */ }
        
        // PASTIKAN conn.close() HANYA DIPANGGIL JIKA conn TIDAK NULL
        try { 
            if (conn != null) { // PENTING: Gunakan kurung kurawal {}
                conn.setAutoCommit(true); 
                conn.close(); 
            } 
        } catch (SQLException e) { 
            /* ignore */ 
        }
    }
}

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (parentFrame != null) {
            parentFrame.setVisible(true);
            this.dispose();
            // instance TIDAK di-reset agar data tabel tetap tersimpan di memori instance.
        } else {
            this.dispose();
        }
    }                                        

    // =======================================================
    // NETBEANS GENERATED CODE (initComponents, main, variables)
    // =======================================================

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanelBackground = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelInput = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtIDRestock = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtKodeBarang = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabelSupplier = new javax.swing.JLabel();
        cmbNamaSupplier = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtJmlRestock = new javax.swing.JTextField();
        jLabelHargaBeli = new javax.swing.JLabel();
        txtHargaBeli = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        jPanelTabel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRestock = new javax.swing.JTable();
        btnHapusItem = new javax.swing.JButton();
        jPanelFooter = new javax.swing.JPanel();
        jLabelTotalHargaBeli = new javax.swing.JLabel();
        btnSimpanTransaksi = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Transaksi Restock");

        jPanelBackground.setBackground(new java.awt.Color(240, 240, 240));

        jPanelHeader.setBackground(new java.awt.Color(0, 102, 102));

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("TRANSAKSI RESTOCK BARANG");

        javax.swing.GroupLayout jPanelHeaderLayout = new javax.swing.GroupLayout(jPanelHeader);
        jPanelHeader.setLayout(jPanelHeaderLayout);
        jPanelHeaderLayout.setHorizontalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeaderLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabelTitle)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanelInput.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Item Restock"));

        jLabel1.setText("ID Restock:");

        txtIDRestock.setEditable(false);

        jLabel2.setText("Tanggal:");

        txtTanggal.setEditable(false);

        jLabel3.setText("Kode Barang:");

        jLabel4.setText("Nama Barang:");

        jLabelSupplier.setText("Supplier:");

        cmbNamaSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Supplier --" }));

        jLabel5.setText("Jumlah Restock:");

        jLabelHargaBeli.setText("Harga Beli (per unit):");

        btnTambah.setText("Tambah (F5)");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelInputLayout = new javax.swing.GroupLayout(jPanelInput);
        jPanelInput.setLayout(jPanelInputLayout);
        jPanelInputLayout.setHorizontalGroup(
            jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInputLayout.createSequentialGroup()
                        .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIDRestock, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(txtKodeBarang))
                        .addGap(30, 30, 30)
                        .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelInputLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelInputLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTanggal)))
                        .addGap(30, 30, 30)
                        .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelSupplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbNamaSupplier, 0, 150, Short.MAX_VALUE)
                            .addComponent(txtJmlRestock)))
                    .addGroup(jPanelInputLayout.createSequentialGroup()
                        .addComponent(jLabelHargaBeli)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHargaBeli, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelInputLayout.setVerticalGroup(
            jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIDRestock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSupplier)
                    .addComponent(cmbNamaSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtJmlRestock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelHargaBeli)
                    .addComponent(txtHargaBeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelTabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Restock"));

        jTableRestock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Nama Barang", "Supplier", "Jml Restock", "Harga Beli", "Subtotal"
            }
        ));
        jScrollPane1.setViewportView(jTableRestock);

        btnHapusItem.setText("Hapus Item");
        btnHapusItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTabelLayout = new javax.swing.GroupLayout(jPanelTabel);
        jPanelTabel.setLayout(jPanelTabelLayout);
        jPanelTabelLayout.setHorizontalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 792, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnHapusItem)))
                .addContainerGap())
        );
        jPanelTabelLayout.setVerticalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHapusItem)
                .addContainerGap())
        );

        jPanelFooter.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTotalHargaBeli.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTotalHargaBeli.setText("Total Harga Beli: Rp. 0");

        btnSimpanTransaksi.setText("Simpan Transaksi (F10)");
        btnSimpanTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanTransaksiActionPerformed(evt);
            }
        });

        btnKembali.setText("Kembali (ESC)");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFooterLayout = new javax.swing.GroupLayout(jPanelFooter);
        jPanelFooter.setLayout(jPanelFooterLayout);
        jPanelFooterLayout.setHorizontalGroup(
            jPanelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTotalHargaBeli, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSimpanTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelFooterLayout.setVerticalGroup(
            jPanelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTotalHargaBeli)
                    .addComponent(btnSimpanTransaksi)
                    .addComponent(btnKembali))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelBackgroundLayout = new javax.swing.GroupLayout(jPanelBackground);
        jPanelBackground.setLayout(jPanelBackgroundLayout);
        jPanelBackgroundLayout.setHorizontalGroup(
            jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelTabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelBackgroundLayout.setVerticalGroup(
            jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBackgroundLayout.createSequentialGroup()
                .addComponent(jPanelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TransaksiRestock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransaksiRestock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransaksiRestock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransaksiRestock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Untuk testing, panggil constructor tanpa parameter
                new TransaksiRestock().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
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
    private javax.swing.JLabel jLabelHargaBeli;
    private javax.swing.JLabel jLabelSupplier;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelTotalHargaBeli;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JPanel jPanelFooter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelInput;
    private javax.swing.JPanel jPanelTabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableRestock;
    private javax.swing.JTextField txtHargaBeli;
    private javax.swing.JTextField txtIDRestock;
    private javax.swing.JTextField txtJmlRestock;
    private javax.swing.JTextField txtKodeBarang;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration                   
}