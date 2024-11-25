
// Abstract: 판매된 음료 정보를 기록하고 매출 보고서를 생성하는 클래스
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class SalesData {
    // 판매된 음료 정보를 담는 내부 클래스
    private class SaleRecord {
        Beverage beverage;
        LocalDate date;

        public SaleRecord(Beverage beverage, LocalDate date) {
            this.beverage = beverage;
            this.date = date;
        }
    }

    private List<SaleRecord> soldBeverages = Collections.synchronizedList(new ArrayList<>());
    private ReentrantLock lock = new ReentrantLock();

    // 판매된 음료를 기록 (판매 날짜 포함)
    public void recordSale(Beverage beverage, LocalDate date) {
        lock.lock();
        try {
            soldBeverages.add(new SaleRecord(beverage, date));
        } finally {
            lock.unlock();
        }
    }

    // 매출 보고서 생성
    public void generateReport() {
        lock.lock();
        try {
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
                weekSales.put(name, weekSales.getOrDefault(name, 0) + 1);

                totalSalesCount.put(name, totalSalesCount.getOrDefault(name, 0) + 1);
                totalSalesAmount.put(name, totalSalesAmount.getOrDefault(name, 0.0) + record.beverage.price);
                totalSales += record.beverage.price;
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
            report.append("\n총 매출: ").append((int) totalSales).append("원\n\n");

            report.append(">> 재고 조절 권고 사항:\n");
            report.append("다음 달에는 '").append(mostSoldItem).append("'의 재고를 늘릴 것을 추천합니다.\n");
            report.append("다음 달에는 '").append(leastSoldItem).append("'의 재고를 줄일 것을 추천합니다.\n");

            // 콘솔 출력
            System.out.println(report.toString());

            // 파일 저장
            saveReportToFile(report.toString());
        } finally {
            lock.unlock();
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

/* 예상 출력 결과물 */
// === 월말 매출 보고서 ===

// >> 1주차 판매량:
// 아메리카노: 15개 판매
// 레드불: 10개 판매
// 딸기 스무디: 5개 판매

// >> 2주차 판매량:
// 아메리카노: 20개 판매
// 레드불: 8개 판매
// 딸기 스무디: 7개 판매

// >> 3주차 판매량:
// 아메리카노: 18개 판매
// 레드불: 12개 판매
// 딸기 스무디: 6개 판매

// >> 4주차 판매량:
// 아메리카노: 22개 판매
// 레드불: 15개 판매
// 딸기 스무디: 9개 판매

// >> 전체 판매량:
// 아메리카노: 75개 판매
// 레드불: 45개 판매
// 딸기 스무디: 27개 판매

// 총 매출: 302500원

// >> 재고 조절 권고 사항:
// 다음 달에는 '아메리카노'의 재고를 늘릴 것을 추천합니다.
// 다음 달에는 '딸기 스무디'의 재고를 줄일 것을 추천합니다.

// => 주간 데이터를 토대로 월간 매출 보고서를 작성하는 기능을 수행
// - 주간/월간 데이터 확인 가능
// => 데이터 분석의 기능을 포함하기 위해 재고 조절 권고 기능을 추가함