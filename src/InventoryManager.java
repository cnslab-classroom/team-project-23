import java.util.HashMap;
import java.util.Map;

public class InventoryManager<T extends Beverage> {
    private Map<String, Integer> inventory = new HashMap<>();
    private double capital;
    private Map<String, Integer> weeklySales = new HashMap<>();
    private Map<String, Integer> monthlySales = new HashMap<>();
    private Map<String, Integer> weeklyReplenish = new HashMap<>();
    private Map<String, Integer> monthlyReplenish = new HashMap<>();

    public InventoryManager(double initialCapital) {
        this.capital = initialCapital;
    }

    // Add beverages to inventory
    public void addBeverage(String name, int quantity) {
        inventory.put(name, inventory.getOrDefault(name, 0) + quantity);
        System.out.println("Added " + quantity + " units of " + name + " to inventory.");
    }

    // Check if sufficient stock is available
    public boolean checkInventory(String name, int quantity) {
        return inventory.getOrDefault(name, 0) >= quantity;
    }

    // Reduce inventory and record sales
    public void reduceInventory(String name, int quantity) {
        if (checkInventory(name, quantity)) {
            inventory.put(name, inventory.get(name) - quantity);
            weeklySales.put(name, weeklySales.getOrDefault(name, 0) + quantity);
            monthlySales.put(name, monthlySales.getOrDefault(name, 0) + quantity);
            System.out.println("Reduced " + quantity + " units of " + name + " from inventory.");
        } else {
            throw new IllegalArgumentException("Insufficient stock.");
        }
    }

    // Restock inventory with capital check
    public void restockBeverage(T beverage, int quantity, double costPerUnit) {
        if (capital >= quantity * costPerUnit) {
            addBeverage(beverage.getName(), quantity);
            capital -= quantity * costPerUnit;
        } else {
            throw new IllegalArgumentException("Insufficient capital to restock.");
        }
    }

    // Replenish inventory (e.g., borrowed from other stores)
    public void replenishInventory(String name, int quantity) {
        addBeverage(name, quantity);
        //추가 부분, 오전에 발주로 채운게 아닌 thread가 채우는 replenish를 따로 기록
        recordReplenish(name, quantity);
    }

    // Record replenish data
    //추가 부분
    public void recordReplenish(String name, int quantity) {
        weeklyReplenish.put(name, weeklyReplenish.getOrDefault(name, 0) + quantity);
        monthlyReplenish.put(name, monthlyReplenish.getOrDefault(name, 0) + quantity);
    }

    // Display current inventory
    public void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }
    }

    // Get inventory stock
    public Map<String, Integer> getStock() {
        return inventory;
    }

    // Print weekly sales report
    public void printWeeklySales() {
        System.out.println("Weekly Sales Report:");
        for (Map.Entry<String, Integer> entry : weeklySales.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units sold");
        }
    }

    // Reset weekly sales
    public void resetWeeklySales() {
        weeklySales.clear();
    }

    // Print monthly sales report
    public void printMonthlySales() {
        System.out.println("Monthly Sales Report:");
        for (Map.Entry<String, Integer> entry : monthlySales.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units sold");
        }
    }

    // Print replenish report
    //추가 부분, 매장 운영 도중에 재고가 부족한 부분은 다른 매장에서 재고를
    // 빌려왔다는 설정(발주에 신경을 쓰라는 경고와 분석을 표현하고 싶었음)
    public void printReplenishReport(boolean isWeekly) {
        Map<String, Integer> replenishData = isWeekly ? weeklyReplenish : monthlyReplenish;
        String period = isWeekly ? "이번주" : "이번달";
        for (Map.Entry<String, Integer> entry : replenishData.entrySet()) {
            System.out.println(period + "은 " + entry.getValue() + "개의 " + entry.getKey() + "를 다른 점포에서 빌려왔습니다. 재고 관리에 신경써 주세요.");
        }
        if (isWeekly) {
            weeklyReplenish.clear(); // Clear weekly data after the report
        }
    }

    // Get available capital
    public double getCapital() {
        return capital;
    }

    // Add capital to inventory manager
    public void addCapital(double amount) {
        capital += amount;
    }
}
