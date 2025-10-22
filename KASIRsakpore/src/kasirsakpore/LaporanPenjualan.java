package kasirsakpore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LaporanPenjualan extends javax.swing.JFrame {

    public LaporanPenjualan() {
        initComponents();
        setLocationRelativeTo(null); // biar muncul di tengah layar
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanPenjualan().setVisible(true);
            }
        });
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableLaporan = new javax.swing.JTable();
        jLabelCari = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        // --- Konflik Pertama: setDefaultCloseOperation dan setTitle ---
        // Saya memilih versi yang lebih baru (DISPOSE_ON_CLOSE dan menambahkan setTitle), karena lebih aman.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Laporan Penjualan");

        jPanelMain.setBackground(new java.awt.Color(245, 247, 250));
        jPanelMain.setPreferredSize(new java.awt.Dimension(900, 600));

        jPanelHeader.setBackground(new java.awt.Color(0, 102, 204));

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); 
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("LAPORAN PENJUALAN");

        javax.swing.GroupLayout jPanelHeaderLayout = new javax.swing.GroupLayout(jPanelHeader);
        jPanelHeader.setLayout(jPanelHeaderLayout);
        jPanelHeaderLayout.setHorizontalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        jPanelHeaderLayout.setVerticalGroup(
            jPanelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addContainerGap())
        );

        tableLaporan.setFont(new java.awt.Font("Segoe UI", 0, 14));
        tableLaporan.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "No", "Tanggal", "Nama Kasir", "Nama Barang", "Jumlah", "Total Harga"
            }
        ));
        tableLaporan.setRowHeight(25);
        jScrollPane1.setViewportView(tableLaporan);

        jLabelCari.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        jLabelCari.setText("Cari:");

        btnCari.setBackground(new java.awt.Color(0, 102, 204));
        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari");

        btnRefresh.setBackground(new java.awt.Color(0, 153, 0));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Refresh");

        btnExport.setBackground(new java.awt.Color(255, 102, 0));
        btnExport.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        btnExport.setForeground(new java.awt.Color(255, 255, 255));
        btnExport.setText("Export PDF");

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addComponent(jPanelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCari)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari)
                    .addComponent(btnRefresh)
                    .addComponent(btnExport))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // --- Konflik Kedua: Pengaturan Layout Utama (layout.setHorizontalGroup) ---
            // Versi HEAD tampak rusak dan mengacu pada komponen yang tidak dideklarasikan (jPanel2, jDateChooser1, dll.).
            // Versi 13984b46c9b9f7566525efda4c5bd9f136fba0da (versi dari branch yang ditarik) adalah yang benar karena menampilkan jPanelMain.
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    // Variables declaration
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabelCari;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableLaporan;
    private javax.swing.JTextField txtCari;
    // End of variables declaration
}