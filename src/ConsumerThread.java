import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerThread extends Thread {
    private Customer customer;
    private InventoryManager inventoryManager;
    private List<Beverage> availableBeverages;
    private Lock lock;

    public ConsumerThread(Customer customer, InventoryManager inventoryManager, List<Beverage> availableBeverages) {
        this.customer = customer;
        this.inventoryManager = inventoryManager;
        this.availableBeverages = availableBeverages;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        try {
            lock.lock();
            Beverage preferredDrink = customer.getPreferredDrink();

            // 시도: 선호 음료 구매
            try {
                System.out.println("Customer " + customer.getId() + " is trying to buy " + preferredDrink.getName());
                customer.buy(preferredDrink, 1, inventoryManager);
                System.out.println("Customer " + customer.getId() + " successfully bought " + preferredDrink.getName());
            } catch (IllegalArgumentException e) {
                // 실패 시: 대체 음료 추천
                System.out.println("Customer " + customer.getId() + " couldn't buy " + preferredDrink.getName() + ": " + e.getMessage());
                recommendAlternative(preferredDrink);
            }
        } finally {
            lock.unlock();
        }
    }

    // 대체 음료 추천 로직
    private void recommendAlternative(Beverage preferredDrink) {
        for (Beverage beverage : availableBeverages) {
            if (!beverage.getName().equals(preferredDrink.getName()) && inventoryManager.getInventory().getStock().getOrDefault(beverage, 0) > 0) {
                System.out.println("Customer " + customer.getId() + ", alternative suggestion: " + beverage.getName());
                try {
                    customer.buy(beverage, 1, inventoryManager);
                    System.out.println("Customer " + customer.getId() + " successfully bought alternative: " + beverage.getName());
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Alternative " + beverage.getName() + " couldn't be bought: " + e.getMessage());
                }
            }
        }
        System.out.println("Customer " + customer.getId() + ", no alternatives available.");
    }
}
