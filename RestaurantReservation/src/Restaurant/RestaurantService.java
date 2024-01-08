package Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Main.DatabaseManager;
import Main.Menu;

public class RestaurantService {
    private Connection connection;
    private MenuManager menuManager;

    public RestaurantService() {
        try {
            connection = DatabaseManager.getConnection();
            menuManager = new MenuManager(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void manageMenu() {
        menuManager.manageMenu();
    }

    public class MenuManager {
        private List<Menu> menuList;
        private Connection connection;

        public MenuManager(Connection connection) {
            this.menuList = new ArrayList<>();
            this.connection = connection;
            loadMenuFromDatabase();
        }

        private void loadMenuFromDatabase() {
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM menu";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");

                    menuList.add(new Menu(id, name, price));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void displayMenu() {
            System.out.println("Daftar Menu:");
            for (Menu menu : menuList) {
                System.out.println(menu.getMenuId() + ". " + menu.getMenuName() + " - Rp " + menu.getMenuPrice());
            }
            System.out.println();
        }

        public void addMenu() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan nama menu: ");
            String name = scanner.nextLine();
            System.out.print("Masukkan harga menu: Rp ");
            double price = scanner.nextDouble();

            insertMenuToDatabase(name, price);
            loadMenuFromDatabase();

            System.out.println("Menu " + name + " berhasil ditambahkan!");
        }

        private void insertMenuToDatabase(String name, double price) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO menu (name, price) VALUES (?, ?)")) {
                preparedStatement.setString(1, name);
                preparedStatement.setDouble(2, price);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void manageMenu() {
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("1. Tampilkan Menu");
                System.out.println("2. Tambah Menu");
                System.out.println("3. Kembali");
                System.out.print("Pilih menu: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        displayMenu();
                        break;
                    case 2:
                        addMenu();
                        break;
                    case 3:
                        System.out.println("Kembali ke menu utama manajemen restaurant.");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            } while (choice != 3);
        }
    }
}
