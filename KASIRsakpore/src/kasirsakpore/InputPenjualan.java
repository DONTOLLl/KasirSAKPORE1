package kasirsakpore;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

// Import untuk JDBC
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// IMPORT KONEKSI DB DARI PACKAGE DAN NAMA FILE YANG BENAR
import koneksi.KoneksiDB; 

/**
 *
 * @author KasirSakpore
 * Form Transaksi Kasir dengan integrasi database.
 */
public class InputPenjualan extends JFrame {

    // --- Variabel State & Utility ---
    private double hargaSatuanItemSaatIni = 0.0;
    
    private final DecimalFormat formatter = new DecimalFormat("Rp #,##0.00", new DecimalFormatSymbols(new Locale("id", "ID")));
    
    private final int currentUserId; 
    
    // --- Komponen GUI ---
    private JPanel headerPanel;
    private JPanel inputBarangPanel;
    private JPanel pembayaranPanel;
    private JPanel mainPanel;
    private JLabel lblTagihanBesar;
    private JLabel lblNoNota;
    private JTextField txtNoNota;
    private JLabel lblTanggal;
    private JTextField txtTanggal; 
    private JLabel lblKodeBarang;
    private JTextField txtKodeBarang;
    private JButton btnCariBarang;
    private JLabel lblNamaBarang;
    private JTextField txtNamaBarang;
    private JLabel lblStok;
    private JTextField txtStok;
    private JLabel lblHargaSatuan;
    private JTextField txtHargaSatuan;
    private JLabel lblJumlahJual;
    private JTextField txtJumlahJual;
    private JLabel lblHargaAkhirItem;
    private JTextField txtHargaAkhirItem;
    private JLabel lblKasir;
    private JButton btnSimpanItem;
    private JButton btnBatalItem;
    private JButton btnHapusSemuaItem;
    private JTable barangTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JLabel lblSubTotal;
    private JTextField txtSubTotal;
    private JLabel lblDiskonPersen;
    private JTextField txtDiskonPersen;
    private JTextField txtDiskonRupiah;
    private JLabel lblTotalHarga;
    private JTextField txtTotalHarga;
    private JLabel lblBayar;
    private JTextField txtBayar;
    private JLabel lblKembalian;
    private JTextField txtKembalian;
    private JButton btnSimpanTransaksi;
    private JButton btnBatalTransaksi;
    private JButton btnLihatPenjualan;
    // --- TAMBAHAN ---
    private JButton btnLogout; // Tombol Logout baru
    // --- END TAMBAHAN ---
    private JFrame parentDashboard; 

    public InputPenjualan(JFrame parent, int userId) {
        this.parentDashboard = parent;
        this.currentUserId = userId; 
        setTitle("Input Penjualan (Transaksi Kasir)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentDashboard != null) {
                    parentDashboard.setVisible(true);
                }
            }
        });

        initComponents();
        setNotaDanTanggalOtomatis(); 
        lblKasir.setText("Kasir ID: " + currentUserId); 
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }
    
    // =======================================================
    // I. INISIALISASI & GUI LAYOUT
    // =======================================================

    private void initComponents() {
        // --- Inisialisasi Panel Utama ---
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE); 
        headerPanel = new JPanel();
        inputBarangPanel = new JPanel();
        pembayaranPanel = new JPanel();
        
        // --- Header Transaksi ---
        headerPanel.setBorder(new TitledBorder("Transaksi Kasir"));
        headerPanel.setBackground(Color.WHITE);
        lblNoNota = new JLabel("No. Nota");
        txtNoNota = new JTextField();
        lblTanggal = new JLabel("Tanggal/Jam"); 
        txtTanggal = new JTextField();
        
        lblTagihanBesar = new JLabel("Tagihan : " + formatter.format(0));
        lblTagihanBesar.setForeground(Color.RED);
        lblTagihanBesar.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        
        // --- TAMBAHAN: Logout Button ---
        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 102, 102));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(this::logoutAction); // Tambahkan listener

        // Layout Header (Diubah untuk menampung tombol Logout)
        GroupLayout headerLayout = new GroupLayout(headerPanel);
        headerPanel.setLayout(headerLayout);
        headerLayout.setAutoCreateGaps(true);
        headerLayout.setAutoCreateContainerGaps(true);
        headerLayout.setHorizontalGroup(
            headerLayout.createSequentialGroup()
                .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblNoNota)
                    .addComponent(lblTanggal))
                .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtNoNota, 200, 200, 200)
                    .addComponent(txtTanggal, 200, 200, 200))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTagihanBesar)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, 80, 80, 80) // Lebar tetap untuk tombol logout
        );
        headerLayout.setVerticalGroup(
            headerLayout.createSequentialGroup()
                .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNoNota)
                    .addComponent(txtNoNota, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogout))
                .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTanggal)
                    .addComponent(txtTanggal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblTagihanBesar)
        );
        
        // --- Input Barang ---
        inputBarangPanel.setBackground(new Color(240, 240, 240)); 
        lblKodeBarang = new JLabel("Kode Barang");
        txtKodeBarang = new JTextField();
        btnCariBarang = new JButton("Cari Barang");
        lblNamaBarang = new JLabel("Nama Barang");
        txtNamaBarang = new JTextField();
        txtNamaBarang.setEditable(false);
        lblStok = new JLabel("Stok");
        txtStok = new JTextField();
        txtStok.setEditable(false);
        lblHargaSatuan = new JLabel("Harga Satuan");
        txtHargaSatuan = new JTextField();
        txtHargaSatuan.setEditable(false);
        lblJumlahJual = new JLabel("Jumlah Jual");
        txtJumlahJual = new JTextField("1"); 
        lblHargaAkhirItem = new JLabel("Harga Akhir");
        txtHargaAkhirItem = new JTextField(formatter.format(0));
        txtHargaAkhirItem.setEditable(false);
        btnSimpanItem = new JButton("Simpan Item");
        btnBatalItem = new JButton("Batal Item");
        btnHapusSemuaItem = new JButton("Hapus Semua Item");
        lblKasir = new JLabel("Kasir :");
        
        // Layout Input Barang
        GroupLayout inputBarangLayout = new GroupLayout(inputBarangPanel);
        inputBarangPanel.setLayout(inputBarangLayout);
        inputBarangLayout.setAutoCreateGaps(true);
        inputBarangLayout.setAutoCreateContainerGaps(true);
        inputBarangLayout.setHorizontalGroup(
            inputBarangLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(inputBarangLayout.createSequentialGroup()
                    .addComponent(lblKodeBarang)
                    .addComponent(txtKodeBarang, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCariBarang)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                    .addComponent(lblNamaBarang)
                    .addComponent(txtNamaBarang, 150, 150, 150)
                    .addComponent(lblStok)
                    .addComponent(txtStok, 50, 50, 50))
                .addGroup(inputBarangLayout.createSequentialGroup()
                    .addComponent(lblHargaSatuan)
                    .addComponent(txtHargaSatuan, 100, 100, 100)
                    .addComponent(lblJumlahJual)
                    .addComponent(txtJumlahJual, 50, 50, 50)
                    .addComponent(lblHargaAkhirItem)
                    .addComponent(txtHargaAkhirItem, 100, 100, 100)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpanItem)
                    .addComponent(btnBatalItem))
                .addGroup(inputBarangLayout.createSequentialGroup()
                    .addComponent(lblKasir)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHapusSemuaItem))
        );
        inputBarangLayout.setVerticalGroup(
            inputBarangLayout.createSequentialGroup()
                .addGroup(inputBarangLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKodeBarang)
                    .addComponent(txtKodeBarang, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCariBarang)
                    .addComponent(lblNamaBarang)
                    .addComponent(txtNamaBarang, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStok)
                    .addComponent(txtStok, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(inputBarangLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHargaSatuan)
                    .addComponent(txtHargaSatuan, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblJumlahJual)
                    .addComponent(txtJumlahJual, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHargaAkhirItem)
                    .addComponent(txtHargaAkhirItem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSimpanItem)
                    .addComponent(btnBatalItem))
                .addGroup(inputBarangLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKasir)
                    .addComponent(btnHapusSemuaItem))
        );

        // --- Tabel Barang ---
        String[] columnNames = {"Kode Barang", "Nama Barang", "Harga Satuan", "Jumlah Jual", "Harga Akhir", "Opsi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; 
            }
        };
        barangTable = new JTable(tableModel);
        barangTable.getColumn("Opsi").setCellRenderer(new ButtonRenderer());
        barangTable.getColumn("Opsi").setCellEditor(new ButtonEditor(new JTextField("Hapus")));
        tableScrollPane = new JScrollPane(barangTable);
        tableScrollPane.setBorder(new TitledBorder("Barang yang dijual"));
        
        // --- Pembayaran ---
        pembayaranPanel.setBackground(Color.WHITE);
        lblSubTotal = new JLabel("Sub Total");
        txtSubTotal = new JTextField(formatter.format(0));
        txtSubTotal.setEditable(false);
        lblDiskonPersen = new JLabel("Diskon");
        txtDiskonPersen = new JTextField("0");
        JLabel lblPersen = new JLabel("%");
        txtDiskonRupiah = new JTextField(formatter.format(0));
        txtDiskonRupiah.setEditable(false);
        lblTotalHarga = new JLabel("Total Harga");
        txtTotalHarga = new JTextField(formatter.format(0));
        txtTotalHarga.setEditable(false);
        lblBayar = new JLabel("Bayar");
        txtBayar = new JTextField(formatter.format(0));
        lblKembalian = new JLabel("Kembalian");
        txtKembalian = new JTextField(formatter.format(0));
        txtKembalian.setEditable(false);
        
        btnSimpanTransaksi = new JButton("Simpan");
        btnBatalTransaksi = new JButton("Batal");
        btnLihatPenjualan = new JButton("Lihat Penjualan"); 
        
        btnSimpanTransaksi.setBackground(new Color(102, 179, 102)); 
        btnSimpanTransaksi.setForeground(Color.WHITE);
        btnBatalTransaksi.setBackground(new Color(255, 102, 102)); 
        btnBatalTransaksi.setForeground(Color.WHITE);
        btnLihatPenjualan.setBackground(new Color(60, 140, 250)); 
        btnLihatPenjualan.setForeground(Color.WHITE);
        
        // Layout Pembayaran
        GroupLayout pembayaranLayout = new GroupLayout(pembayaranPanel);
        pembayaranPanel.setLayout(pembayaranLayout);
        pembayaranLayout.setAutoCreateGaps(true);
        pembayaranLayout.setAutoCreateContainerGaps(true);
        pembayaranLayout.setHorizontalGroup(
            pembayaranLayout.createSequentialGroup()
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubTotal)
                    .addGroup(pembayaranLayout.createSequentialGroup()
                        .addComponent(lblDiskonPersen)
                        .addComponent(txtDiskonPersen, 30, 30, 30)
                        .addComponent(lblPersen))
                    .addComponent(lblTotalHarga))
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtSubTotal, 150, 150, 150)
                    .addComponent(txtDiskonRupiah, 150, 150, 150)
                    .addComponent(txtTotalHarga, 150, 150, 150))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE) 
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblBayar)
                    .addComponent(lblKembalian))
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtBayar, 150, 150, 150)
                    .addComponent(txtKembalian, 150, 150, 150))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnLihatPenjualan, 100, 100, 100) 
                    .addComponent(btnSimpanTransaksi, 100, 100, 100)
                    .addComponent(btnBatalTransaksi, 100, 100, 100))
        );
        pembayaranLayout.setVerticalGroup(
            pembayaranLayout.createSequentialGroup()
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubTotal)
                    .addComponent(txtSubTotal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBayar)
                    .addComponent(txtBayar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLihatPenjualan)) 
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDiskonPersen)
                    .addComponent(txtDiskonPersen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPersen)
                    .addComponent(txtDiskonRupiah, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKembalian)
                    .addComponent(txtKembalian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSimpanTransaksi))
                .addGroup(pembayaranLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalHarga)
                    .addComponent(txtTotalHarga, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatalTransaksi))
        );
        
        // Layout Utama
        GroupLayout mainLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainLayout);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(headerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inputBarangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pembayaranPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        mainLayout.setVerticalGroup(
            mainLayout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(inputBarangPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE) 
                .addComponent(pembayaranPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        
        getContentPane().add(mainPanel);


        // --- Action Listeners ---
        btnSimpanTransaksi.addActionListener(this::simpanTransaksiAction);
        btnBatalTransaksi.addActionListener(this::batalTransaksiAction);
        btnLihatPenjualan.addActionListener(this::lihatPenjualanAction); 
        btnSimpanItem.addActionListener(this::simpanItemAction);
        btnBatalItem.addActionListener(this::batalItemAction);
        btnHapusSemuaItem.addActionListener(this::hapusSemuaItemAction);
        btnCariBarang.addActionListener(this::cariBarangAction);
        
        // Listeners untuk perhitungan otomatis
        txtJumlahJual.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hitungHargaAkhir(); }
            @Override
            public void removeUpdate(DocumentEvent e) { hitungHargaAkhir(); }
            @Override
            public void changedUpdate(DocumentEvent e) { hitungHargaAkhir(); }
        });
        
        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override
            public void removeUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override
            public void changedUpdate(DocumentEvent e) { hitungKembalian(); }
        });
        
        txtDiskonPersen.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hitungTotalTransaksi(); }
            @Override
            public void removeUpdate(DocumentEvent e) { hitungTotalTransaksi(); }
            @Override
            public void changedUpdate(DocumentEvent e) { hitungTotalTransaksi(); }
        });
    }

    private void setNotaDanTanggalOtomatis() {
        SimpleDateFormat sdfNota = new SimpleDateFormat("yyMMddHHmmss");
        String noNota = "TRX-" + sdfNota.format(new Date());
        txtNoNota.setText(noNota);
        txtNoNota.setEditable(false); 

        txtTanggal.setEditable(false);
        new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdfTanggal = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                txtTanggal.setText(sdfTanggal.format(new Date()));
            }
        }).start();
        
        lblTagihanBesar.setText("Tagihan : " + formatter.format(0));
    }
    
    // =======================================================
    // II. LOGIKA PERHITUNGAN
    // =======================================================
    
    private void hitungHargaAkhir() {
        try {
            int jumlah = Integer.parseInt(txtJumlahJual.getText().trim());
            
            if (hargaSatuanItemSaatIni > 0 && jumlah > 0) {
                double hargaAkhir = hargaSatuanItemSaatIni * jumlah;
                txtHargaAkhirItem.setText(formatter.format(hargaAkhir));
            } else {
                txtHargaAkhirItem.setText(formatter.format(0));
            }
        } catch (NumberFormatException e) {
            txtHargaAkhirItem.setText(formatter.format(0));
        }
    }
    
    private void hitungTotalTransaksi() {
        double subTotal = 0.0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String hargaAkhirStr = (String) tableModel.getValueAt(i, 4);
            // Menghilangkan format Rp, spasi, pemisah ribuan (titik), lalu mengubah koma jadi titik untuk parsing
            hargaAkhirStr = hargaAkhirStr.replace("Rp ", "").replace(".", "").replace(",", ".");
            try {
                subTotal += Double.parseDouble(hargaAkhirStr);
            } catch (NumberFormatException ex) { /* Abaikan */ }
        }
        
        // 1. Hitung Diskon
        double diskonPersen = 0;
        try {
            String diskonText = txtDiskonPersen.getText().trim();
            // PERBAIKAN: Pastikan input diskon tidak kosong sebelum parsing
            diskonPersen = Double.parseDouble(diskonText.isEmpty() ? "0" : diskonText);
        } catch (NumberFormatException e) { /* diskon tetap 0 */ }
        
        double diskonRupiah = subTotal * (diskonPersen / 100);
        
        // 2. Hitung Total Harga
        double totalHarga = subTotal - diskonRupiah;
        
        // 3. Update Field
        txtSubTotal.setText(formatter.format(subTotal));
        txtDiskonRupiah.setText(formatter.format(diskonRupiah));
        txtTotalHarga.setText(formatter.format(totalHarga));
        lblTagihanBesar.setText("Tagihan : " + formatter.format(totalHarga));
        
        hitungKembalian();
    }

    private void hitungKembalian() {
        try {
            // Hilangkan format Rp, spasi, pemisah ribuan (titik), lalu ubah koma jadi titik untuk parsing
            String totalHargaStr = txtTotalHarga.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            double totalHarga = Double.parseDouble(totalHargaStr);

            String bayarStr = txtBayar.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            bayarStr = bayarStr.replaceAll("[^\\d\\.,]", ""); 
            
            // !!! PERBAIKAN (Baris 381): Menangani 'For input string: ""' jika field Bayar kosong
            double bayar = Double.parseDouble(bayarStr.isEmpty() ? "0" : bayarStr.replace(",", ".")); 

            double kembalian = bayar - totalHarga;
            txtKembalian.setText(formatter.format(kembalian));
        } catch (NumberFormatException e) {
            txtKembalian.setText(formatter.format(0));
        }
    }
    
    // =======================================================
    // III. LOGIKA AKSI (DENGAN DATABASE)
    // =======================================================
    
    private void cariBarangAction(ActionEvent evt) {
        String kode = txtKodeBarang.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan Kode Barang untuk mencari.", "Cari Barang", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "SELECT nama_barang, stok, harga_jual FROM barang WHERE kode_barang = ?";
        
        // Menggunakan KoneksiDB
        Connection conn = KoneksiDB.getKoneksi(); 
        PreparedStatement pst = null;
        ResultSet rs = null;

        batalItemAction(null); 

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, kode);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                String nama = rs.getString("nama_barang");
                int stok = rs.getInt("stok");
                double hargaJual = rs.getDouble("harga_jual");

                txtNamaBarang.setText(nama);
                txtStok.setText(String.valueOf(stok));
                txtHargaSatuan.setText(formatter.format(hargaJual));
                
                this.hargaSatuanItemSaatIni = hargaJual;
                
                if (txtJumlahJual.getText().isEmpty()) {
                     txtJumlahJual.setText("1");
                }
                hitungHargaAkhir();
            } else {
                JOptionPane.showMessageDialog(this, "Barang dengan kode " + kode + " tidak ditemukan.", "Cari Barang", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mencari barang: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Tutup resource JDBC
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pst != null) pst.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void simpanItemAction(ActionEvent evt) {
        String kode = txtKodeBarang.getText();
        String nama = txtNamaBarang.getText();
        String hargaSatuan = txtHargaSatuan.getText();
        String jumlah = txtJumlahJual.getText();
        String hargaAkhir = txtHargaAkhirItem.getText();
        
        if (kode.isEmpty() || jumlah.isEmpty() || nama.isEmpty() || hargaSatuanItemSaatIni == 0.0) {
            JOptionPane.showMessageDialog(this, "Lengkapi detail item atau cari barang terlebih dahulu.", "Simpan Item", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int jmlJual = Integer.parseInt(jumlah);
            int stokSaatIni = Integer.parseInt(txtStok.getText());
            
            if (jmlJual > stokSaatIni) {
                JOptionPane.showMessageDialog(this, "Jumlah jual (" + jmlJual + ") melebihi stok yang tersedia (" + stokSaatIni + ").", "Simpan Item", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (jmlJual <= 0) {
                 JOptionPane.showMessageDialog(this, "Jumlah jual harus lebih dari 0.", "Simpan Item", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Input jumlah jual tidak valid.", "Simpan Item", JOptionPane.WARNING_MESSAGE);
             return;
        }
        
        tableModel.addRow(new Object[]{kode, nama, hargaSatuan, jumlah, hargaAkhir, "Hapus"});
        
        batalItemAction(null); 
        hitungTotalTransaksi();
    }
    
    private void batalItemAction(ActionEvent evt) {
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        txtStok.setText("");
        txtHargaSatuan.setText("");
        txtJumlahJual.setText("1");
        txtHargaAkhirItem.setText(formatter.format(0));
        this.hargaSatuanItemSaatIni = 0.0;
    }
    
    private void hapusSemuaItemAction(ActionEvent evt) {
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus semua item dari daftar penjualan?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0); 
            hitungTotalTransaksi(); 
            JOptionPane.showMessageDialog(this, "Semua item telah dihapus.");
        }
    }
    
    private void lihatPenjualanAction(ActionEvent evt) {
        this.setVisible(false);
        // Asumsi: Ada kelas DaftarPenjualan(JFrame parent, int userId)
        // Jika tidak ada, ganti dengan LaporanPenjualan(this) jika hanya untuk melihat
        // DaftarPenjualan daftar = new DaftarPenjualan(this, this.currentUserId);
        LaporanPenjualan laporan = new LaporanPenjualan(this);
        laporan.setVisible(true);
    }
    
    // --- TAMBAHAN: Logika Logout ---
    private void logoutAction(ActionEvent evt) {
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin Logout dan kembali ke halaman Login?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            // Buat form Login baru
            Login login = new Login();
            login.setVisible(true);
            // Tutup form InputPenjualan saat ini
            this.dispose(); 
        }
    }
    // --- END TAMBAHAN ---
    
    // =======================================================
    // LOGIC UTAMA: SIMPAN TRANSAKSI KE DATABASE (TRANSACTION)
    // =======================================================
    private void simpanTransaksiAction(ActionEvent evt) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada item untuk disimpan.", "Simpan Transaksi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Menggunakan KoneksiDB
        Connection conn = KoneksiDB.getKoneksi(); 
        PreparedStatement pstHeader = null;
        PreparedStatement pstDetail = null;
        PreparedStatement pstUpdateStok = null;

        try {
            // 1. Ambil & Validasi data transaksi. Hilangkan format mata uang 'Rp '
            String idNota = txtNoNota.getText();
            
            // SubTotal
            String subTotalStr = txtSubTotal.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            if (subTotalStr.isEmpty()) subTotalStr = "0"; 

            // Diskon Persen
            String diskonPersenText = txtDiskonPersen.getText().trim();
            if (diskonPersenText.isEmpty()) diskonPersenText = "0"; 
            
            // Diskon Rupiah
            String diskonRupiahStr = txtDiskonRupiah.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            if (diskonRupiahStr.isEmpty()) diskonRupiahStr = "0";

            // Total Harga
            String totalBayarStr = txtTotalHarga.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            if (totalBayarStr.isEmpty()) totalBayarStr = "0";
            
            // Jumlah Bayar (Field yang Paling Berpotensi Kosong)
            String jumlahBayarStr = txtBayar.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            // Membersihkan sisa karakter non-digit/pemisah
            jumlahBayarStr = jumlahBayarStr.replaceAll("[^\\d\\.,]", ""); 
            if (jumlahBayarStr.isEmpty()) jumlahBayarStr = "0"; 
            
            // Kembalian
            String kembalianStr = txtKembalian.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            if (kembalianStr.isEmpty()) kembalianStr = "0";

            // Konversi ke Double setelah penanganan string kosong
            double totalBayar = Double.parseDouble(totalBayarStr);
            double jumlahBayar = Double.parseDouble(jumlahBayarStr);


            if (jumlahBayar < totalBayar) {
                JOptionPane.showMessageDialog(this, "Jumlah Bayar kurang dari Total Harga!", "Simpan Transaksi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mulai Transaksi Database (penting untuk menjaga integritas data)
            conn.setAutoCommit(false); 

            // 2. INSERT INTO penjualan_header
            String sqlHeader = "INSERT INTO penjualan_header (id_nota, id_kasir, tanggal_transaksi, sub_total, diskon_persen, diskon_rupiah, total_bayar, jumlah_bayar, kembalian) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?)";
            pstHeader = conn.prepareStatement(sqlHeader);
            pstHeader.setString(1, idNota);
            pstHeader.setInt(2, this.currentUserId);
            pstHeader.setDouble(3, Double.parseDouble(subTotalStr));
            pstHeader.setInt(4, Integer.parseInt(diskonPersenText)); 
            pstHeader.setDouble(5, Double.parseDouble(diskonRupiahStr));
            pstHeader.setDouble(6, totalBayar);
            pstHeader.setDouble(7, jumlahBayar);
            pstHeader.setDouble(8, Double.parseDouble(kembalianStr));
            pstHeader.executeUpdate();

            // 3. INSERT INTO penjualan_detail & UPDATE stok (Batch Operation)
            String sqlDetail = "INSERT INTO penjualan_detail (id_nota, kode_barang, nama_barang_snapshot, harga_satuan_jual, jumlah_jual, total_harga_item) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlUpdateStok = "UPDATE barang SET stok = stok - ? WHERE kode_barang = ?";
            
            pstDetail = conn.prepareStatement(sqlDetail);
            pstUpdateStok = conn.prepareStatement(sqlUpdateStok);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String kodeBarang = (String) tableModel.getValueAt(i, 0);
                String namaBarang = (String) tableModel.getValueAt(i, 1);
                // Konversi harga satuan
                String hargaSatuan = ((String) tableModel.getValueAt(i, 2)).replace("Rp ", "").replace(".", "").replace(",", ".");
                String jumlahJual = (String) tableModel.getValueAt(i, 3);
                // Konversi harga akhir
                String hargaAkhir = ((String) tableModel.getValueAt(i, 4)).replace("Rp ", "").replace(".", "").replace(",", ".");
                
                int jumlah = Integer.parseInt(jumlahJual);

                // Detail
                pstDetail.setString(1, idNota);
                pstDetail.setString(2, kodeBarang);
                pstDetail.setString(3, namaBarang);
                pstDetail.setDouble(4, Double.parseDouble(hargaSatuan));
                pstDetail.setInt(5, jumlah);
                pstDetail.setDouble(6, Double.parseDouble(hargaAkhir));
                pstDetail.addBatch();

                // Update Stok
                pstUpdateStok.setInt(1, jumlah);
                pstUpdateStok.setString(2, kodeBarang);
                pstUpdateStok.addBatch();
            }

            pstDetail.executeBatch();
            pstUpdateStok.executeBatch();
            
            // Commit Transaksi: Jika semua berhasil, data disimpan permanen
            conn.commit();
            
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan dengan No. Nota: " + idNota + "!", "Simpan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            batalTransaksiAction(null); 
            
        } catch (NumberFormatException | SQLException e) {
            // Error ini akan menangkap NumberFormatException (For input string) dan SQLException (Database)
            JOptionPane.showMessageDialog(this, "Error saat menyimpan transaksi: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            
            // Rollback jika terjadi error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaksi dibatalkan (Rollback).");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Kembalikan autocommit dan tutup resource
            try { 
                if (conn != null) conn.setAutoCommit(true);
                if (pstHeader != null) pstHeader.close(); 
                if (pstDetail != null) pstDetail.close(); 
                if (pstUpdateStok != null) pstUpdateStok.close(); 
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
        }
    }
    
    private void batalTransaksiAction(ActionEvent evt) {
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin membatalkan transaksi ini?", "Konfirmasi Batal", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            setNotaDanTanggalOtomatis(); 
            tableModel.setRowCount(0);
            batalItemAction(null);
            
            txtSubTotal.setText(formatter.format(0));
            txtDiskonPersen.setText("0");
            txtDiskonRupiah.setText(formatter.format(0));
            txtTotalHarga.setText(formatter.format(0));
            txtBayar.setText(formatter.format(0));
            txtKembalian.setText(formatter.format(0));
            
            hitungTotalTransaksi(); 
            JOptionPane.showMessageDialog(this, "Transaksi dibatalkan.");
        }
    }


    // =======================================================
    // IV. CLASS UTILITY (MAIN & BUTTON JTABLE)
    // =======================================================
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InputPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            // ID Kasir 99 digunakan jika dijalankan langsung (tanpa login)
            new InputPenjualan(null, 99).setVisible(true); 
        });
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Hapus");
            setBackground(new Color(255, 102, 102)); 
            setForeground(Color.WHITE);
        }
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private int row;

        public ButtonEditor(JTextField txt) {
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(this);
            button.setBackground(new Color(255, 102, 102));
            button.setForeground(Color.WHITE);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            label = (value == null) ? "Hapus" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int konfirmasi = JOptionPane.showConfirmDialog(table, 
                        "Yakin ingin menghapus item ini?", 
                        "Hapus Item", 
                        JOptionPane.YES_NO_OPTION);
                
                if (konfirmasi == JOptionPane.YES_OPTION) {
                    ((DefaultTableModel) table.getModel()).removeRow(row);
                    InputPenjualan.this.hitungTotalTransaksi(); 
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
        }
    }
}