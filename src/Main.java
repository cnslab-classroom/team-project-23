import java.util.*;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        InventoryManager inventoryManager = new InventoryManager(inventory);

        // Initialize beverages and add to inventory
        List<Beverage> beverages = Beverage.initializeBeverages();
        for (Beverage beverage : beverages) {
            inventory.addStock(beverage, 5); // Initial vending machine stock
        }

        // Create customers
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1, beverages.get(0), 10000)); // Example customer
        customers.add(new Customer(2, beverages.get(4), 15000)); // Example customer

        // Start 7-day simulation
        for (int day = 1; day <= 7; day++) {
            System.out.println("\nDay " + day + " simulation starts");

            // Morning (AM): Inventory Turn
            System.out.println("-- Morning: Inventory Turn --");
            for (Beverage beverage : beverages) {
                if (inventory.getStock().get(beverage) == 0) {
                    int costPerUnit = beverage.getPrice() / 2; // Example restock cost
                    try {
                        inventoryManager.restockBeverage(beverage, 5, costPerUnit);
                        System.out.println(beverage.getName() + " restocked to vending machine.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(beverage.getName() + " cannot be restocked: Sold out");
                    }
                }
            }

            // Afternoon (PM): Customer Turn
            System.out.println("-- Afternoon: Customer Turn --");
            for (Customer customer : customers) {
                Beverage preferredDrink = customer.getPreferredDrink();
                try {
                    customer.buy(preferredDrink, 1, inventoryManager);
                    System.out.println(customer.getId() + " bought " + preferredDrink.getName());
                } catch (IllegalArgumentException e) {
                    System.out.println(customer.getId() + " couldn't buy " + preferredDrink.getName() + ": Insufficient stock or budget.");
                }
            }

            // Display vending machine status
            System.out.println("\nVending Machine Status:");
            for (Beverage beverage : beverages) {
                int stock = inventory.getStock().getOrDefault(beverage, 0);
                String displayStock = stock > 0 ? stock + " units" : "Sold out";
                System.out.println("Name: " + beverage.getName() + ", Price: " + beverage.getPrice() + ", Stock: " + displayStock);
            }
        }

        // Display final total sales
        System.out.println("\nSimulation ended. Final sales report:");
        inventoryManager.printWeeklySales();
        System.out.println("Total Capital: " + inventory.getCapital());
    }
}
