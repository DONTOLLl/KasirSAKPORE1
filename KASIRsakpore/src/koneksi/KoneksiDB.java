/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

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
    
    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                // 1. Load Driver PostgreSQL
                Class.forName("org.postgresql.Driver");
                
                // Variabel Database (SESUAIKAN INI DENGAN PENGATURAN ANDA)
                String url = "jdbc:postgresql://localhost:5432/sakpore_db"; 
                String user = "postgres"; // User default PostgreSQL
                String password = "12345"; // GANTI DENGAN PASSWORD POSTGRES ANDA
                
                // 2. Buka Koneksi
                koneksi = DriverManager.getConnection(url, user, password);
                
                System.out.println("Koneksi ke Database Berhasil!");
                
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Driver PostgreSQL tidak ditemukan.", "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal koneksi ke database. Cek URL, User, atau Password Anda.\nDetail: " + e.getMessage(), "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return koneksi;
    }
}
