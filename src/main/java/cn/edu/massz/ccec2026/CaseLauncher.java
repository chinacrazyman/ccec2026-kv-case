package cn.edu.massz.ccec2026;

import cn.edu.massz.ccec2026.module1_cache.CacheLineLiveDemo;
import cn.edu.massz.ccec2026.module2_hash.HashDoSAttackDemo;
import cn.edu.massz.ccec2026.module3_concurrency.ConcurrencyCrashTest;
import cn.edu.massz.ccec2026.module4_bloom.BloomFilterLimitDemo;
import cn.edu.massz.ccec2026.module4_bloom.BloomMathDemo;
import cn.edu.massz.ccec2026.support.EnvInfo;

import java.util.Scanner;

public class CaseLauncher {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n========== CCEC2026 教学案例工程 ==========");
            System.out.println("1. 实验环境信息");
            System.out.println("2. CPU Cache 行列遍历现场演示");
            System.out.println("3. Hash DoS 与 HashMap 红黑树防御");
            System.out.println("4. HashMap / Hashtable / ConcurrentHashMap 并发对比");
            System.out.println("5. Bloom Filter 误判率与容量演示");
            System.out.println("6. Bloom Filter 理论容量计算");
            System.out.println("0. 退出");
            System.out.print("请选择：");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> EnvInfo.print();
                case "2" -> CacheLineLiveDemo.main(new String[]{});
                case "3" -> HashDoSAttackDemo.main(new String[]{});
                case "4" -> ConcurrencyCrashTest.main(new String[]{});
                case "5" -> BloomFilterLimitDemo.main(new String[]{});
                case "6" -> BloomMathDemo.main(new String[]{});
                case "0" -> {
                    System.out.println("已退出。");
                    return;
                }
                default -> System.out.println("输入无效，请重新选择。");
            }
        }
    }
}