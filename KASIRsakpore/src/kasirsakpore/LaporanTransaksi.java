/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

// --- IMPORTS WAJIB ---
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; 
// --- IMPORTS DESAIN (BorderLayout) ---
import java.awt.BorderLayout;
import java.awt.FlowLayout; 
// ------------------------------------

/**
 * Form untuk menampilkan Laporan Transaksi (Restock/Pembelian).
 * @author user
 */
public class LaporanTransaksi extends javax.swing.JFrame {

    // --- DEKLARASI PROPERTI ---
    private DefaultTableModel modelTabelLaporan; 
    private final DecimalFormat formatter;
    // ---------------------------

    public LaporanTransaksi() {
        // Menggunakan BorderLayout untuk layout yang lebih fleksibel
        getContentPane().setLayout(new BorderLayout()); 
        
        initComponents();
        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setLocationRelativeTo(null); 
        
        // Inisialisasi formatter untuk format angka mata uang
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter = new DecimalFormat("#,##0", symbols);
        
        setupTable();
        loadDataRestock(); 
    }

    // =======================================================
    // HELPER METHODS
    // =======================================================
    
    /**
     * Mengatur model tabel dan header kolom untuk Laporan Restock.
     */
    private void setupTable() {
        modelTabelLaporan = new DefaultTableModel();
        // Kolom Disesuaikan dengan JOIN 3 Tabel
        modelTabelLaporan.addColumn("ID Detail"); 
        modelTabelLaporan.addColumn("No Pembelian"); 
        modelTabelLaporan.addColumn("Tanggal Pembelian"); 
        modelTabelLaporan.addColumn("Kode Barang");
        modelTabelLaporan.addColumn("Nama Barang"); 
        modelTabelLaporan.addColumn("Supplier"); 
        modelTabelLaporan.addColumn("Jml Beli"); 
        modelTabelLaporan.addColumn("Harga Satuan"); 
        modelTabelLaporan.addColumn("Sub Total"); 
        
        // Asumsi nama JTable adalah jTable1
        jTable1.setModel(modelTabelLaporan); 
    }

    /**
     * Memuat data restock menggunakan JOIN dari pembelian, detail_pembelian, dan barang.
     */
    private void loadDataRestock() {
        modelTabelLaporan.setRowCount(0); 
    
        // Mengambil data detail (dp), menggabungkan dengan transaksi master (p), dan info barang (b)
        // Pastikan semua nama tabel dan kolom di DB Anda menggunakan huruf kecil (pembelian, detail_pembelian, barang)
        String sql = "SELECT dp.id_detail, p.no_pembelian, p.tanggal_pembelian, dp.kode_barang, b.nama_barang, p.nama_supplier, dp.jumlah_barang, dp.harga, dp.total_harga " +
                     "FROM detail_pembelian dp " +
                     "JOIN pembelian p ON dp.no_pembelian = p.no_pembelian " + 
                     "LEFT JOIN barang b ON dp.kode_barang = b.kode_barang " + 
                     "ORDER BY p.tanggal_pembelian DESC, p.no_pembelian DESC, dp.id_detail ASC";
        
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            if (conn == null) {
                 JOptionPane.showMessageDialog(this, "Koneksi ke database gagal. Pastikan server PostgreSQL berjalan.", "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    long hargaBeli = rs.getLong("harga");
                    long subTotal = rs.getLong("total_harga"); 
                    
                    Object[] row = {
                        rs.getInt("id_detail"),
                        rs.getString("no_pembelian"),
                        rs.getDate("tanggal_pembelian").toString(), 
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang") != null ? rs.getString("nama_barang") : "N/A", // Jika nama barang null, tampilkan N/A
                        rs.getString("nama_supplier"),
                        rs.getInt("jumlah_barang"),
                        "Rp " + formatter.format(hargaBeli),
                        "Rp " + formatter.format(subTotal)
                    };
                    modelTabelLaporan.addRow(row);
                }
            }
            
        } catch (SQLException e) {
            // Tampilkan error SQL yang spesifik
            JOptionPane.showMessageDialog(this, "Gagal memuat data Laporan Restock. Cek JOIN tabel (pembelian, detail_pembelian, barang).\nError: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } 
    }
    
    // --- EVENT HANDLER KEMBALI ---
    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose(); 
    }
    
    // --- EVENT HANDLER CETAK (Placeholder) ---
    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Fitur Cetak belum diimplementasikan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }


    // =======================================================
    // GENERATED CODE (NETBEANS) - Sesuaikan dengan form Anda
    // =======================================================

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanelFooter = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // --- HEADER (NORTH) ---
        jPanelHeader.setBackground(new java.awt.Color(204, 255, 204));
        jPanelHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        jLabel1.setText("LAPORAN TRANSAKSI RESTOCK");
        jPanelHeader.add(jLabel1);

        getContentPane().add(jPanelHeader, java.awt.BorderLayout.NORTH);

        // --- TABEL (CENTER) ---
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
        jScrollPane1.setViewportView(jTable1);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        // --- FOOTER (SOUTH) ---
        jPanelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10)); 

        // Panel KEMBALI
        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel2.setText("KEMBALI");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel2)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        
        // Panel CETAK
        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel3MouseClicked(evt);
            }
        });
        
        jLabel3.setText("CETAK");
        
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel3)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jPanelFooter.add(jPanel3); // CETAK
        jPanelFooter.add(jPanel2); // KEMBALI
        
        getContentPane().add(jPanelFooter, java.awt.BorderLayout.SOUTH);


        pack();
    }// </editor-fold>                        

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {                                     
        btnKembaliActionPerformed(null); 
    }                                    

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {                                     
        btnKembaliActionPerformed(null); 
    }       
    
    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {                                     
        btnCetakActionPerformed(null);
    } 

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelFooter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration                   
}