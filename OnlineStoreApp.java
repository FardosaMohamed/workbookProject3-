import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            System.out.println("3. Checkout");
            System.out.println("4. Exit");
            System.out.println("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayProducts();
                    break;
                case "2":
                    displayCart();
                    break;
                case "3":
                    checkout();
                    break;
                case "4":
                    System.out.println("Thanks for visiting!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    static void displayProducts() {
        System.out.println("\nAvailable Products:");
        System.out.printf("%-10s %-30s %-10s %-20s\n", "SKU", "Name", "Price", "Department");
        System.out.println("-------------------------------------------------------------------------------");
        for (Product product : inventory.values()) {
            System.out.println(product);
        }

        System.out.println("\nEnter SKU to add to cart or press Enter to return:");
        String sku = scanner.nextLine().trim();
        if (!sku.isEmpty() && inventory.containsKey(sku)) {
            Product selected = inventory.get(sku);
            if (cart.containsKey(sku)) {
                cart.get(sku).quantity++;
            } else {
                cart.put(sku, new CartItem(selected));
            }
            System.out.println(selected.name + " added to cart.");
        } else if (!sku.isEmpty()) {
            System.out.println("Invalid SKU.");
        }
    }

    static void displayCart() {
        System.out.println("\nYour Cart:");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            double total = 0;
            for (CartItem item : cart.values()) {
                System.out.println(item);
                total += item.getTotalPrice();
            }
            System.out.printf("Total: $%.2f\n", total);
        }
    }

    static void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty. Add items first.");
            return;
        }
        // Display Total sales
        double total = 0.0;
        System.out.println("\n==== Checkout ====");
        System.out.println("Items:");
        for (CartItem item : cart.values()) {
            System.out.println(item);
            total += item.getTotalPrice();
        }
        System.out.printf("Total: $%.2f\n", total);

        // Prompt for Cash Payment
        System.out.print("Enter cash amount: $");
        double paid = Double.parseDouble(scanner.nextLine());

        while (paid < total) {
            System.out.print("Insufficient amount. Please enter at least $" + total + ": ");
            paid = Double.parseDouble(scanner.nextLine());
        }
        double change = paid - total;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String orderDate = now.format(formatter);

        // Generate receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== Sales Receipt ===\n");
        receipt.append("Date: ").append(orderDate).append("\n\n");
        receipt.append("Items:\n");

        for (CartItem item : cart.values()) {
            receipt.append(item).append("\n");
        }

        receipt.append(String.format("\nTotal: $%.2f\n", total));
        receipt.append(String.format("Paid:  $%.2f\n", paid));
        receipt.append(String.format("Change:$%.2f\n", change));
        System.out.println("\n" + receipt);

        // Save to file
        saveReceiptToFile(receipt.toString(), now);

        // Clear cart
        cart.clear();
        System.out.println("\nThank you for your purchase! Returning to home screen...");
    }

    static void saveReceiptToFile(String receiptText, LocalDateTime dateTime) {
        try {
            File folder = new File("Receipts");
            if (!folder.exists()) folder.mkdirs();

            String fileName = "Receipts/" + dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(receiptText);
            writer.close();
            System.out.println("Receipt saved to: " + fileName);
        } catch (IOException e) {
            System.out.println("Failed to save receipt: " + e.getMessage());
        }
    }
}

