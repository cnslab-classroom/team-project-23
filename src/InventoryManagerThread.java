public class InventoryManagerThread<T extends Beverage> extends Thread {
    private InventoryManager<T> inventoryManager;
    private String beverageName;
    private int replenishQuantity;
    private boolean replenishing = false; // 보충 상태를 기록하는 플래그

    // checkInterval을 삭제 항시 대기를 하다가 보충하는 로직으로 변경
    public InventoryManagerThread(InventoryManager<T> inventoryManager, String beverageName, int replenishQuantity) {
        this.inventoryManager = inventoryManager;
        this.beverageName = beverageName;
        this.replenishQuantity = replenishQuantity;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (inventoryManager) {
                int currentStock = inventoryManager.getStock().getOrDefault(beverageName, 0);

                if (currentStock == 0 && !replenishing) { // 보충 중이 아닐 때만 수행
                    replenishing = true; // 보충 시작
                    inventoryManager.replenishInventory(beverageName, replenishQuantity);
                    System.out.println("Replenished " + replenishQuantity + " units of " + beverageName);
                    replenishing = false; // 보충 완료
                }
            }

            try {
                Thread.sleep(1000); // 1초 간격으로 재고 확인
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}