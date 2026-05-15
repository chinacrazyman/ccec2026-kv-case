package cn.edu.massz.ccec2026.support;

public class EnvInfo {

    public static void main(String[] args) {
        print();
    }

    public static void print() {
        Runtime runtime = Runtime.getRuntime();

        System.out.println("========== 实验环境信息 ==========");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("JVM Name: " + System.getProperty("java.vm.name"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("CPU Cores: " + runtime.availableProcessors());
        System.out.println("Max Memory: " + runtime.maxMemory() / 1024 / 1024 + " MB");
        System.out.println("=================================");
    }
}