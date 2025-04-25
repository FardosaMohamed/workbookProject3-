import java.io.*;
import java.util.*;

class Product {
    String sku;
    String name;
    double price;
    String department;

    Product(String sku, String name, double price, String department) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.department = department;
    }

    public String toString() {
        return String.format("%-10s %-30s $%-10.2f %-20s", sku, name, price, department);
    }
}

class CartItem {
    Product product;
    int quantity;

    CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    double getTotalPrice() {
        return product.price * quantity;
    }

    public String toString() {
        return String.format("%-30s x%-3d $%-10.2f", product.name, quantity, getTotalPrice());
    }
}

public class OnlineStoreApp {
    static Map<String, Product> inventory = new HashMap<>();
    static Map<String, CartItem> cart = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        loadInventory("products.csv");
        homeScreen();
    }

    static void loadInventory(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 4) {
                try {
                    Product product = new Product(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                    inventory.put(parts[0], product);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid price in line: " + line);
                }
            } else {
                System.out.println("Skipping malformed line: " + line);
            }
        }
        reader.close();
    }

    static void homeScreen() {
        while (true) {
            System.out.println("\n=== Online Store ===");
            System.out.println("1. Display Products");
            System.out.println("2. View Cart");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": displayProducts(); break;
                case "2": displayCart(); break;
                case "3": System.out.println("Thanks for visiting!"); return;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }