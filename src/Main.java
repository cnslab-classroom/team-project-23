import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InventoryManager<Beverage> inventoryManager = new InventoryManager<>(1000.0); // Initial capital
        SalesData salesData = new SalesData();  // SalesData 객체 생성
        Scanner scanner = new Scanner(System.in);

        // Initial stock
        inventoryManager.addBeverage("Latte", 10);
        inventoryManager.addBeverage("Red Bull", 5);
        inventoryManager.addBeverage("Berry Blast", 8);

        // Threads for each beverage
        InventoryManagerThread<Beverage> coffeeThread = new InventoryManagerThread<>(inventoryManager, "Latte", 10);
        InventoryManagerThread<Beverage> redBullThread = new InventoryManagerThread<>(inventoryManager, "Red Bull", 10);
        InventoryManagerThread<Beverage> smoothieThread = new InventoryManagerThread<>(inventoryManager, "Berry Blast", 10);

        // Start threads
        coffeeThread.start();
        redBullThread.start();
        smoothieThread.start();

        // Beverages list
        List<Beverage> beverages = new ArrayList<>();
        beverages.add(new Beverage.Coffee("Latte", 3.5));
        beverages.add(new Beverage.EnergyDrink("Red Bull", 2.5));
        beverages.add(new Beverage.Smoothie("Berry Blast", 4.0));

        // Simulation loop (1 to 30 days)
        for (int day = 1; day <= 30; day++) {
            System.out.println("Day " + day + " Simulation:");

            // Morning (AM): Restock beverages
            System.out.println("-- Morning: Restock Beverages --");
            while (true) {
                inventoryManager.displayInventory();
                System.out.println("Current Capital: " + inventoryManager.getCapital());
                System.out.println("\nSelect an option:");
                System.out.println("1. Restock Latte");
                System.out.println("2. Restock Red Bull");
                System.out.println("3. Restock Berry Blast");
                System.out.println("4. Finish Morning Restock");
                System.out.print("Enter your choice: ");

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    continue;
                }

                if (choice == 4) {
                    System.out.println("Morning restock finished.\n");
                    break;
                }

                if (choice < 1 || choice > 4) {
                    System.out.println("Invalid choice. Please select a valid option.");
                    continue;
                }

                System.out.print("Enter quantity to restock: ");
                int quantity;
                try {
                    quantity = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a valid number.");
                    continue;
                }

                Beverage selectedBeverage = null;
                switch (choice) {
                    case 1 -> selectedBeverage = beverages.get(0); // Latte
                    case 2 -> selectedBeverage = beverages.get(1); // Red Bull
                    case 3 -> selectedBeverage = beverages.get(2); // Berry Blast
                }

                try {
                    inventoryManager.restockBeverage(selectedBeverage, quantity, selectedBeverage.getPrice());
                    System.out.println("Restocked " + quantity + " units of " + selectedBeverage.getName() + ".");
                } catch (IllegalArgumentException e) {
                    System.out.println("Cannot restock " + selectedBeverage.getName() + ": " + e.getMessage());
                }
            }

            // Afternoon (PM): Customer Turn
            System.out.println("-- Afternoon: Customer Turn --");
            while (true) {
                System.out.println("Current Inventory:");
                inventoryManager.displayInventory();
                System.out.println("\nSelect an option:");
                System.out.println("1. Customer buys Latte");
                System.out.println("2. Customer buys Red Bull");
                System.out.println("3. Customer buys Berry Blast");
                System.out.println("4. Finish Customer Turn");
                System.out.print("Enter your choice: ");

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    continue;
                }

                if (choice == 4) {
                    System.out.println("Customer turn finished.\n");
                    break;
                }

                if (choice < 1 || choice > 4) {
                    System.out.println("Invalid choice. Please select a valid option.");
                    continue;
                }

                System.out.print("Enter quantity to buy: ");
                int quantity;
                try {
                    quantity = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a valid number.");
                    continue;
                }

                Beverage selectedBeverage = null;
                switch (choice) {
                    case 1 -> selectedBeverage = beverages.get(0); // Latte
                    case 2 -> selectedBeverage = beverages.get(1); // Red Bull
                    case 3 -> selectedBeverage = beverages.get(2); // Berry Blast
                }

                try {
                    inventoryManager.reduceInventory(selectedBeverage.getName(), quantity);
                    System.out.println("A customer bought " + quantity + " units of " + selectedBeverage.getName() + ".");
                    // 판매가 이루어질 때마다 SalesData에 기록
                    salesData.recordSale(selectedBeverage, quantity, day); // 날짜를 1~30으로 전달
                } catch (IllegalArgumentException e) {
                    System.out.println("Cannot sell " + selectedBeverage.getName() + ": " + e.getMessage());
                }
            }

            // Generate weekly report
            if (day % 7 == 0) {
                System.out.println("\nWeekly Sales Report:");
                inventoryManager.printWeeklySales();       // 주간 판매량 출력
                inventoryManager.printReplenishReport(true); // 주간 보충 보고
                inventoryManager.resetWeeklySales();       // 주간 판매량 초기화
            }
        }

        // 월말 매출 보고서를 생성하고 출력하기
        salesData.generateReport();  // SalesData에서 월말 매출 보고서를 생성하고 출력

        // Total Capital
        System.out.println("Total Capital: " + inventoryManager.getCapital());

        // Stop all threads
        coffeeThread.interrupt();
        redBullThread.interrupt();
        smoothieThread.interrupt();

        scanner.close();
    }
}
