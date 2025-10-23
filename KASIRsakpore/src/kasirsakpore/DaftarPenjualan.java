package kasirsakpore;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.TitledBorder;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import koneksi.KoneksiDB;

/**
 * Form untuk melihat riwayat penjualan dan cetak struk.
 */
public class DaftarPenjualan extends JFrame {

    private final int currentUserId;
    private JFrame parentFrame;
    private JTable penjualanTable;
    private DefaultTableModel tableModel;
    private JButton btnKembali; // <--- DEKLARASI TOMBOL BARU
    private final DecimalFormat formatter = new DecimalFormat("Rp #,##0.00", new DecimalFormatSymbols(new Locale("id", "ID")));

    public DaftarPenjualan(JFrame parent, int userId) {
        this.parentFrame = parent;
        this.currentUserId = userId;
        setTitle("Riwayat Penjualan (Kasir ID: " + currentUserId + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Logika untuk menampilkan kembali InputPenjualan saat ini ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                kembaliKeParent();
            }
        });

        initComponents();
        setupKeyBindings(); // <--- PANGGIL SETUP KEY BINDINGS
        loadDataPenjualan();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }
    
    // --- Method Aksi Kembali ---
    private void kembaliKeParent() {
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        this.dispose(); // Tutup frame DaftarPenjualan
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        
        // --- Inisialisasi Tombol Kembali ---
        btnKembali = new JButton("Kembali (Esc)");
        btnKembali.setBackground(new Color(153, 153, 153)); // Warna abu-abu
        btnKembali.setForeground(Color.WHITE);
        btnKembali.addActionListener(e -> kembaliKeParent());
        
        // Inisialisasi Tabel
        String[] columnNames = {"No. Nota", "Tanggal/Jam", "Sub Total", "Diskon", "Total Bayar", "Bayar", "Kembalian", "Opsi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Hanya kolom Opsi yang bisa diedit (tombol)
            }
        };
        penjualanTable = new JTable(tableModel);
        
        // Menggunakan ButtonRenderer/ButtonEditor untuk kolom Opsi (untuk tombol Cetak Struk)
        penjualanTable.getColumn("Opsi").setCellRenderer(new CetakButtonRenderer());
        penjualanTable.getColumn("Opsi").setCellEditor(new CetakButtonEditor(new JTextField("Cetak Struk")));
        
        JScrollPane tableScrollPane = new JScrollPane(penjualanTable);
        tableScrollPane.setBorder(new TitledBorder("Daftar Transaksi Penjualan"));
        
        // Layout
        GroupLayout mainLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainLayout);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        
        // --- Layout Horizontal (Tambahkan Tombol Kembali di Pojok Kanan Atas) ---
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainLayout.createSequentialGroup()
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKembali, 150, 150, 150)) // <--- TAMBAHAN
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        // --- Layout Vertical ---
        mainLayout.setVerticalGroup(
            mainLayout.createSequentialGroup()
                .addComponent(btnKembali, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) // <--- TAMBAHAN
                .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        getContentPane().add(mainPanel);
    }
    
    // --- Method untuk mengaktifkan tombol ESC ---
    private void setupKeyBindings() {
        Action kembaliAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kembaliKeParent();
            }
        };

        // Mengikat tombol Escape ke JRootPane
        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ESCAPE"), "kembaliAction");
        getRootPane().getActionMap().put("kembaliAction", kembaliAction);
    }
    
    // ... (kode loadDataPenjualan, cetakStruk, CetakButtonRenderer, dan CetakButtonEditor tetap sama) ...
    // Hapus bagian ini saat menyalin kode:
    
    private void loadDataPenjualan() {
        // Hapus semua baris yang ada
        tableModel.setRowCount(0);
        
        String sql = "SELECT id_nota, tanggal_transaksi, sub_total, diskon_rupiah, total_bayar, jumlah_bayar, kembalian FROM penjualan_header WHERE id_kasir = ? ORDER BY tanggal_transaksi DESC";
        
        Connection conn = KoneksiDB.getKoneksi(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, this.currentUserId);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                String idNota = rs.getString("id_nota");
                String tanggal = rs.getString("tanggal_transaksi");
                double subTotal = rs.getDouble("sub_total");
                double diskon = rs.getDouble("diskon_rupiah");
                double totalBayar = rs.getDouble("total_bayar");
                double jumlahBayar = rs.getDouble("jumlah_bayar");
                double kembalian = rs.getDouble("kembalian");
                
                tableModel.addRow(new Object[]{
                    idNota, 
                    tanggal.substring(0, 19), // Potong milidetik
                    formatter.format(subTotal), 
                    formatter.format(diskon), 
                    formatter.format(totalBayar), 
                    formatter.format(jumlahBayar),
                    formatter.format(kembalian),
                    "Cetak Struk"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data penjualan: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pst != null) pst.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void cetakStruk(String idNota) {
        // Logika untuk membuat dan menampilkan struk
        StringBuilder strukContent = new StringBuilder();
        strukContent.append("=========================================\n");
        strukContent.append("           STRUK PENJUALAN KASIR\n");
        strukContent.append("=========================================\n");
        
        Connection conn = KoneksiDB.getKoneksi();
        PreparedStatement pstHeader = null;
        PreparedStatement pstDetail = null;
        ResultSet rsHeader = null;
        ResultSet rsDetail = null;
        
        try {
            // 1. Ambil Data Header
            String sqlHeader = "SELECT tanggal_transaksi, sub_total, diskon_rupiah, total_bayar, jumlah_bayar, kembalian FROM penjualan_header WHERE id_nota = ?";
            pstHeader = conn.prepareStatement(sqlHeader);
            pstHeader.setString(1, idNota);
            rsHeader = pstHeader.executeQuery();
            
            if (rsHeader.next()) {
                String tanggal = rsHeader.getString("tanggal_transaksi").substring(0, 19);
                double subTotal = rsHeader.getDouble("sub_total");
                double diskon = rsHeader.getDouble("diskon_rupiah");
                double totalBayar = rsHeader.getDouble("total_bayar");
                double jumlahBayar = rsHeader.getDouble("jumlah_bayar");
                double kembalian = rsHeader.getDouble("kembalian");

                strukContent.append("Nota: ").append(idNota).append("\n");
                strukContent.append("Kasir ID: ").append(this.currentUserId).append("\n");
                strukContent.append("Tgl: ").append(tanggal).append("\n");
                strukContent.append("-----------------------------------------\n");
                strukContent.append("Barang yang Dibeli:\n");

                // 2. Ambil Data Detail
                String sqlDetail = "SELECT nama_barang_snapshot, harga_satuan_jual, jumlah_jual, total_harga_item FROM penjualan_detail WHERE id_nota = ?";
                pstDetail = conn.prepareStatement(sqlDetail);
                pstDetail.setString(1, idNota);
                rsDetail = pstDetail.executeQuery();
                
                while (rsDetail.next()) {
                    String nama = rsDetail.getString("nama_barang_snapshot");
                    double hargaSatuan = rsDetail.getDouble("harga_satuan_jual");
                    int jumlah = rsDetail.getInt("jumlah_jual");
                    double totalItem = rsDetail.getDouble("total_harga_item");

                    // Format: Nama (Jml x Satuan) = Total
                    String itemLine = String.format("%-15s (%d x %s) = %s\n", 
                        nama, jumlah, formatter.format(hargaSatuan), formatter.format(totalItem));
                    strukContent.append(itemLine);
                }
                
                strukContent.append("-----------------------------------------\n");
                strukContent.append(String.format("%-15s: %20s\n", "Sub Total", formatter.format(subTotal)));
                strukContent.append(String.format("%-15s: %20s\n", "Diskon", formatter.format(diskon)));
                strukContent.append(String.format("%-15s: %20s\n", "TOTAL BAYAR", formatter.format(totalBayar)));
                strukContent.append(String.format("%-15s: %20s\n", "Dibayar", formatter.format(jumlahBayar)));
                strukContent.append(String.format("%-15s: %20s\n", "Kembalian", formatter.format(kembalian)));
                strukContent.append("=========================================\n");
                strukContent.append("           TERIMA KASIH\n");

                JOptionPane.showMessageDialog(this, strukContent.toString(), "Struk Penjualan: " + idNota, JOptionPane.PLAIN_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(this, "Data header nota " + idNota + " tidak ditemukan.", "Error Cetak", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data cetak struk: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rsHeader != null) rsHeader.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstHeader != null) pstHeader.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsDetail != null) rsDetail.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstDetail != null) pstDetail.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // =======================================================
    // CLASS UTILITY (BUTTON JTABLE)
    // =======================================================
    class CetakButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public CetakButtonRenderer() {
            setOpaque(true);
            setText("Cetak Struk");
            setBackground(new Color(60, 140, 250));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class CetakButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {
        private JButton button;
        private JTable table;
        private int row;

        public CetakButtonEditor(JTextField txt) {
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(this);
            button.setBackground(new Color(60, 140, 250));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setText("Cetak Struk");
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Cetak Struk";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
            String idNota = (String) table.getModel().getValueAt(row, 0); // Ambil No. Nota dari kolom pertama
            cetakStruk(idNota); // Panggil fungsi cetak
        }
    }
}