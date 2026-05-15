package cn.edu.massz.ccec2026.module1_cache;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(value = 1)
@State(Scope.Thread)
public class CacheLineJmhBenchmark {

    @Param({"4096"})
    private int size;

    private int[][] array;

    @Setup(Level.Trial)
    public void setup() {
        array = new int[size][size];
    }

    @Benchmark
    public long rowFirstTraversal() {
        long sum = 0;
        for (int i = 0; i < size; i++) {
            int[] row = array[i];
            for (int j = 0; j < size; j++) {
                row[j] = i + j;
                sum += row[j];
            }
        }
        return sum;
    }

    @Benchmark
    public long columnFirstTraversal() {
        long sum = 0;
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                array[i][j] = i + j;
                sum += array[i][j];
            }
        }
        return sum;
    }
}