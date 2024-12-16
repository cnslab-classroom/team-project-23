import java.util.*;

public class Beverage {
    private String category;
    private String name;
    private int price;
    private int stock;

    public Beverage(String category, String name, int price, int stock) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void purchase(int quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException("Not enough stock available.");
        }
        stock -= quantity;
    }

    public void restock(int quantity) {
        stock += quantity;
    }

    public static List<Beverage> initializeBeverages() {
        List<Beverage> beverages = new ArrayList<>();

        beverages.add(new Beverage("coffee", "americano", 2000, 10));
        beverages.add(new Beverage("coffee", "espresso", 1500, 10));
        beverages.add(new Beverage("coffee", "latte", 3200, 10));

        beverages.add(new Beverage("energy drink", "blue", 2300, 10));
        beverages.add(new Beverage("energy drink", "white", 2200, 10));
        beverages.add(new Beverage("energy drink", "pink", 2000, 10));

        beverages.add(new Beverage("smoothie", "blueberry smoothie", 3500, 10));
        beverages.add(new Beverage("smoothie", "strawberry smoothie", 4000, 10));
        beverages.add(new Beverage("smoothie", "plain smoothie", 3000, 10));

        beverages.add(new Beverage("ade", "mango", 2500, 10));
        beverages.add(new Beverage("ade", "apple", 3000, 10));
        beverages.add(new Beverage("ade", "pineapple", 2800, 10));

        return beverages;
    }
}
