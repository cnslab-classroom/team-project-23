import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();

    // Add a beverage to the inventory
    public void addBeverage(String name, int quantity) {
        inventory.put(name, inventory.getOrDefault(name, 0) + quantity);
        System.out.println("Added " + quantity + " units of " + name + " to inventory.");
    }

    // Check if the inventory has enough quantity of a beverage
    public boolean checkInventory(String name, int quantity) {
        return inventory.getOrDefault(name, 0) >= quantity;
    }

    // Reduce the inventory by a certain quantity of a beverage
    public void reduceInventory(String name, int quantity) {
        if (checkInventory(name, quantity)) {
            inventory.put(name, inventory.get(name) - quantity);
            System.out.println("Reduced " + quantity + " units of " + name + " from inventory.");
        } else {
            System.out.println("Insufficient inventory for " + name);
        }
    }

    // Replenish the inventory by adding a certain quantity of a beverage
    public void replenishInventory(String name, int quantity) {
        addBeverage(name, quantity);
    }

    // Display the current inventory status
    public void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }
    }
}