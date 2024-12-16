// SalesAnalysis.java
import java.util.HashMap;
import java.util.Map;

public class SalesAnalysis {
    private Map<String, Integer> salesData = new HashMap<>();

    public void recordSale(String beverageName, int quantity) {
        salesData.put(beverageName, salesData.getOrDefault(beverageName, 0) + quantity);
    }

    public void generateReport() {
        System.out.println("Sales Report:");
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units sold");
        }
    }
}