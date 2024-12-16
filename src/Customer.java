// Customer.java
public class Customer {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    public void purchaseBeverage(InventoryManager inventoryManager, String beverageName, int quantity) {
        if (inventoryManager.checkInventory(beverageName, quantity)) {
            inventoryManager.reduceInventory(beverageName, quantity);
            System.out.println(name + " purchased " + quantity + " " + beverageName);
        } else {
            System.out.println("Insufficient inventory for " + beverageName);
        }
    }
}