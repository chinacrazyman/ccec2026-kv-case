package cn.edu.massz.ccec2026.module4_bloom;

public class BloomMathDemo {

    public static void main(String[] args) {
        printBloomMemory(10_000_000L, 0.01);
        printBloomMemory(10_000_000L, 0.0001);
        printBloomMemory(1_000_000_000L, 0.01);
        printBloomMemory(1_000_000_000L, 0.0001);
    }

    private static void printBloomMemory(long n, double p) {
        double bits = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        double mb = bits / 8.0 / 1024 / 1024;
        double gb = mb / 1024;
        double k = bits / n * Math.log(2);

        System.out.println("==================================");
        System.out.println("数据规模 n = " + n);
        System.out.println("误判率 p = " + p);
        System.out.printf("理论位数组大小 = %.0f bits%n", bits);
        System.out.printf("理论内存 ≈ %.2f MB，约 %.2f GB%n", mb, gb);
        System.out.printf("理论最优哈希函数个数 ≈ %.2f%n", k);
    }
}