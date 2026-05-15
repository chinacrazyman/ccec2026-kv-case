package cn.edu.massz.ccec2026.module3_concurrency;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 面向高并发 KV 系统的数据结构工程化教学案例：
 * 突破并发边界 —— HashMap、Hashtable 与 ConcurrentHashMap 的并发写入对比
 *
 * 教学目标：
 * 1. 通过实验复现普通 HashMap 在多线程写入下的数据丢失问题；
 * 2. 对比 Hashtable 与 ConcurrentHashMap 在数据完整性方面的表现；
 * 3. 引导学生理解“线程安全、同步粒度、并发扩展性”之间的工程权衡；
 * 4. 避免简单化结论：ConcurrentHashMap 并不意味着在所有短时纯写入测试中都绝对更快，
 *    其核心价值在于更细粒度的同步机制与更好的并发扩展能力。
 */
public class ConcurrencyCrashTest {

    /**
     * 模拟并发线程数。
     * 可以根据课堂电脑配置适当调整：
     * - 课堂稳定演示：100
     * - 国赛材料截图：200
     */
    private static final int THREAD_COUNT = 200;

    /**
     * 每个线程写入的 KV 数量。
     * 当前配置下理论总写入量为 200 × 5000 = 1,000,000 条。
     */
    private static final int INSERT_PER_THREAD = 5000;

    /**
     * 理论预期总数据量。
     */
    private static final int EXPECTED_TOTAL = THREAD_COUNT * INSERT_PER_THREAD;

    /**
     * 设置较大的初始容量，尽量减少扩容对实验结果的干扰。
     */
    private static final int INITIAL_CAPACITY = 1_500_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== 面向高并发 KV 系统的数据结构工程化教学案例：突破并发边界实战演示 ==========");
        System.out.println("实验类型：多线程并发写入同一个 Map 容器");
        System.out.println("并发线程数：" + THREAD_COUNT);
        System.out.println("每线程写入数据量：" + INSERT_PER_THREAD);
        System.out.println("理论预期总数据量：" + EXPECTED_TOTAL + " 条\n");

        TestResult hashMapResult = testMap(
                new HashMap<>(INITIAL_CAPACITY),
                "1. 普通 HashMap（非线程安全）",
                "普通 HashMap 没有同步保护。多个线程同时修改内部数组、链表或树结构时，可能出现数据覆盖、丢失甚至结构异常。"
        );

        TestResult hashtableResult = testMap(
                new Hashtable<>(INITIAL_CAPACITY),
                "2. Hashtable（方法级 synchronized，全局锁）",
                "Hashtable 通过方法级 synchronized 保证线程安全。本次实验中数据完整，但所有写入操作竞争同一把全局锁，并发扩展能力受限。"
        );

        TestResult concurrentHashMapResult = testMap(
                new ConcurrentHashMap<>(INITIAL_CAPACITY),
                "3. ConcurrentHashMap（CAS + 桶级同步）",
                "ConcurrentHashMap 通过 CAS 与桶级同步机制保证线程安全。它不保证在每一次短时纯写入测试中都绝对最快，但在高并发读写混合场景下通常具有更好的扩展能力。"
        );

        printSummary(hashMapResult, hashtableResult, concurrentHashMapResult);
    }

    /**
     * 压测核心方法：
     * 使用 CountDownLatch 作为“发令枪”，让多个线程尽可能同时开始写入同一个 Map。
     */
    private static TestResult testMap(Map<String, String> map,
                                      String testName,
                                      String analysis) throws InterruptedException {

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;

            Thread worker = new Thread(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < INSERT_PER_THREAD; j++) {
                        /*
                         * 构造全局唯一 Key：
                         * Thread-0-Order-0 与 Thread-1-Order-0 不会重复，
                         * 因此如果最终 size 小于理论值，不能归因于业务 Key 重复，
                         * 而应归因于并发写入过程中的线程安全问题。
                         */
                        String key = "Thread-" + threadId + "-Order-" + j;
                        map.put(key, "Success");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });

            worker.setName("kv-writer-" + i);
            worker.start();
        }

        long startTime = System.nanoTime();
        startLatch.countDown();
        endLatch.await();
        long endTime = System.nanoTime();

        int actualSize = map.size();
        int lostCount = EXPECTED_TOTAL - actualSize;
        double lostRate = lostCount * 100.0 / EXPECTED_TOTAL;
        double elapsedMs = (endTime - startTime) / 1_000_000.0;

        TestResult result = new TestResult(testName, actualSize, lostCount, lostRate, elapsedMs);

        System.out.println("【" + testName + "】测试完成：");
        System.out.printf(" -> 实际存入数据量：%,d 条%n", actualSize);
        System.out.printf(" -> 数据丢失数量：%,d 条%n", lostCount);
        System.out.printf(" -> 数据丢失率：%.4f%%%n", lostRate);
        System.out.printf(" -> 压测总耗时：%.3f ms%n", elapsedMs);
        System.out.println("【结论分析】" + analysis + "\n");

        return result;
    }

    /**
     * 汇总输出：
     * 这里故意不写“ConcurrentHashMap 一定比 Hashtable 快”，
     * 而是强调线程安全与同步粒度的工程差异。
     */
    private static void printSummary(TestResult... results) {
        System.out.println("========== 实验汇总 ==========");
        System.out.printf("%-42s %15s %15s %15s %15s%n",
                "容器类型", "实际数量", "丢失数量", "丢失率", "耗时(ms)");

        for (TestResult result : results) {
            System.out.printf("%-42s %,15d %,15d %14.4f%% %15.3f%n",
                    result.name,
                    result.actualSize,
                    result.lostCount,
                    result.lostRate,
                    result.elapsedMs);
        }

        System.out.println("\n========== 教学结论 ==========");
        System.out.println("1. 普通 HashMap 不适合多线程共享写入场景，本实验能够稳定暴露数据丢失问题。");
        System.out.println("2. Hashtable 和 ConcurrentHashMap 都能保证本实验中的数据完整性，但二者同步策略不同。");
        System.out.println("3. Hashtable 采用方法级 synchronized，设计简单，但所有线程竞争同一把全局锁。");
        System.out.println("4. ConcurrentHashMap 采用 CAS 与桶级同步等机制，设计更复杂，适合更高并发度和读写混合的工程场景。");
        System.out.println("5. 本实验的重点不是证明某个容器在任意场景下绝对最快，而是引导学生理解：线程安全、锁粒度、实现复杂度与并发扩展性之间存在工程权衡。");
    }

    /**
     * 实验结果对象。
     */
    private static class TestResult {
        private final String name;
        private final int actualSize;
        private final int lostCount;
        private final double lostRate;
        private final double elapsedMs;

        private TestResult(String name,
                           int actualSize,
                           int lostCount,
                           double lostRate,
                           double elapsedMs) {
            this.name = name;
            this.actualSize = actualSize;
            this.lostCount = lostCount;
            this.lostRate = lostRate;
            this.elapsedMs = elapsedMs;
        }
    }
}