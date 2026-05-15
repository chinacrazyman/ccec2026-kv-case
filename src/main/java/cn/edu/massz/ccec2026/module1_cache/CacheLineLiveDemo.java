package cn.edu.massz.ccec2026.module1_cache;

public class CacheLineLiveDemo {

    public static void main(String[] args) {

        int SIZE = 10000;
        int[][] arr = new int[SIZE][SIZE];

        System.out.println("========== CPU Cache 与内存访问局部性实验 ==========");
        System.out.println("实验目标：比较相同 O(n²) 复杂度下，不同内存访问方式带来的性能差异");
        System.out.println("数组规模：" + SIZE + " × " + SIZE + "\n");

        long start1 = System.currentTimeMillis();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                arr[i][j] = 1;
            }
        }

        System.out.println("【阶段一】按行优先遍历（Row First Traversal）");
        System.out.println(" -> 访问方式：连续访问同一行中的相邻元素");
        System.out.println(" -> 总耗时：" + (System.currentTimeMillis() - start1) + " ms\n");

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                arr[j][i] = 1;
            }
        }

        System.out.println("【阶段二】按列优先遍历（Column First Traversal）");
        System.out.println(" -> 访问方式：频繁跨行访问不同数组对象");
        System.out.println(" -> 总耗时：" + (System.currentTimeMillis() - start2) + " ms\n");

        System.out.println("【教学结论】");
        System.out.println("1. 两段代码时间复杂度均为 O(n²)，但真实运行时间差异巨大。");
        System.out.println("2. 按行遍历具有更好的空间局部性，更容易命中 CPU Cache。");
        System.out.println("3. 按列遍历会产生更多 Cache Miss 与内存访问延迟。");
        System.out.println("4. 工程性能不仅取决于算法复杂度，还受到内存访问模式影响。");
    }
}
