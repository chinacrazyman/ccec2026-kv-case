package cn.edu.massz.ccec2026.module4_bloom;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.text.NumberFormat;

public class BloomFilterLimitDemo {

    private static final int TOTAL_CAPACITY = 10_000_000;
    private static final double FPP = 0.0001;

    public static void main(String[] args) {

        System.out.println("========== Bloom Filter 海量数据判重实验 ==========\n");
        System.out.println("实验目标：观察布隆过滤器在空间占用与误判率之间的工程权衡\n");

        printTheoreticalMemoryCost(TOTAL_CAPACITY, FPP);

        long startTime = System.currentTimeMillis();

        BloomFilter<Integer> bloomFilter = BloomFilter.create(
                Funnels.integerFunnel(),
                TOTAL_CAPACITY,
                FPP
        );

        System.out.println("\n【阶段一】初始化布隆过滤器并写入合法 ID");
        System.out.println(" -> 正在写入 1000 万个合法商品 ID...");

        for (int i = 0; i < TOTAL_CAPACITY; i++) {
            bloomFilter.put(i);
        }

        System.out.println(" -> 初始化完成，总耗时：" + (System.currentTimeMillis() - startTime) + " ms");

        System.out.println("\n【阶段二】验证合法 ID 不会被误判为不存在");

        int legalId = 8_888_888;

        System.out.println(" -> 查询合法 ID [" + legalId + "]：" + bloomFilter.mightContain(legalId));

        System.out.println("\n【阶段三】模拟非法 ID 请求并统计误判率");

        int attackCount = 1_000_000;
        int falsePositiveCount = 0;

        for (int i = TOTAL_CAPACITY; i < TOTAL_CAPACITY + attackCount; i++) {
            if (bloomFilter.mightContain(i)) {
                falsePositiveCount++;
            }
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(6);

        double actualFpp = (double) falsePositiveCount / attackCount;

        System.out.println(" -> 非法请求总数：" + attackCount);
        System.out.println(" -> 被拦截次数：" + (attackCount - falsePositiveCount));
        System.out.println(" -> 误判放行次数：" + falsePositiveCount);
        System.out.println(" -> 实际误判率：" + percentFormat.format(actualFpp));
        System.out.println(" -> 设计误判率：" + percentFormat.format(FPP));
        System.out.println(" -> Guava 当前估算误判率：" + percentFormat.format(bloomFilter.expectedFpp()));

        System.out.println("\n【教学结论】");
        System.out.println("1. 布隆过滤器不会把已加入的数据误判为不存在。");
        System.out.println("2. 但可能把未加入的数据误判为“可能存在”。");
        System.out.println("3. 它通过极小概率误判，换取极大的空间节省。");
        System.out.println("4. 在缓存穿透防护等场景中，属于典型的工程 Trade-off 设计。");
    }

    private static void printTheoreticalMemoryCost(long n, double p) {

        double bits = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        double mb = bits / 8.0 / 1024 / 1024;
        double optimalHashFunctions = bits / n * Math.log(2);

        System.out.println("【理论容量规划】");
        System.out.println("预计插入量 n = " + n);
        System.out.println("目标误判率 p = " + p);
        System.out.printf("理论所需位数 m ≈ %.0f bits%n", bits);
        System.out.printf("理论所需内存 ≈ %.2f MB%n", mb);
        System.out.printf("理论最优哈希函数个数 k ≈ %.2f%n", optimalHashFunctions);
    }
}
