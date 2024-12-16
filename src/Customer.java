public class Customer {
    private int id;
    private Beverage preferredDrink;
    private int budget;

    public Customer(int id, Beverage preferredDrink, int budget) {
        this.id = id;
        this.preferredDrink = preferredDrink;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Beverage getPreferredDrink() {
        return preferredDrink;
    }

    public void setPreferredDrink(Beverage preferredDrink) {
        this.preferredDrink = preferredDrink;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void buy(Beverage beverage, int quantity, InventoryManager inventoryManager) {
        int totalCost = beverage.getPrice() * quantity;
        if (totalCost > budget) {
            throw new IllegalArgumentException("Not enough budget to buy this quantity.");
        }

        inventoryManager.sellBeverage(beverage, quantity);
        budget -= totalCost;
    }
}
