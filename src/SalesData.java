// Abstract: 판매된 음료 정보를 기록하고 매출 보고서를 생성하는 클래스
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

    // 판매된 음료를 기록 (판매 날짜 및 수량 포함)
    public void recordSale(Beverage beverage, int quantity, int day) {
        synchronized (soldBeverages) {
            // 날짜가 int 형식으로 들어오므로 LocalDate로 변환
            LocalDate date = LocalDate.of(2024, 12, day); // 2024년 12월을 기준으로 사용
            soldBeverages.add(new SaleRecord(beverage, quantity, date));
        }
    }

    // 매출 보고서 생성
    public void generateReport() {
        synchronized (soldBeverages) {
            // 주차별 판매량을 저장하는 리스트 (4주차)
            List<Map<String, Integer>> weeklySales = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                weeklySales.add(new HashMap<>());
            }
    
            Map<String, Integer> totalSalesCount = new HashMap<>();
            Map<String, Double> totalSalesAmount = new HashMap<>();
            double totalSales = 0;
    
            // 판매 데이터 집계
            for (SaleRecord record : soldBeverages) {
                int week = (record.date.getDayOfMonth() - 1) / 7; // 주차 계산 (0~3)
                Map<String, Integer> weekSales = weeklySales.get(week);
    
                String name = record.beverage.getName();
                weekSales.put(name, weekSales.getOrDefault(name, 0) + record.quantity);
    
                totalSalesCount.put(name, totalSalesCount.getOrDefault(name, 0) + record.quantity);
                totalSalesAmount.put(name, totalSalesAmount.getOrDefault(name, 0.0) + record.beverage.getPrice() * record.quantity);
                totalSales += record.beverage.getPrice() * record.quantity;
            }
    
            // 가장 많이 팔린 상품과 적게 팔린 상품 찾기
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
    
            // 보고서 출력 및 저장
            StringBuilder report = new StringBuilder();
            report.append("=== 월말 매출 보고서 ===\n\n");
    
            for (int i = 0; i < 4; i++) {
                report.append(">> ").append(i + 1).append("주차 판매량:\n");
                Map<String, Integer> weekSales = weeklySales.get(i);
                for (Map.Entry<String, Integer> entry : weekSales.entrySet()) {
                    report.append(entry.getKey()).append(": ").append(entry.getValue()).append("개 판매\n");
                }
                report.append("\n");
            }
    
            report.append(">> 전체 판매량:\n");
            for (Map.Entry<String, Integer> entry : totalSalesCount.entrySet()) {
                report.append(entry.getKey()).append(": ").append(entry.getValue()).append("개 판매\n");
            }
    
            // 총 매출에 1000을 곱하여 출력
            report.append("\n총 매출: ").append((int) (totalSales * 1000)).append("원\n\n");  // 매출에 1000 곱하기
    
            report.append(">> 재고 조절 권고 사항:\n");
            if (mostSoldItem != null) {
                report.append("다음 달에는 '").append(mostSoldItem).append("'의 재고를 늘릴 것을 추천합니다.\n");
            }
            if (leastSoldItem != null) {
                report.append("다음 달에는 '").append(leastSoldItem).append("'의 재고를 줄일 것을 추천합니다.\n");
            }
    
            // 콘솔 출력
            System.out.println(report.toString());
    
            // 파일 저장
            saveReportToFile(report.toString());
        }
    }

    // 보고서를 파일로 저장
    private void saveReportToFile(String reportContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("monthly_sales_report.txt"))) {
            writer.write(reportContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
