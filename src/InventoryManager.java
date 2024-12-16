import java.util.*;

public class InventoryManager {
    private Inventory inventory;
    private Map<Beverage, Integer> weeklySales;

    public InventoryManager(Inventory inventory) {
        this.inventory = inventory;
        this.weeklySales = new HashMap<>();
    }

    public void sellBeverage(Beverage beverage, int quantity) {
        int totalCost = beverage.getPrice() * quantity;
        inventory.deductStock(beverage, quantity);
        inventory.adjustCapital(totalCost);
        weeklySales.put(beverage, weeklySales.getOrDefault(beverage, 0) + quantity);
    }

    public void restockBeverage(Beverage beverage, int quantity, int costPerUnit) {
        int totalCost = costPerUnit * quantity;
        if (totalCost > inventory.getCapital()) {
            throw new IllegalArgumentException("Not enough capital to restock.");
        }
        inventory.addStock(beverage, quantity);
        inventory.adjustCapital(-totalCost);
    }

    public void printWeeklySales() {
        System.out.println("Weekly Sales Report:");
        for (Map.Entry<Beverage, Integer> entry : weeklySales.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue() + " units sold");
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
