import java.util.*;

public class Inventory {
    private int capital;
    private Map<Beverage, Integer> stock;

    public Inventory() {
        this.capital = 1000000; // 초기 자본금
        this.stock = new HashMap<>();
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public Map<Beverage, Integer> getStock() {
        return stock;
    }

    public void addStock(Beverage beverage, int quantity) {
        stock.put(beverage, stock.getOrDefault(beverage, 0) + quantity);
    }

    public void deductStock(Beverage beverage, int quantity) {
        if (!stock.containsKey(beverage) || stock.get(beverage) < quantity) {
            throw new IllegalArgumentException("Not enough stock available.");
        }
        stock.put(beverage, stock.get(beverage) - quantity);
    }

    public void adjustCapital(int amount) {
        this.capital += amount;
    }
}
