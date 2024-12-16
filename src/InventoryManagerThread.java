public class InventoryManagerThread extends Thread {
    private InventoryManager inventoryManager;
    private String beverageName;
    private int replenishQuantity;
    private long checkInterval;

    public InventoryManagerThread(InventoryManager inventoryManager, String beverageName, int replenishQuantity, long checkInterval) {
        this.inventoryManager = inventoryManager;
        this.beverageName = beverageName;
        this.replenishQuantity = replenishQuantity;
        this.checkInterval = checkInterval;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(checkInterval); // Check inventory at specified intervals
                inventoryManager.replenishInventory(beverageName, replenishQuantity);
                System.out.println("Replenished " + replenishQuantity + " units of " + beverageName);
                inventoryManager.displayInventory();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}