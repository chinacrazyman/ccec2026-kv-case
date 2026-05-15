# CCEC2026 高并发 KV 系统数据结构工程化教学案例

本仓库为 **中国高校计算机教育大会（CCEC2026）第三届全国计算机教学案例大赛** 参赛案例配套资源。

案例名称：

> 面向高并发 KV 系统的数据结构工程化教学案例：性能、安全、并发与容量的四重重构

本案例面向数据结构、数据结构与算法、Java 程序设计等课程，围绕高并发 KV 系统中的真实工程问题，将 CPU Cache、HashMap、ConcurrentHashMap、Bloom Filter 等知识组织成四个递进式教学模块，帮助学生从“理解数据结构概念”走向“解释系统行为、分析工程权衡、形成设计方案”。

---

## 一、案例背景

传统数据结构教学往往强调抽象逻辑结构和时间复杂度分析，但在真实工程系统中，数据结构还会受到以下因素影响：

- CPU Cache 与内存访问局部性
- 哈希碰撞与极端输入攻击
- 多线程并发写入与线程安全
- 海量数据判重与内存容量约束
- 精确性、性能、空间和实现复杂度之间的权衡

因此，本案例以“高并发 KV 系统”为统一工程背景，引导学生理解：

> 数据结构不是孤立的考试知识点，而是支撑真实系统性能、安全、并发和容量的底层基础。

---

## 二、四个教学模块

| 模块 | 主题 | 核心问题 | 对应代码 |
|---|---|---|---|
| 模块一 | CPU Cache 与内存访问局部性 | 同样是 `O(n²)`，为什么真实运行时间可能相差数十倍？ | `CacheLineLiveDemo` / `CacheLineJmhBenchmark` |
| 模块二 | HashMap 极端碰撞与 Hash DoS 防御 | HashMap 平均 `O(1)` 为什么会退化？Java 8 红黑树化解决了什么问题？ | `HashDoSAttackDemo` |
| 模块三 | 并发写入与并发容器 | 普通 HashMap 为什么不能用于多线程共享写入？ | `ConcurrencyCrashTest` |
| 模块四 | Bloom Filter 海量数据判重 | 如何用可控误判率换取极大的空间节省？ | `BloomFilterLimitDemo` / `BloomMathDemo` / `JolMemoryLayoutDemo` |

---

## 三、工程结构

```text
ccec2026-kv-case
│
├─ pom.xml
│
├─ docs
│  ├─ 运行说明.md
│  ├─ 实验记录模板.md
│  └─ 课堂演示脚本.md
│
├─ results
│  ├─ cache-line
│  ├─ hash-dos
│  ├─ concurrency
│  └─ bloom-filter
│
└─ src
   └─ main
      └─ java
         └─ cn
            └─ edu
               └─ massz
                  └─ ccec2026
                     ├─ CaseLauncher.java
                     ├─ module1_cache
                     ├─ module2_hash
                     ├─ module3_concurrency
                     ├─ module4_bloom
                     └─ support
## 四、运行环境

| 项目     | 推荐配置                |
| -------- | ----------------------- |
| JDK      | JDK 17                  |
| IDE      | IntelliJ IDEA           |
| 构建工具 | Maven                   |
| 编码     | UTF-8                   |
| 操作系统 | Windows 10 / Windows 11 |

------

## 五、运行方式

### 1. 克隆仓库

```bash
git clone https://github.com/chinacrazyman/ccec2026-kv-case.git
```

进入工程目录：

```bash
cd ccec2026-kv-case
```

### 2. 使用 IDEA 打开工程

在 IntelliJ IDEA 中选择：

```text
File → Open → ccec2026-kv-case
```

等待 Maven 自动加载依赖。

### 3. 运行统一入口

如果使用统一入口，可运行：

```text
cn.edu.massz.ccec2026.CaseLauncher
```

### 4. 分模块运行

也可以分别运行以下类：

```text
cn.edu.massz.ccec2026.module1_cache.CacheLineLiveDemo
cn.edu.massz.ccec2026.module2_hash.HashDoSAttackDemo
cn.edu.massz.ccec2026.module3_concurrency.ConcurrencyCrashTest
cn.edu.massz.ccec2026.module4_bloom.BloomFilterLimitDemo
cn.edu.massz.ccec2026.module4_bloom.JolMemoryLayoutDemo
```

------

## 六、JMH 基准测试

模块一提供 JMH 基准测试，用于更严谨地验证 CPU Cache 与内存访问局部性对性能的影响。

在工程根目录执行：

```bash
mvn clean package
```

然后运行：

```bash
java -jar target/ccec2026-kv-case-benchmarks.jar CacheLineJmhBenchmark
```

典型实验结果显示，在相同 `O(n²)` 时间复杂度下，按列遍历可能比按行遍历慢数十倍。

------

## 七、模块四 VM 参数建议

运行 `BloomFilterLimitDemo` 时，建议在 IDEA 中设置 VM options：

```text
-Xms1g -Xmx4g
```

设置路径：

```text
Run → Edit Configurations → VM options
```

------

## 八、教学资源

本仓库配套提供以下教学资源：

| 文件                   | 说明                       |
| ---------------------- | -------------------------- |
| `docs/运行说明.md`     | 工程运行步骤与常见问题说明 |
| `docs/实验记录模板.md` | 学生实验记录模板           |
| `docs/课堂演示脚本.md` | 教师课堂演示脚本           |
| `results/`             | 典型实验结果截图与记录     |

完整教学资源包可包括：

- 实验指导书
- 学生实验报告模板
- 评价量规表
- 课堂 PPT
- 代码运行演示视频
- 实验截图
- 决赛现场汇报材料

------

## 九、教学主线

本案例采用如下教学闭环：

```text
实验冲突 → 原理解释 → 源码验证 → 架构权衡 → 学习产出
```

对应四个模块：

| 教学模块     | 实验冲突                           | 工程结论                 |
| ------------ | ---------------------------------- | ------------------------ |
| CPU Cache    | 同样 `O(n²)`，运行时间差异巨大     | 复杂度不是完整性能模型   |
| Hash DoS     | 平均 `O(1)` 的哈希表被极端碰撞拖垮 | 极端输入需要防御性设计   |
| 并发容器     | Key 不重复仍然可能丢数据           | 单线程正确不代表并发可靠 |
| Bloom Filter | 精确集合内存代价过高               | 可控误判换取空间优势     |

------

## 十、适用课程与对象

适用课程：

- 数据结构
- 数据结构与算法
- Java 程序设计
- Java 核心编程
- 软件工程实践
- 系统能力训练

适用对象：

- 计算机科学与技术专业学生
- 软件工程专业学生
- 软件技术专业学生
- 已具备 Java 基础和数据结构基础的学生

------

## 十一、教学目标

学生完成本案例学习后，应能够：

1. 解释时间复杂度与真实运行性能之间的差异。
2. 说明 HashMap 在极端哈希碰撞下的复杂度退化过程。
3. 分析 Java 8 HashMap 红黑树化机制的工程价值。
4. 复现普通 HashMap 在多线程共享写入场景下的数据丢失问题。
5. 比较 Hashtable 与 ConcurrentHashMap 的同步策略差异。
6. 推导 Bloom Filter 的空间占用与误判率关系。
7. 从性能、安全、并发和容量角度进行数据结构选型。

------

## 十二、案例特色

本案例具有以下特点：

1. **统一工程背景**：以高并发 KV 系统贯穿四个教学模块。
2. **真实代码驱动**：每个知识点均配套可运行 Java 实验。
3. **结果可复现**：提供 Maven 工程、运行说明和实验记录模板。
4. **强调工程权衡**：不只讲“是什么”，更讲“为什么这样设计”。
5. **衔接 JDK 源码**：引导学生理解 HashMap、ConcurrentHashMap 等源码设计思想。
6. **服务教学推广**：配套实验指导书、报告模板、评价量规和课堂脚本。

------

## 十三、License

本仓库用于教学研究、课程建设和教学案例交流。使用时请注明来源。
