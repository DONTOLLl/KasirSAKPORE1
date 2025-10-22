/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasirsakpore;

// --- IMPORTS KRITIS (DEFAULT) ---
import com.toedter.calendar.JDateChooser; 
import java.sql.Timestamp; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent; 
import java.awt.event.KeyEvent;    
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.table.DefaultTableModel;
import koneksi.KoneksiDB; 
import java.awt.BorderLayout;
import java.awt.FlowLayout; 

// --- IMPORTS UNTUK CETAK PDF (iText) ---
// ************************************************
// JIKA MASIH ERROR, CEK KEMBALI JAR iText SUDAH ADA
// ************************************************
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
// ------------------------------------

/**
 * Form untuk menampilkan Laporan Transaksi (Restock/Pembelian) dengan filter, waktu, dan fitur delete.
 * @author user
 */
public class LaporanTransaksi extends javax.swing.JFrame {

    // --- DEKLARASI PROPERTI ---
    private DefaultTableModel modelTabelLaporan; 
    private final DecimalFormat formatter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
    private final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"); 
    
    private javax.swing.JFrame parentFrame; 
    
    // VARIABEL UNTUK FILTER TANGGAL
    private JDateChooser dateChooserMulai; 
    private JDateChooser dateChooserAkhir; 
    private javax.swing.JButton btnFilter;
    
    // Variabel untuk Komponen yang ditambahkan secara manual
    private javax.swing.JPanel jPanelDelete;
    private javax.swing.JLabel jLabelDelete;
    // ---------------------------

    /**
     * Konstruktor baru yang menerima frame induk (DashboardAdmin).
     * @param parentFrame Frame Dashboard Admin yang memanggil laporan ini.
     */
    public LaporanTransaksi(javax.swing.JFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        getContentPane().setLayout(new BorderLayout()); 
        
        initComponents();
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); 
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setLocationRelativeTo(null); 
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter = new DecimalFormat("#,##0", symbols);
        
        setupFilterPanel(); 
        setupTable();
        loadDataRestock(null, null); 
        setupEscapeKey(); 
        
        if (jPanelDelete != null) {
             jPanelDelete.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jPanelDeleteMouseClicked(evt);
                }
            });
        }
    }
    
    public LaporanTransaksi() {
        this(null); 
    }
    
    // =======================================================
    // FUNGSI UTAMA CETAK PDF
    // =======================================================
    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {
        exportToPDF();
    }
    
    private void exportToPDF() {
        if (modelTabelLaporan.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Tentukan nama file dan lokasi penyimpanan
        JFileChooser fileChooser = new JFileChooser();
        String defaultFileName = "Laporan_Restock_" + fileNameDateFormat.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        fileChooser.setDialogTitle("Simpan Laporan Transaksi Restock");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();
                
                // --- SETUP FONT ---
                BaseFont bf;
                try {
                    // Coba gunakan font standar (lebih aman)
                    bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                } catch (Exception e) {
                    // Fallback jika ada masalah font
                    bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                }
                
                Font fontHeader = new Font(bf, 14, Font.BOLD, BaseColor.BLACK);
                Font fontContent = new Font(bf, 8, Font.NORMAL, BaseColor.BLACK);
                Font fontTableHeader = new Font(bf, 8, Font.BOLD, BaseColor.WHITE);
                // ---------------------------------------------------------
                
                // Judul
                Paragraph title = new Paragraph("LAPORAN TRANSAKSI RESTOCK", fontHeader);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("Tanggal Cetak: " + dateFormat.format(new Date()) + "\n\n", fontContent));

                // Table
                int numColumns = modelTabelLaporan.getColumnCount();
                // Kolom 0 (ID Detail) disembunyikan dalam laporan
                PdfPTable pdfTable = new PdfPTable(numColumns - 1); 
                pdfTable.setWidthPercentage(100);
                // Atur lebar kolom (sesuaikan jika perlu, ini adalah rasio)
                pdfTable.setWidths(new float[] {1f, 2f, 1f, 2.5f, 2f, 1f, 1.5f, 1.5f}); 

                // Header Tabel
                for (int i = 1; i < numColumns; i++) { // Mulai dari indeks 1 (skip ID Detail)
                    PdfPCell cell = new PdfPCell(new Phrase(modelTabelLaporan.getColumnName(i), fontTableHeader));
                    cell.setBackgroundColor(new BaseColor(50, 50, 50)); 
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }

                // Isi Tabel
                for (int row = 0; row < modelTabelLaporan.getRowCount(); row++) {
                    for (int col = 1; col < numColumns; col++) { // Mulai dari indeks 1
                        String value = modelTabelLaporan.getValueAt(row, col).toString();
                        PdfPCell cell = new PdfPCell(new Phrase(value, fontContent));
                        // Alignment untuk kolom angka
                        if (col >= 6) { // Kolom Jumlah Beli, Harga Satuan, Sub Total
                             cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        } else if (col == 1 || col == 2) { // No Pembelian, Tgl & Waktu
                             cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        } else {
                             cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        }
                        cell.setPadding(3);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();
                
                JOptionPane.showMessageDialog(this, 
                        "Laporan berhasil dicetak ke PDF:\n" + path, 
                        "Cetak Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (NoClassDefFoundError e) {
    JOptionPane.showMessageDialog(this, 
        "Library iText (PDF) tidak ditemukan. Pastikan JAR iText telah ditambahkan ke Libraries proyek Anda.", 
        "FATAL ERROR: Library Missing", JOptionPane.ERROR_MESSAGE);
}
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Gagal membuat PDF. Error: " + e.getMessage(), 
                        "Error Cetak", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // =======================================================
    // FUNGSI LAIN
    // =======================================================

    private void setupEscapeKey() {
        InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        inputMap.put(escapeKeyStroke, "escapeAction");
        actionMap.put("escapeAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnKembaliActionPerformed(e);
            }
        });
    }

    private void jPanelDeleteMouseClicked(java.awt.event.MouseEvent evt) {                                     
        deleteRestockTransaction();
    }    
    
    private void deleteRestockTransaction() {
        int selectedRow = jTable1.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi yang ingin dihapus terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String noPembelian = modelTabelLaporan.getValueAt(selectedRow, 1).toString(); 
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus seluruh Transaksi Pembelian/Restock No. " + noPembelian + "?\n" + 
                "Tindakan ini tidak dapat dibatalkan.", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement psDetail = null;
            PreparedStatement psHeader = null;
            
            try {
                conn = KoneksiDB.getKoneksi();
                conn.setAutoCommit(false); 
                
                String sqlDetail = "DELETE FROM detail_pembelian WHERE no_pembelian = ?";
                psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setString(1, noPembelian);
                psDetail.executeUpdate();
                
                String sqlHeader = "DELETE FROM pembelian WHERE no_pembelian = ?";
                psHeader = conn.prepareStatement(sqlHeader);
                psHeader.setString(1, noPembelian);
                psHeader.executeUpdate();
                
                conn.commit(); 
                JOptionPane.showMessageDialog(this, "Transaksi Pembelian/Restock No. " + noPembelian + " berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                loadDataRestock(dateChooserMulai.getDate(), dateChooserAkhir.getDate());
                
            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback(); 
                } catch (SQLException ex) {
                    // ignore
                }
                JOptionPane.showMessageDialog(this, "Gagal menghapus transaksi. Error: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (psDetail != null) psDetail.close(); } catch (SQLException e) { /* ignored */ }
                try { if (psHeader != null) psHeader.close(); } catch (SQLException e) { /* ignored */ }
                try { 
                    if (conn != null) conn.setAutoCommit(true); 
                } catch (SQLException e) { /* ignored */ }
            }
        }
    }
    
    private void setupFilterPanel() {
        dateChooserMulai = new JDateChooser();
        dateChooserAkhir = new JDateChooser();
        
        dateChooserMulai.setDateFormatString("dd-MM-yyyy");
        dateChooserAkhir.setDateFormatString("dd-MM-yyyy");

        btnFilter = new javax.swing.JButton("Filter Data");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        javax.swing.JPanel jPanelFilter = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        jPanelFilter.add(new javax.swing.JLabel("Dari Tanggal:"));
        jPanelFilter.add(dateChooserMulai);
        jPanelFilter.add(new javax.swing.JLabel("Sampai Tanggal:"));
        jPanelFilter.add(dateChooserAkhir);
        jPanelFilter.add(btnFilter);
        
        javax.swing.JPanel jPanelMainCenter = new javax.swing.JPanel(new BorderLayout());
        jPanelMainCenter.add(jPanelFilter, BorderLayout.NORTH);
        jPanelMainCenter.add(jScrollPane1, BorderLayout.CENTER);
        
        getContentPane().remove(jScrollPane1);
        getContentPane().add(jPanelMainCenter, BorderLayout.CENTER);
    }
    
    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {
        Date tanggalMulai = dateChooserMulai.getDate();
        Date tanggalAkhir = dateChooserAkhir.getDate();
        
        if (tanggalMulai != null && tanggalAkhir != null && tanggalMulai.after(tanggalAkhir)) {
            JOptionPane.showMessageDialog(this, "Tanggal Mulai tidak boleh setelah Tanggal Akhir.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        loadDataRestock(tanggalMulai, tanggalAkhir);
    }

    private void setupTable() {
        modelTabelLaporan = new DefaultTableModel();
        modelTabelLaporan.addColumn("ID Detail"); 
        modelTabelLaporan.addColumn("No Pembelian"); 
        modelTabelLaporan.addColumn("Tgl & Waktu"); 
        modelTabelLaporan.addColumn("Kode Barang");
        modelTabelLaporan.addColumn("Nama Barang"); 
        modelTabelLaporan.addColumn("Supplier"); 
        modelTabelLaporan.addColumn("Jml Beli"); 
        modelTabelLaporan.addColumn("Harga Satuan"); 
        modelTabelLaporan.addColumn("Sub Total"); 
        
        jTable1.setModel(modelTabelLaporan); 
        // Sembunyikan kolom ID Detail (Indeks 0)
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private void loadDataRestock(Date tglMulai, Date tglAkhir) {
        modelTabelLaporan.setRowCount(0); 
    
        String sql = "SELECT dp.id_detail, p.no_pembelian, p.tanggal, dp.kode_barang, b.nama_barang, s.nama_supplier, dp.jumlah_barang, dp.harga, dp.total_harga " +
                     "FROM detail_pembelian dp " +
                     "JOIN pembelian p ON dp.no_pembelian = p.no_pembelian " + 
                     "LEFT JOIN barang b ON dp.kode_barang = b.kode_barang " +
                     "LEFT JOIN supplier s ON dp.kode_supplier = s.kode_supplier "; 
        
        String whereClause = "";
        if (tglMulai != null && tglAkhir != null) {
            String tglMulaiStr = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(tglMulai);
            String tglAkhirStr = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(tglAkhir); 
            whereClause = "WHERE p.tanggal BETWEEN '" + tglMulaiStr + "' AND '" + tglAkhirStr + "' ";
        }
        
        sql += whereClause + "ORDER BY p.tanggal DESC, p.no_pembelian DESC, dp.id_detail ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = KoneksiDB.getKoneksi();
            if (conn == null) {
                 JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.", "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
                
            while (rs.next()) {
                long hargaBeli = rs.getLong("harga");
                long subTotal = rs.getLong("total_harga"); 
                Timestamp timestamp = rs.getTimestamp("tanggal");
                
                Object[] row = {
                    rs.getInt("id_detail"),
                    rs.getString("no_pembelian"),
                    timestamp != null ? dateFormat.format(timestamp) : "Tgl. Tidak Ada", 
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang") != null ? rs.getString("nama_barang") : "N/A", 
                    rs.getString("nama_supplier") != null ? rs.getString("nama_supplier") : "Supplier Tidak Ada", 
                    rs.getInt("jumlah_barang"),
                    "Rp " + formatter.format(hargaBeli),
                    "Rp " + formatter.format(subTotal)
                };
                modelTabelLaporan.addRow(row);
            }
            
        } catch (SQLException e) {
            String errorMessage = "Gagal memuat data Laporan Restock. Cek koneksi atau struktur DB.\n" +
                                  "Error: " + e.getMessage();
            JOptionPane.showMessageDialog(this, errorMessage, "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignored */ }
        }
    }
    
    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {
        if (parentFrame != null) {
            parentFrame.setVisible(true); 
        }
        this.dispose(); 
    }


    // =======================================================
    // GENERATED CODE (NETBEANS) - Jangan Diubah
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
        
        // INISIALISASI KOMPONEN BARU
        jPanelDelete = new javax.swing.JPanel();
        jLabelDelete = new javax.swing.JLabel();
        
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); 
        jScrollPane1.setViewportView(jTable1);
        
        // --- FOOTER (SOUTH) ---
        jPanelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10)); 
        
        // Panel HAPUS 
        jPanelDelete.setBackground(new java.awt.Color(255, 153, 153));
        jLabelDelete.setText("HAPUS TRANSAKSI");
        
        javax.swing.GroupLayout jPanelDeleteLayout = new javax.swing.GroupLayout(jPanelDelete);
        jPanelDelete.setLayout(jPanelDeleteLayout);
        jPanelDeleteLayout.setHorizontalGroup(
            jPanelDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDeleteLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabelDelete)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanelDeleteLayout.setVerticalGroup(
            jPanelDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDeleteLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabelDelete)
                .addContainerGap())
        );
        jPanelFooter.add(jPanelDelete); 

        // Panel KEMBALI
        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel2.setText("KEMBALI (ESC)");
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