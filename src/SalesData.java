/* SalesData의 전체적인 기능 디벨롭 및 초기 기획 의도에 맞게 역할 수행 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class SalesData {
    private class SaleRecord {
        Beverage beverage;
        int quantity;
        LocalDate date;

        public SaleRecord(Beverage beverage, int quantity, LocalDate date) {
            this.beverage = beverage;
            this.quantity = quantity;
            this.date = date;
        }
    }

    private List<SaleRecord> soldBeverages = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Integer> initialInventory = new HashMap<>(); // 초기 재고 기록용

    // 초기 재고 기록(Main에서 임의 수치로 테스트 필요)
    public void recordInitialInventory(Map<String, Integer> initInventory) {
        this.initialInventory.putAll(initInventory);
    }

    // 판매 기록
    public void recordSale(Beverage beverage, int quantity, int day) {
        synchronized (soldBeverages) {
            LocalDate date = LocalDate.of(2024, 12, day);
            soldBeverages.add(new SaleRecord(beverage, quantity, date));
        }
    }

    // 월말 보고서 생성
    public void generateReport(InventoryManager<Beverage> inventoryManager) {
        synchronized (soldBeverages) {
            // 주차별 판매량, 발주량, 빌린 양양 (4주)
            List<Map<String, Integer>> weeklySales = new ArrayList<>();
            List<Map<String, Integer>> weeklyPurchases = new ArrayList<>();
            List<Map<String, Integer>> weeklyReplenishData = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                weeklySales.add(new HashMap<>());
                weeklyPurchases.add(new HashMap<>());
                weeklyReplenishData.add(new HashMap<>());
            }

            Map<String, Integer> totalSalesCount = new HashMap<>();
            Map<String, Double> totalSalesAmount = new HashMap<>();
            double totalSales = 0.0;

            Map<String, Beverage> bevInfo = new HashMap<>(inventoryManager.getBeverageInfo());

            // 판매 데이터 집계
            for (SaleRecord record : soldBeverages) {
                int week = (record.date.getDayOfMonth() - 1) / 7; 
                Map<String, Integer> weekSales = weeklySales.get(week);

                String name = record.beverage.getName();
                weekSales.put(name, weekSales.getOrDefault(name, 0) + record.quantity);

                double sellingPrice = record.beverage.getPrice() * 1.2; 
                double saleRevenue = sellingPrice * record.quantity;
                totalSalesCount.put(name, totalSalesCount.getOrDefault(name, 0) + record.quantity);
                totalSalesAmount.put(name, totalSalesAmount.getOrDefault(name, 0.0) + saleRevenue);
                totalSales += saleRevenue;
            }

            // 테스트 필요 돌려보고 예상과 다르면 삭제 예정
            // 월간 구매/보충 데이터 (현재 날짜 기반 관리 X, 단순 1주차에 몰아서 출력 예시)
            Map<String, Integer> monthlyPurchases = inventoryManager.getMonthlyPurchases();
            Map<String, Integer> monthlyReplenish = inventoryManager.getMonthlyReplenish();

            // 모든 구매/보충을 1주차에 몰아넣는 예시 (실제 구현시 날짜별 기록 필요)
            weeklyPurchases.get(0).putAll(monthlyPurchases);
            weeklyReplenishData.get(0).putAll(monthlyReplenish);


            // 최다 판매 & 최저 판매 상품
            String mostSoldItem = null;
            String leastSoldItem = null;
            int maxCount = Integer.MIN_VALUE;
            int minCount = Integer.MAX_VALUE;

            for (Map.Entry<String, Integer> entry : totalSalesCount.entrySet()) {
                int count = entry.getValue();
                String name = entry.getKey();
                if (count > maxCount) {
                    maxCount = count;
                    mostSoldItem = name;
                }
                if (count < minCount) {
                    minCount = count;
                    leastSoldItem = name;
                }
            }

            // 굳이 달러라고 생각할 필요가 다시 하니 없어 보임임
            // // 총 매출 원단위 계산 (매출에 1000 곱한 값) // 굳이 원 단위로 수정할 필요는 없어 보임임
            // int totalSalesWon = (int) (totalSales * 1000);

            // 이윤 계산: 이윤 = (판매가 - 원가) * 판매수량
            // 원가: beverage.getPrice(), 판매가: beverage.getPrice()*1.2
            // 각 제품별 이윤 계산
            double totalProfit = 0.0;
            for (String product : totalSalesCount.keySet()) {
                Beverage bev = bevInfo.get(product);
                double costPrice = bev.getPrice();
                double sellingPrice = costPrice * 1.2;
                double profitPerUnit = (sellingPrice - costPrice);
                double pProfit = profitPerUnit * totalSalesCount.get(product);
                totalProfit += pProfit;
            }

            // 굳이 달러 단위로 고려할 필요가 없던 것 같아서 삭제제
            // int totalProfitWon = (int) (totalProfit * 1000);

            // 다음 달 권장 재고: 이번 달 판매량의 1.2배로 가정
            Map<String, Integer> recommendedStock = new HashMap<>();
            for (Map.Entry<String, Integer> entry : totalSalesCount.entrySet()) {
                int recommended = (int) Math.ceil(entry.getValue() * 1.2);
                recommendedStock.put(entry.getKey(), recommended);
            }

            // 재고 회전율 계산: 총 판매량 / (초기재고량 평균)
            // 평균 재고량은 초기 재고량을 기준으로 하지만, 여기서는 단순히 초기 재고를 사용
            Map<String, Double> turnoverRate = new HashMap<>();
            for (String product : totalSalesCount.keySet()) {
                int sold = totalSalesCount.get(product);
                int initInv = initialInventory.getOrDefault(product, 1);
                // 월말까지 초기 재고만 있었던 것처럼 가정 => 편의를 위해 설정, 수정 혹은 변경 가능함함
                double rate = (double) sold / initInv;
                turnoverRate.put(product, rate);
            }

            // 빌려온 품목 분석
            // 가정: 10개 단위로 빌렸다고 가정해 횟수 계산('InventoryManger'에서 상정한 개수에 따름)
            Map<String, Integer> borrowCount = new HashMap<>();
            for (Map.Entry<String, Integer> entry : monthlyReplenish.entrySet()) {
                int totalBorrowed = entry.getValue();
                int count = totalBorrowed / 10; 
                borrowCount.put(entry.getKey(), count);
            }

            // 보고서 작성 내용
            StringBuilder report = new StringBuilder();
            report.append("=== 월말 매출 보고서 ===\n\n");

            // 주차별 판매, 구매, 빌린 양양
            for (int i = 0; i < 4; i++) {
                report.append("<<").append(i + 1).append("주차 제품 별 판매량 및 재고 구매량>>\n");
                // 판매
                report.append("(판매)\n");
                Map<String, Integer> wSales = weeklySales.get(i);
                if (wSales.isEmpty()) {
                    report.append("(판매 없음)\n");
                } else {
                    for (Map.Entry<String, Integer> e : wSales.entrySet()) {
                        report.append(e.getKey()).append(": ").append(e.getValue()).append("개 판매\n");
                    }
                }

                // 구매
                report.append("(구매)\n");
                Map<String, Integer> wPurch = weeklyPurchases.get(i);
                if (wPurch.isEmpty()) {
                    report.append("(구매 없음)\n");
                } else {
                    for (Map.Entry<String, Integer> e : wPurch.entrySet()) {
                        report.append(e.getKey()).append(": ").append(e.getValue()).append("개 구매\n");
                    }
                }

                // 빌려온 제품의 양
                report.append("(빌려온 제품의 양)\n");
                Map<String, Integer> wRep = weeklyReplenishData.get(i);
                if (wRep.isEmpty()) {
                    report.append("(빌린 제품 없음)\n");
                } else {
                    for (Map.Entry<String, Integer> e : wRep.entrySet()) {
                        report.append(e.getKey()).append(": ").append(e.getValue()).append("개\n");
                    }
                }

                report.append("\n");
            }

            // 월간 총 판매/구매량
            report.append("<<제품 별 월 판매량 및 재고 구매량>>\n");
            report.append("(판매)\n");
            for (Map.Entry<String, Integer> entry : totalSalesCount.entrySet()) {
                report.append(entry.getKey()).append(": ").append(entry.getValue()).append("개 판매\n");
            }

            report.append("(구매)\n");
            for (Map.Entry<String, Integer> entry : monthlyPurchases.entrySet()) {
                report.append(entry.getKey()).append(": ").append(entry.getValue()).append("개 구매\n");
            }

            report.append("\n>> 빌려온 품목\n");
            for (Map.Entry<String, Integer> entry : monthlyReplenish.entrySet()) {
                String product = entry.getKey();
                int totalBorrowed = entry.getValue();
                int count = borrowCount.getOrDefault(product, 1);
                report.append("'").append(product).append("'의 재고량이 ").append(count)
                      .append("번 모자랐습니다. 타 점포에 총 ")
                      .append(totalBorrowed).append("개를 갚아야 합니다.\n");
            }

            // 총 매출/이윤 출력 (정수로 반올림)
            report.append("\n총 매출: ").append(String.format("%.0f", totalSales)).append("$\n");
            report.append("총 이윤: ").append(String.format("%.0f", totalProfit)).append("$\n\n");

            // 재고 회전율 및 재고 관리 개선안
            report.append(">> 재고 회전율:\n");
            for (Map.Entry<String, Double> entry : turnoverRate.entrySet()) {
                report.append(entry.getKey()).append(": ")
                      .append(String.format("%.2f 회/월\n", entry.getValue()));
            }
            report.append("\n");

            report.append(">> 재고 조절 권고 사항:\n");
            if (mostSoldItem != null) {
                report.append("다음 달에는 '").append(mostSoldItem).append("'의 재고를 늘릴 것을 추천합니다.\n");
                report.append("권장 초기 재고: ").append(recommendedStock.get(mostSoldItem)).append("개\n");
            }
            if (leastSoldItem != null) {
                report.append("다음 달에는 '").append(leastSoldItem).append("'의 재고를 줄일 것을 추천합니다.\n");
                report.append("권장 초기 재고: ").append(recommendedStock.get(leastSoldItem)).append("개\n");
            }

            // 파일로 저장
            saveReportToFile(report.toString());
        }
    }

    private void saveReportToFile(String reportContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("monthly_sales_report.txt"))) {
            writer.write(reportContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}