package cn.edu.massz.ccec2026.module2_hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashDoSAttackDemo {

    public static void main(String[] args) {

        System.out.println("========== HashMap 极端碰撞与 Hash DoS 防御实验 ==========");
        System.out.println("实验目标：观察哈希碰撞导致的复杂度退化，以及 Java 8 红黑树化机制的工程价值\n");

        String s1 = "Aa";
        String s2 = "BB";

        System.out.println("【阶段一】验证哈希碰撞现象");
        System.out.println(" -> 字符串 '" + s1 + "' 的 hashCode: " + s1.hashCode());
        System.out.println(" -> 字符串 '" + s2 + "' 的 hashCode: " + s2.hashCode());
        System.out.println(" -> 不同字符串可能拥有完全相同的哈希值\n");

        System.out.println("【阶段二】构造极端哈希碰撞数据");
        System.out.println(" -> 正在利用排列组合生成 65536 个哈希值完全一致的字符串...");

        List<String> maliciousData = generateMaliciousStrings();

        System.out.println(" -> 数据生成完成，总量：" + maliciousData.size());
        System.out.println(" -> 第一个元素 hashCode：" + maliciousData.get(0).hashCode());
        System.out.println(" -> 最后一个元素 hashCode：" + maliciousData.get(65535).hashCode() + "\n");

        System.out.println("【阶段三】模拟 Hash DoS 极端碰撞攻击");

        System.out.println(" -> 正在测试传统链表 HashMap...");

        LegacyHashMap<String, Integer> legacyMap = new LegacyHashMap<>();

        long startTime1 = System.currentTimeMillis();

        for (int i = 0; i < maliciousData.size(); i++) {
            legacyMap.put(maliciousData.get(i), i);
        }

        long endTime1 = System.currentTimeMillis();

        System.out.println(" -> 传统 HashMap 总耗时：" + (endTime1 - startTime1) + " ms");
        System.out.println(" -> 在高并发场景下，此类极端碰撞可能导致 CPU 长时间满载，系统响应显著下降。\n");

        System.out.println(" -> 正在测试 Java 8 HashMap（红黑树防御机制）...");

        Map<String, Integer> java8Map = new HashMap<>();

        long startTime2 = System.currentTimeMillis();

        for (int i = 0; i < maliciousData.size(); i++) {
            java8Map.put(maliciousData.get(i), i);
        }

        long endTime2 = System.currentTimeMillis();

        System.out.println(" -> Java 8 HashMap 总耗时：" + (endTime2 - startTime2) + " ms");

        System.out.println("\n【教学结论】");
        System.out.println("1. 哈希表的平均复杂度 O(1) 并不意味着永远不会退化。");
        System.out.println("2. 极端哈希碰撞会导致链表长度急剧增长，查询与插入复杂度退化为 O(N)。");
        System.out.println("3. 在大量碰撞数据下，总体耗时可能退化为 O(N²)。");
        System.out.println("4. Java 8 引入红黑树化机制后，可将复杂度重新控制在 O(log N)。");
        System.out.println("5. 数据结构设计不仅影响性能，也直接关系到系统安全性与稳定性。");
    }

    private static List<String> generateMaliciousStrings() {
        String[] seeds = {"Aa", "BB"};
        List<String> result = new ArrayList<>(65536);
        generate(seeds, "", 16, result);
        return result;
    }

    private static void generate(String[] seeds, String current, int depth, List<String> result) {
        if (depth == 0) {
            result.add(current);
            return;
        }

        for (String seed : seeds) {
            generate(seeds, current + seed, depth - 1, result);
        }
    }

    static class LegacyHashMap<K, V> {

        private Node<K, V>[] table = new Node[16];

        public void put(K key, V value) {

            int hash = key == null ? 0 : key.hashCode();
            int index = (hash ^ (hash >>> 16)) & 15;

            Node<K, V> head = table[index];
            Node<K, V> current = head;

            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }

            Node<K, V> newNode = new Node<>(key, value);
            newNode.next = head;
            table[index] = newNode;
        }

        static class Node<K, V> {

            K key;
            V value;
            Node<K, V> next;

            Node(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }
    }
}
