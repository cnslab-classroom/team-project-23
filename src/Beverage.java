// Beverage.java
public abstract class Beverage {
    private String name;
    private double price;

    public Beverage(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayInfo();
}

// Coffee.java moved to its own file
// EnergyDrink.java moved to its own file
// Smoothie.java moved to its own file