package cn.edu.massz.ccec2026.module4_bloom;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

/**
 * JOL 内存布局演示：
 * 用于观察 Long、链表节点 Node、树节点 TreeNode 等对象布局，
 * 支撑 HashSet / HashMap / Bloom Filter 的容量估算教学。
 *
 * 说明：
 * 1. 本类使用自定义 Node 与 TreeNode，不直接反射 JDK HashMap 内部类，
 *    这样可以避免 JDK 17 模块访问限制，课堂演示更稳定。
 * 2. 自定义 LongNode 结构近似模拟 HashMap.Node 的核心字段：
 *    hash、key、value、next。
 * 3. 自定义 LongTreeNode 结构近似模拟红黑树节点的额外字段：
 *    parent、left、right、prev、red。
 */
public class JolMemoryLayoutDemo {

    private static final long TEN_MILLION = 10_000_000L;
    private static final long ONE_BILLION = 1_000_000_000L;

    public static void main(String[] args) {

        System.out.println("========== JOL 对象内存布局演示 ==========");
        System.out.println("实验目标：观察 Long、Node、TreeNode 等对象布局，支撑海量数据结构的内存估算\n");

        printJvmInfo();

        Long longObject = Long.valueOf(2026L);
        LongNode node = new LongNode(2026, Long.valueOf(100001L), Long.valueOf(1L), null);
        LongTreeNode treeNode = new LongTreeNode(2026, Long.valueOf(100001L), Long.valueOf(1L), null);

        printLongLayout(longObject);
        printNodeLayout(node);
        printTreeNodeLayout(treeNode);

        printGraphFootprint(longObject, node, treeNode);
        printCapacityEstimation(longObject, node, treeNode);
        printTeachingConclusion();
    }

    private static void printJvmInfo() {
        System.out.println("【阶段一】JVM 内存模型信息");
        System.out.println(VM.current().details());
    }

    private static void printLongLayout(Long longObject) {
        System.out.println("\n【阶段二】Long 包装对象内存布局");
        System.out.println(" -> 示例对象：Long.valueOf(2026L)");
        System.out.println(ClassLayout.parseInstance(longObject).toPrintable());
    }

    private static void printNodeLayout(LongNode node) {
        System.out.println("\n【阶段三】链表节点 LongNode 内存布局");
        System.out.println(" -> 近似模拟 HashMap.Node 的核心字段：hash、key、value、next");
        System.out.println(ClassLayout.parseInstance(node).toPrintable());
    }

    private static void printTreeNodeLayout(LongTreeNode treeNode) {
        System.out.println("\n【阶段四】树节点 LongTreeNode 内存布局");
        System.out.println(" -> 近似模拟红黑树节点的额外字段：parent、left、right、prev、red");
        System.out.println(ClassLayout.parseInstance(treeNode).toPrintable());
    }

    private static void printGraphFootprint(Long longObject, LongNode node, LongTreeNode treeNode) {
        System.out.println("\n【阶段五】对象图占用估算");

        System.out.println(" -> 单个 Long 对象图：");
        System.out.println(GraphLayout.parseInstance(longObject).toFootprint());

        System.out.println(" -> 单个 LongNode 对象图（包含 key、value 引用对象）：");
        System.out.println(GraphLayout.parseInstance(node).toFootprint());

        System.out.println(" -> 单个 LongTreeNode 对象图（包含 key、value 引用对象）：");
        System.out.println(GraphLayout.parseInstance(treeNode).toFootprint());
    }

    private static void printCapacityEstimation(Long longObject, LongNode node, LongTreeNode treeNode) {
        long longSize = VM.current().sizeOf(longObject);
        long nodeSize = VM.current().sizeOf(node);
        long treeNodeSize = VM.current().sizeOf(treeNode);

        /*
         * 这里是教学估算，不是对某一种集合实现的精确内存审计。
         * 估算思路：
         * 1. 如果每个 ID 使用 Long 对象保存，会产生 Long 包装对象成本。
         * 2. 如果放入 HashMap / HashSet，还会产生节点对象、桶数组引用等额外成本。
         * 3. 如果链表树化，TreeNode 的字段更多，单节点成本更高。
         */
        long hashLikePerElement = longSize + nodeSize;
        long treeLikePerElement = longSize + treeNodeSize;

        System.out.println("\n【阶段六】海量数据结构内存估算");
        System.out.println(" -> 单个 Long 对象大小：" + longSize + " bytes");
        System.out.println(" -> 单个 LongNode 对象大小：" + nodeSize + " bytes");
        System.out.println(" -> 单个 LongTreeNode 对象大小：" + treeNodeSize + " bytes");

        System.out.println("\n【估算一】若每个 ID 使用 Long + 链表节点保存：");
        printEstimate("1000 万 ID", TEN_MILLION, hashLikePerElement);
        printEstimate("10 亿 ID", ONE_BILLION, hashLikePerElement);

        System.out.println("\n【估算二】若极端碰撞后使用 Long + 树节点保存：");
        printEstimate("1000 万 ID", TEN_MILLION, treeLikePerElement);
        printEstimate("10 亿 ID", ONE_BILLION, treeLikePerElement);

        System.out.println("\n【对照】Bloom Filter 理论位数组空间");
        printBloomEstimate("1000 万 ID，误判率 0.01%", TEN_MILLION, 0.0001);
        printBloomEstimate("10 亿 ID，误判率 0.01%", ONE_BILLION, 0.0001);
    }

    private static void printEstimate(String label, long count, long perElementBytes) {
        double totalBytes = count * (double) perElementBytes;
        System.out.printf(" -> %s：约 %.2f MB，约 %.2f GB%n",
                label,
                totalBytes / 1024 / 1024,
                totalBytes / 1024 / 1024 / 1024);
    }

    private static void printBloomEstimate(String label, long n, double p) {
        double bits = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        double mb = bits / 8.0 / 1024 / 1024;
        double gb = mb / 1024;
        System.out.printf(" -> %s：约 %.2f MB，约 %.2f GB%n", label, mb, gb);
    }

    private static void printTeachingConclusion() {
        System.out.println("\n【教学结论】");
        System.out.println("1. Java 对象并不只保存业务数据，还包含对象头、对齐填充和引用字段等额外成本。");
        System.out.println("2. Long、Node、TreeNode 等对象在海量数据场景下会产生显著内存开销。");
        System.out.println("3. HashSet / HashMap 类结构适合精确存储，但在千万级、十亿级场景下内存压力明显。");
        System.out.println("4. Bloom Filter 不保存完整对象，只维护位数组，因此能用较小空间完成大规模判重。");
        System.out.println("5. 该实验支撑案例四的核心思想：用可控误判率换取极大的空间节省。");
    }

    /**
     * 近似模拟 HashMap.Node<K,V> 的核心字段。
     */
    static class LongNode {
        int hash;
        Long key;
        Long value;
        LongNode next;

        LongNode(int hash, Long key, Long value, LongNode next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 近似模拟红黑树节点。
     * 真实 JDK HashMap.TreeNode 还继承 LinkedHashMap.Entry，
     * 本类只保留教学中最关键的树结构字段。
     */
    static class LongTreeNode extends LongNode {
        LongTreeNode parent;
        LongTreeNode left;
        LongTreeNode right;
        LongTreeNode prev;
        boolean red;

        LongTreeNode(int hash, Long key, Long value, LongNode next) {
            super(hash, key, value, next);
        }
    }
}
