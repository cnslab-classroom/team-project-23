public class Customer {
    private String id;
    private Beverage preferredDrink;

    public Customer(String id, Beverage preferredDrink) {
        this.id = id;
        this.preferredDrink = preferredDrink;
    }

    public String getId() {
        return id;
    }

    public Beverage getPreferredDrink() {
        return preferredDrink;
    }

    public void buy(Beverage beverage, int quantity, InventoryManager<Beverage> inventoryManager) {
        if (inventoryManager.checkInventory(beverage.getName(), quantity)) {
            inventoryManager.reduceInventory(beverage.getName(), quantity);
            inventoryManager.addCapital(beverage.getPrice() * quantity);
            System.out.println(id + " bought " + quantity + " " + beverage.getName());
        } else {
            throw new IllegalArgumentException("Insufficient stock.");
        }
    }
}