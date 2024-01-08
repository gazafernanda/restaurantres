package Employee;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Main.DatabaseManager;

public class EmployeeService {
    private Connection koneksi;

    public EmployeeService() {
        try {
            koneksi = DatabaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int idEmployeeSaatIni = 1;

    public String getLokasiCabangKaryawan() {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("SELECT branch_location FROM employees WHERE id = ?")) {
            preparedStatement.setInt(1, idEmployeeSaatIni);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("branch_location");
            } else {
                System.out.println("Employee with ID " + idEmployeeSaatIni + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void kelolaKaryawan() {
        Scanner scanner = new Scanner(System.in);
        int pilihan;

        do {
            System.out.println("1. Tambah Employee");
            System.out.println("2. Hapus Employee");
            System.out.println("3. Tampilkan Semua Employees");
            System.out.println("4. Kembali");
            System.out.print("Pilih menu: ");
            pilihan = scanner.nextInt();

            switch (pilihan) {
                case 1:
                    tambahEmployee();
                    break;
                case 2:
                    hapusEmployee();
                    break;
                case 3:
                    tampilkanSemuaEmployees();
                    break;
                case 4:
                    System.out.println("Kembali ke menu utama manajemen employee.");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        } while (pilihan != 4);
    }

    private void tambahEmployee() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan nama employee: ");
        String namaEmployee = scanner.nextLine();
        System.out.print("Masukkan cabang tempat bekerja: ");
        String lokasiCabang = scanner.nextLine();
        masukkanEmployeeKeDatabase(namaEmployee, lokasiCabang);
        System.out.println("Employee " + namaEmployee + " berhasil ditambahkan!");
    }

    private void masukkanEmployeeKeDatabase(String namaEmployee, String lokasiCabang) {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("INSERT INTO employees (name, branch_location) VALUES (?, ?)")) {
            preparedStatement.setString(1, namaEmployee);
            preparedStatement.setString(2, lokasiCabang);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hapusEmployee() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan ID employee yang akan dihapus: ");
        int idEmployee = scanner.nextInt();
        hapusEmployeeDariDatabase(idEmployee);

        System.out.println("Employee dengan ID " + idEmployee + " berhasil dihapus!");
    }

    private void hapusEmployeeDariDatabase(int idEmployee) {
        try (PreparedStatement preparedStatement = koneksi.prepareStatement("DELETE FROM employees WHERE id = ?")) {
            preparedStatement.setInt(1, idEmployee);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanSemuaEmployees() {
        try (Statement statement = koneksi.createStatement()) {
            String query = "SELECT * FROM employees";
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("========== Semua Employees ==========");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String namaEmployee = resultSet.getString("name");
                String lokasiCabang = resultSet.getString("branch_location");

                System.out.println("ID: " + id + ", Name: " + namaEmployee + ", Branch: " + lokasiCabang);
            }
            System.out.println("=====================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
