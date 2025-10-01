/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kasirsakpore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author ISSI
 */
public class KoneksiDB {
    private static Connection koneksi;
    
    // METODE UNTUK MEMBUKA KONEKSI
    public static Connection getKoneksi() {
        if (koneksi == null) { // Cek apakah koneksi belum dibuat
            try {
                // 1. DAFTARKAN DRIVER POSTGRESQL
                Class.forName("org.postgresql.Driver");
                
                // Variabel Database (Sesuaikan dengan setup Anda!)
                String url = "jdbc:postgresql://localhost:5432/sakpore_db"; // Alamat server dan nama database
                String user = "postgres"; // Username PostgreSQL default
                String password = "YOUR_POSTGRES_PASSWORD"; // Ganti dengan password PostgreSQL Anda
                
                // 2. BUKA KONEKSI
                koneksi = DriverManager.getConnection(url, user, password);
                
                System.out.println("Koneksi ke Database Berhasil!");
                
            } catch (ClassNotFoundException e) {
                // Jika driver tidak ditemukan
                JOptionPane.showMessageDialog(null, "Driver Database Tidak Ditemukan: " + e.getMessage(), "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                // Jika ada masalah saat koneksi (salah user/pass/alamat)
                JOptionPane.showMessageDialog(null, "Gagal Koneksi ke Database: " + e.getMessage(), "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return koneksi;
    }
    
    // Metode utama untuk pengujian (opsional)
    public static void main(String[] args) {
        getKoneksi();
    }
}
