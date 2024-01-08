package Restaurant;
import Employee.EmployeeService;
import Restaurant.RestaurantService;
import Reservation.ReservationService;
import java.util.Scanner;

public class RestaurantApp {
    private RestaurantService layananRestoran;
    private ReservationService layananReservasi;
    private EmployeeService layananKaryawan;

    public RestaurantApp() {
        this.layananRestoran = new RestaurantService();
        this.layananReservasi = new ReservationService();
        this.layananKaryawan = new EmployeeService();
    }

    public void jalankan() {
        Scanner scanner = new Scanner(System.in);
        int pilihan;

        do {
            System.out.println("1. Kelola Menu");
            System.out.println("2. Kelola Reservasi");
            System.out.println("3. Kelola Employee");
            System.out.println("4. Keluar");
            System.out.print("Pilih menu: ");
            pilihan = scanner.nextInt();

            switch (pilihan) {
                case 1:
                    layananRestoran();
                    break;
                case 2:
                    layananReservasi.kelolaReservasi(layananKaryawan);
                    break;
                case 3:
                    layananKaryawan();
                    break;
                case 4:
                    System.out.println("Keluar dari aplikasi. Terima kasih!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        } while (pilihan != 4);
    }
}
