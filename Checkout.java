import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Checkout {
    private static void checkout() {
        System.out.println("\n--- Checkout ---");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Cannot proceed to checkout.");
            return;
        }

        for (CartItem item : cart.getItems()) {
            System.out.println(item);
        }
        double total = cart.getTotal();
        System.out.printf("Total amount due: $%.2f\n", total);

        System.out.print("Confirm checkout? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            generateReceipt();
            cart.clear();
            System.out.println("Checkout complete. Thank you for your purchase!");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    private static void generateReceipt() {
        String filename = "receipt.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("--- Receipt ---");
            for (CartItem item : cart.getItems()) {
                writer.println(item);
            }
            writer.printf("Total: $%.2f\n", cart.getTotal());
        } catch (IOException e) {
            System.out.println("Failed to write receipt: " + e.getMessage());
        }

    }
    }
