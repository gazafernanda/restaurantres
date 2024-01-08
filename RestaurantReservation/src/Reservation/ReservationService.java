package Reservation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Main.DatabaseManager;
import Employee.EmployeeService;
import java.util.*;

public class ReservationService {
    private Connection koneksi;

    public ReservationService() {
        try {
            koneksi = DatabaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kelolaReservasi(EmployeeService layananKaryawan) {
        Scanner scanner = new Scanner(System.in);
        int pilihan;

        do {
            System.out.println("1.Tambah Reservasi");
            System.out.println("2.Perbarui Status Reservasi");
            System.out.println("3.Check Out");
            System.out.println("4.Tampilkan Semua Reservasi");
            System.out.println("5.Kembali");
            System.out.print("Pilih menu: ");
            pilihan = scanner.nextInt();

            switch (pilihan) {
                case 1:
                    tambahReservasi(layananKaryawan);
                    break;
                case 2:
                    perbaruiStatusReservasi();
                    break;
                case 3:
                    checkOut();
                    break;
                case 4:
                    tampilkanSemuaReservasi();
                    break;
                case 5:
                    System.out.println("Kembali ke menu utama manajemen reservasi.");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        } while (pilihan != 5);
    }

    private void tambahReservasi(EmployeeService layananKaryawan) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan nama pemesan: ");
        String namaPelanggan = scanner.nextLine();
        System.out.print("Masukkan jumlah meja: ");
        int jumlahMeja = scanner.nextInt();
        System.out.print("Masukkan tipe meja (Romantic/General/Family): ");
        String tipeMeja = scanner.next();
        System.out.print("Masukkan jumlah orang per meja: ");
        int jumlahOrang = scanner.nextInt();
        String lokasiCabang = layananKaryawan.getLokasiCabangKaryawan();
        masukkanReservasiKeDatabase(namaPelanggan, jumlahMeja, tipeMeja, jumlahOrang, lokasiCabang);

        System.out.println("Reservasi berhasil ditambahkan!");
    }

    private void masukkanReservasiKeDatabase(String namaPelanggan, int jumlahMeja, String tipeMeja, int jumlahOrang, String lokasiCabang) {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("INSERT INTO reservations (customer_name, table_count, table_type, people_count, branch_location, status) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, namaPelanggan);
            preparedStatement.setInt(2, jumlahMeja);
            preparedStatement.setString(3, tipeMeja);
            preparedStatement.setInt(4, jumlahOrang);
            preparedStatement.setString(5, lokasiCabang);
            preparedStatement.setString(6, ReservationStatus.IN_RESERVE.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void perbaruiStatusReservasi() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan ID reservasi yang akan diubah: ");
        int idReservasi = scanner.nextInt();
        System.out.print("Masukkan status baru (IN_RESERVE/IN_ORDER/FINALIZED): ");
        String statusBaru = scanner.next();
        perbaruiStatusReservasiDiDatabase(idReservasi, statusBaru);

        System.out.println("Status reservasi berhasil diubah!");
    }

    private void perbaruiStatusReservasiDiDatabase(int idReservasi, String statusBaru) {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("UPDATE reservations SET status = ? WHERE id = ?")) {
            preparedStatement.setString(1, statusBaru);
            preparedStatement.setInt(2, idReservasi);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkOut() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan ID reservasi yang akan di-check out: ");
        int idReservasi = scanner.nextInt();
        checkOutReservasi(idReservasi);
    }

    private void checkOutReservasi(int idReservasi) {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("SELECT * FROM reservations WHERE id = ?")) {
            preparedStatement.setInt(1, idReservasi);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String namaPelanggan = resultSet.getString("customer_name");
                int jumlahMeja = resultSet.getInt("table_count");
                String tipeMeja = resultSet.getString("table_type");
                int jumlahOrang = resultSet.getInt("people_count");
                ReservationStatus statusReservasi = ReservationStatus.valueOf(resultSet.getString("status"));
                double totalHarga = hitungTotalHarga(tipeMeja, jumlahOrang);
                tampilkanTagihan(namaPelanggan, jumlahMeja, tipeMeja, jumlahOrang, totalHarga);
                perbaruiStatusReservasiDiDatabase(idReservasi, ReservationStatus.FINALIZED.toString());

                System.out.println("Check out berhasil!");
            } else {
                System.out.println("Reservasi dengan ID " + idReservasi + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double hitungTotalHarga(String tipeMeja, int jumlahOrang) {
        double hargaDasar;
        switch (tipeMeja) {
            case "Romantic":
                hargaDasar = 100000.0;
                break;
            case "General":
                hargaDasar = 75000.0;
                break;
            case "Family":
                hargaDasar = 120000.0;
                break;
            default:
                hargaDasar = 0.0;
        }

        return hargaDasar * jumlahOrang;
    }

    private void tampilkanTagihan(String namaPelanggan, int jumlahMeja, String tipeMeja, int jumlahOrang, double totalHarga) {
        System.out.println("========== Tagihan ==========");
        System.out.println("Pelanggan: " + namaPelanggan);
        System.out.println("Jumlah Meja: " + jumlahMeja);
        System.out.println("Tipe Meja: " + tipeMeja);
        System.out.println("Jumlah Orang: " + jumlahOrang);
        System.out.println("Total Harga: Rp " + totalHarga);
        System.out.println("=============================");
    }

    private void tampilkanSemuaReservasi() {
        try (Statement statement = koneksi.createStatement()) {
            String query = "SELECT * FROM reservations";
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("========== Semua Reservasi ==========");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String namaPelanggan = resultSet.getString("customer_name");
                int jumlahMeja = resultSet.getInt("table_count");
                String tipeMeja = resultSet.getString("table_type");
                int jumlahOrang = resultSet.getInt("people_count");
                String status = resultSet.getString("status");

                System.out.println("ID: " + id + ", Pelanggan: " + namaPelanggan + ", Jumlah Meja: " + jumlahMeja + ", Tipe Meja: " + tipeMeja + ", Jumlah Orang: " + jumlahOrang + ", Status: " + status);
            }
            System.out.println("=================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
