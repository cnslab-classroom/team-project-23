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

    public static class Coffee extends Beverage {
        public Coffee(String name, double price) {
            super(name, price);
        }

        @Override
        public void displayInfo() {
            System.out.println("Coffee: " + getName() + ", Price: " + getPrice());
        }
    }

    public static class EnergyDrink extends Beverage {
        public EnergyDrink(String name, double price) {
            super(name, price);
        }

        @Override
        public void displayInfo() {
            System.out.println("Energy Drink: " + getName() + ", Price: " + getPrice());
        }
    }

    public static class Smoothie extends Beverage {
        public Smoothie(String name, double price) {
            super(name, price);
        }

        @Override
        public void displayInfo() {
            System.out.println("Smoothie: " + getName() + ", Price: " + getPrice());
        }
    }
}