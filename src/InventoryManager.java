/* 수정 사항 */
// 1. beverageInfo 맵 추가. -> 판매가 계산을 위해 필요.
// 2. addBeverage 메서드 오버로딩: 음료 객체를 받는 메서드 추가.
// 3. reduceInventory에서 판매가 계산(20% 마진) 후 capital 증가 반영.
// -> 마진율은 임의로 설정하였으며, 보다 현실적인 시뮬레이션을 위함.
// -> 고객이 물품 구매 후 capital에 이윤이 더해지지 않는 문제를 수정.
import java.util.HashMap;
import java.util.Map;

public class InventoryManager<T extends Beverage> {
    private Map<String, Integer> inventory = new HashMap<>();
    private double capital;
    private Map<String, Integer> weeklySales = new HashMap<>();
    private Map<String, Integer> monthlySales = new HashMap<>();
    // 추가: 구매(발주) 기록 (주간/월간)
    private Map<String, Integer> weeklyPurchases = new HashMap<>();
    private Map<String, Integer> monthlyPurchases = new HashMap<>();
    private Map<String, Integer> weeklyReplenish = new HashMap<>();
    private Map<String, Integer> monthlyReplenish = new HashMap<>();

    // 추가: 음료 정보 저장용 맵
    private Map<String, T> beverageInfo = new HashMap<>();

    public InventoryManager(double initialCapital) {
        this.capital = initialCapital;
    }

    // 기존(수정-0이하에서 메세지 출력하지 않게, 없는 재고 표기 필요X): Add beverages to inventory
    public void addBeverage(String name, int quantity) {
        if (quantity > 0) {
            inventory.put(name, inventory.getOrDefault(name, 0) + quantity);
            System.out.println("Added " + quantity + " units of " + name + " to inventory.");
        } else {
            // 0 이하의 경우 메시지 생략, inventory에 키는 저장
            inventory.putIfAbsent(name, 0);
        }
    }

    // 오버로딩: 음료 객체 자체를 등록하며 재고 추가
    public void addBeverage(T beverage, int quantity) {
        beverageInfo.put(beverage.getName(), beverage);
        if (quantity > 0) {
            inventory.put(beverage.getName(), inventory.getOrDefault(beverage.getName(), 0) + quantity);
            System.out.println("Added " + quantity + " units of " + beverage.getName() + " to inventory.");
        } else {
            inventory.putIfAbsent(beverage.getName(), 0);
        }
    }

    // Check if sufficient stock is available
    public boolean checkInventory(String name, int quantity) {
        return inventory.getOrDefault(name, 0) >= quantity;
    }

    // Reduce inventory and record sales
    public void reduceInventory(String name, int quantity) {
        if (checkInventory(name, quantity)) {
            inventory.put(name, inventory.get(name) - quantity);
            weeklySales.put(name, weeklySales.getOrDefault(name, 0) + quantity);
            monthlySales.put(name, monthlySales.getOrDefault(name, 0) + quantity);

            // 판매가 계산: 기본가격의 120%
            // beverageInfo에 해당 음료 존재한다고 가정
            if (!beverageInfo.containsKey(name)) {
                throw new IllegalArgumentException("Beverage info not found for " + name);
            }
            T bev = beverageInfo.get(name);
            double sellingPrice = bev.getPrice() * 1.2; 
            capital += sellingPrice * quantity; // Capital에 실시간 판매금액 반영

            System.out.println("Reduced " + quantity + " units of " + name + " from inventory.");
            // 추가: 연관된 내용 함께 출력되도록 묶어서 배치 
            System.out.println("A customer bought " + quantity + " units of " + name + ".");
            System.out.println("Current Capital: " + capital);
        } else {
            throw new IllegalArgumentException("Insufficient stock.");
        }
    }


    // 추가: 구매(발주) 기록
    private void recordPurchase(String name, int quantity) {
        weeklyPurchases.put(name, weeklyPurchases.getOrDefault(name, 0) + quantity);
        monthlyPurchases.put(name, monthlyPurchases.getOrDefault(name, 0) + quantity);
    }

    // 수정(발주 기록 기능 추가): Restock inventory with capital check
    public void restockBeverage(T beverage, int quantity, double costPerUnit) {
        double cost = quantity * costPerUnit;
        if (capital >= cost) {
            addBeverage(beverage, quantity);
            capital -= cost;
            recordPurchase(beverage.getName(), quantity);
            System.out.println("Restocked " + quantity + " units of " + beverage.getName() + ".");
            System.out.println("Current Capital: " + capital);
        } else {
            throw new IllegalArgumentException("Insufficient capital to restock.");
        }
    }

    // ** 수정: addBeverage()를 통해 재고 추가하면, 보충과 발주의 개념이 명확히 구분되지 않을 수 있음
    //          => 보충 전영 로직으로 처리하는 것이 더 타당한 것 같음, 기록 로직도 별도 관리 용이해짐
    // Replenish inventory (e.g., borrowed from other stores)
    public void replenishInventory(String name, int quantity) {
        //addBeverage(name, quantity); 아래 코드로 대체
        inventory.put(name, inventory.getOrDefault(name, 0) + quantity);
        //추가 부분, 오전에 발주로 채운게 아닌 thread가 채우는 replenish를 따로 기록
        recordReplenish(name, quantity);
        // 추가: 중복 메시지 나오지 않도록 여기에서만 출력
        System.out.println("Replenished " + quantity + " units of " + name); 
    }

    // Record replenish data
    //추가 부분
    public void recordReplenish(String name, int quantity) {
        weeklyReplenish.put(name, weeklyReplenish.getOrDefault(name, 0) + quantity);
        monthlyReplenish.put(name, monthlyReplenish.getOrDefault(name, 0) + quantity);
    }

    // Display current inventory
    public void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }
    }

    // Get inventory stock
    public Map<String, Integer> getStock() {
        return inventory;
    }

    // 주간/월간 판매량, 보충 데이터 접근용 메서드
    public Map<String, Integer> getMonthlySales() {
        return monthlySales;
    }

    public Map<String, Integer> getMonthlyReplenish() {
        return monthlyReplenish;
    }

    // 추가: 발주 기록용 'SalesData'에 필요
    public Map<String, Integer> getMonthlyPurchases() {
        return monthlyPurchases;
    }

    public Map<String, Integer> getWeeklySales() {
        return weeklySales;
    }

    public Map<String, Integer> getWeeklyPurchases() {
        return weeklyPurchases;
    }

    public Map<String, Integer> getWeeklyReplenish() {
        return weeklyReplenish;
    }

    // 코드 흐름을 고려하여 위치 변경
    // Get available capital
    public double getCapital() {
        return capital;
    }

    public Map<String, T> getBeverageInfo() {
        return beverageInfo;
    }

    // SalesData 파트에서 기능할 역할이라 삭제
    // // Print weekly sales report
    // public void printWeeklySales() {
    //     System.out.println("Weekly Sales Report:");
    //     for (Map.Entry<String, Integer> entry : weeklySales.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue() + " units sold");
    //     }
    // }

    // Reset weekly sales
    public void resetWeeklyData() {
        weeklySales.clear();
        weeklyPurchases.clear();
        weeklyReplenish.clear();
    }

    // SalesData 파트에서 기능할 역할이라 삭제
    // // Print monthly sales report
    // public void printMonthlySales() {
    //     System.out.println("Monthly Sales Report:");
    //     for (Map.Entry<String, Integer> entry : monthlySales.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue() + " units sold");
    //     }
    // }

    // SalesData 파트에서 기능할 역할이라 삭제 // 빌리는 개념 SalesData에서 활용
    // Print replenish report
    //추가 부분, 매장 운영 도중에 재고가 부족한 부분은 다른 매장에서 재고를
    // 빌려왔다는 설정(발주에 신경을 쓰라는 경고와 분석을 표현하고 싶었음)
    // public void printReplenishReport(boolean isWeekly) {
    //     Map<String, Integer> replenishData = isWeekly ? weeklyReplenish : monthlyReplenish;
    //     String period = isWeekly ? "이번주" : "이번달";
    //     for (Map.Entry<String, Integer> entry : replenishData.entrySet()) {
    //         System.out.println(period + "은 " + entry.getValue() + "개의 " + entry.getKey() + "를 다른 점포에서 빌려왔습니다. 재고 관리에 신경써 주세요.");
    //     }
    //     if (isWeekly) {
    //         weeklyReplenish.clear(); // Clear weekly data after the report
    //     }
    // }


    // 판매가를 기준으로 더하는 것으로 수정하여 삭제
    // // Add capital to inventory manager
    // public void addCapital(double amount) {
    //     capital += amount;
    // }
}
