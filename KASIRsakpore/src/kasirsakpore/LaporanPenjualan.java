package kasirsakpore;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar; // Import untuk manipulasi tanggal
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.TitledBorder;

// Import untuk JDBC
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Import tipe data Timestamp SQL

// IMPORT KONEKSI DB DARI PACKAGE DAN NAMA FILE YANG BENAR
import koneksi.KoneksiDB; 

/**
 * Form Laporan Penjualan.
 * Menampilkan riwayat transaksi tanpa tombol cetak struk, dilengkapi sortir tanggal dan total pendapatan.
 */
public class LaporanPenjualan extends JFrame {

    private JFrame parentDashboard;
    private JTable penjualanTable;
    private DefaultTableModel tableModel;
    private JTextField txtTglMulai;
    private JTextField txtTglSelesai;
    private JButton btnSortir;
    private JLabel lblTotalPendapatan;
    
    // Formatter untuk mata uang
    private final DecimalFormat formatter = new DecimalFormat("Rp #,##0.00", new DecimalFormatSymbols(new Locale("id", "ID")));
    
    // Format untuk parsing tanggal dari input (DD/MM/YYYY)
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Format untuk tanggal database (YYYY-MM-DD) - TIDAK DIPAKAI LAGI SETELAH KOREKSI
    // private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanPenjualan(JFrame parent) {
        this.parentDashboard = parent;
        setTitle("Laporan Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Menampilkan kembali Dashboard saat form ini ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (parentDashboard != null) {
                    parentDashboard.setVisible(true);
                }
            }
        });

        initComponents();
        setTanggalDefault(); // Set tanggal default hari ini
        loadDataPenjualan(null, null); // Muat data awal
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        // ... (Tidak ada perubahan di sini) ...
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        
        // --- Panel Filter & Summary ---
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(new TitledBorder("Filter Tanggal & Total Pendapatan"));
        filterPanel.setBackground(new Color(240, 240, 240));
        
        JLabel lblMulai = new JLabel("Tanggal Mulai (dd/mm/yyyy):");
        txtTglMulai = new JTextField(10);
        JLabel lblSelesai = new JLabel("Tanggal Selesai (dd/mm/yyyy):");
        txtTglSelesai = new JTextField(10);
        btnSortir = new JButton("Sortir & Hitung");
        btnSortir.setBackground(new Color(60, 140, 250));
        btnSortir.setForeground(Color.WHITE);
        
        lblTotalPendapatan = new JLabel("Total Pendapatan: " + formatter.format(0));
        lblTotalPendapatan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalPendapatan.setForeground(new Color(0, 100, 0)); // Warna Hijau Gelap
        
        // Layout Filter Panel
        GroupLayout filterLayout = new GroupLayout(filterPanel);
        filterPanel.setLayout(filterLayout);
        filterLayout.setAutoCreateGaps(true);
        filterLayout.setAutoCreateContainerGaps(true);
        filterLayout.setHorizontalGroup(
            filterLayout.createSequentialGroup()
                .addComponent(lblMulai)
                .addComponent(txtTglMulai, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSelesai)
                .addComponent(txtTglSelesai, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnSortir)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalPendapatan)
        );
        filterLayout.setVerticalGroup(
            filterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblMulai)
                .addComponent(txtTglMulai, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSelesai)
                .addComponent(txtTglSelesai, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnSortir)
                .addComponent(lblTotalPendapatan)
        );
        
        // --- Inisialisasi Tabel ---
        String[] columnNames = {"No. Nota", "Tanggal/Jam", "Kasir ID", "Sub Total", "Diskon (Rp)", "Total Harga", "Bayar", "Kembalian"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        penjualanTable = new JTable(tableModel);
        penjualanTable.getTableHeader().setReorderingAllowed(false); // Cegah kolom dipindah
        
        JScrollPane tableScrollPane = new JScrollPane(penjualanTable);
        tableScrollPane.setBorder(new TitledBorder("Daftar Transaksi Penjualan"));
        
        // Layout Utama
        GroupLayout mainLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainLayout);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(filterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        mainLayout.setVerticalGroup(
            mainLayout.createSequentialGroup()
                .addComponent(filterPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        getContentPane().add(mainPanel);
        
        // --- Action Listener Sortir ---
        btnSortir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSortir();
            }
        });
    }

    private void setTanggalDefault() {
        // Set tanggal mulai ke hari ini, dan tanggal selesai ke hari ini
        String today = inputDateFormat.format(new Date());
        txtTglMulai.setText(today);
        txtTglSelesai.setText(today);
    }
    
    private void actionSortir() {
        try {
            Date tglMulaiDate = inputDateFormat.parse(txtTglMulai.getText().trim());
            Date tglSelesaiDate = inputDateFormat.parse(txtTglSelesai.getText().trim());

            // FIX: Cek apakah tanggal mulai lebih besar dari tanggal selesai
            if (tglMulaiDate.after(tglSelesaiDate)) {
                 JOptionPane.showMessageDialog(this, "Tanggal Mulai tidak boleh setelah Tanggal Selesai.", "Peringatan Filter", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            
            // Konversi ke java.sql.Timestamp untuk penggunaan PreparedStatement
            // Mulai: Pukul 00:00:00
            Timestamp tglMulaiTS = new Timestamp(tglMulaiDate.getTime());
            
            // Selesai: Pukul 23:59:59 (untuk mencakup seluruh hari terakhir)
            Calendar cal = Calendar.getInstance();
            cal.setTime(tglSelesaiDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Timestamp tglSelesaiTS = new Timestamp(cal.getTimeInMillis());
            
            // Panggil loadDataPenjualan dengan objek Timestamp
            loadDataPenjualan(tglMulaiTS, tglSelesaiTS);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan format DD/MM/YYYY.", "Error Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * FIX: Ubah parameter dari String ke java.sql.Timestamp
     */
    private void loadDataPenjualan(Timestamp tglMulai, Timestamp tglSelesai) {
        tableModel.setRowCount(0);
        double totalPendapatan = 0.0;
        
        // Query Dasar - PASTIKAN SEMUA KOLOM TERDAFTAR
        String sql = "SELECT id_nota, id_kasir, tanggal_transaksi, sub_total, diskon_rupiah, total_bayar, jumlah_bayar, kembalian FROM penjualan_header ";
        
        // Tambahkan filter tanggal jika ada
        if (tglMulai != null && tglSelesai != null) {
            // FIX SQL: Gunakan klausa WHERE standar dan PreparedStatement akan menangani tipe Timestamp
            // Ini akan membandingkan objek Timestamp dengan kolom timestamp without time zone
            sql += "WHERE tanggal_transaksi BETWEEN ? AND ? "; 
        }
        
        sql += "ORDER BY tanggal_transaksi DESC";

        Connection conn = KoneksiDB.getKoneksi(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            pst = conn.prepareStatement(sql);
            if (tglMulai != null && tglSelesai != null) {
                // FIX: Gunakan setTimestamp() untuk binding objek Timestamp
                pst.setTimestamp(1, tglMulai);
                pst.setTimestamp(2, tglSelesai);
            }
            
            rs = pst.executeQuery();
            
            while (rs.next()) {
                String idNota = rs.getString("id_nota");
                int idKasir = rs.getInt("id_kasir");
                
                // FIX: Ambil tanggal sebagai Timestamp agar aman
                Timestamp rawTimestamp = rs.getTimestamp("tanggal_transaksi");
                // Konversi Timestamp ke String yang mudah dibaca untuk tampilan
                String tanggal = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(rawTimestamp);
                
                double subTotal = rs.getDouble("sub_total");
                double diskon = rs.getDouble("diskon_rupiah");
                double totalBayar = rs.getDouble("total_bayar");
                double jumlahBayar = rs.getDouble("jumlah_bayar");
                double kembalian = rs.getDouble("kembalian");
                
                // Tambahkan ke total pendapatan
                totalPendapatan += totalBayar; 
                
                tableModel.addRow(new Object[]{
                    idNota, 
                    tanggal, // Tampilkan tanggal yang sudah diformat
                    idKasir,
                    formatter.format(subTotal), 
                    formatter.format(diskon), 
                    formatter.format(totalBayar), 
                    formatter.format(jumlahBayar),
                    formatter.format(kembalian)
                });
            }
            
            // Update label total pendapatan
            lblTotalPendapatan.setText("Total Pendapatan: " + formatter.format(totalPendapatan));

        } catch (SQLException e) {
            // Tampilkan error yang lebih spesifik untuk debugging
            JOptionPane.showMessageDialog(this, "Error saat memuat data laporan:\n" + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pst != null) pst.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Koneksi TIDAK perlu ditutup di sini jika KoneksiDB.getKoneksi() menggunakan Connection Pool atau singleton
            // Jika Anda yakin harus ditutup, tambahkan try-catch untuk conn.close()
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaporanPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new LaporanPenjualan(null).setVisible(true); 
        });
    }
}