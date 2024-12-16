public class Main {
    public static void main(String[] args) {
        InventoryManager inventoryManager = new InventoryManager();
        InventoryManagerThread inventoryThread = new InventoryManagerThread(inventoryManager, "Coffee", 10, 10000);
        inventoryThread.start();

        Customer customer1 = new Customer("Alice");
        Customer customer2 = new Customer("Bob");

        inventoryManager.addBeverage("Coffee", 10);
        inventoryManager.addBeverage("Energy Drink", 5);
        inventoryManager.addBeverage("Smoothie", 8);

        customer1.purchaseBeverage(inventoryManager, "Coffee", 2);
        customer2.purchaseBeverage(inventoryManager, "Energy Drink", 1);

        SalesAnalysis salesAnalysis = new SalesAnalysis();
        salesAnalysis.recordSale("Coffee", 2);
        salesAnalysis.recordSale("Energy Drink", 1);
        salesAnalysis.generateReport();
    }
}