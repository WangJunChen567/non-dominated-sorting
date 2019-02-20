package ru.ifmo.nds;

import ru.ifmo.nds.IdCollection;
import ru.ifmo.nds.NonDominatedSorting;
import ru.ifmo.nds.NonDominatedSortingFactory;
import ru.ifmo.nds.jfb.JFBBase;
import ru.ifmo.nds.jfb.SplitMergeMetricA;
import ru.ifmo.nds.jfb.SplitMergeMetricB;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RunnerWithMetric {
    private static void run(JFBBase sorting, double[][][] tests, int[] ranks, PrintWriter pw) throws IOException {
        while (System.in.available() == 0) {
            long t0 = System.nanoTime();
            int sum = 0;
            for (double[][] test : tests) {
                sorting.sort(test, ranks);
                List<SplitMergeMetricA> splitMergeMetricA = sorting.getSplitMergeMetricA();
                List<SplitMergeMetricB> splitMergeMetricB = sorting.getSplitMergeMetricB();
                pw.println("Metrics A");
                for (SplitMergeMetricA i :splitMergeMetricA) {
                    pw.println(i);
                }
                pw.println("Metrics B");
                for (SplitMergeMetricB i :splitMergeMetricB) {
                    pw.println(i);
                }
                for (int i : ranks) sum += i;
            }
            pw.flush();
            long time = System.nanoTime() - t0;
            System.out.println("Time " + (time / 1e9) + " s, checksum " + sum);
        }
    }

    public static void main(String[] args) throws IOException {
        NonDominatedSortingFactory<JFBBase> factory = IdCollection.getNonDominatedSortingFactory(args[0]);
        int n = Integer.parseInt(args[1]);
        int d = Integer.parseInt(args[2]);
        int count = Integer.parseInt(args[3]);
        PrintWriter pw = new PrintWriter(new File(args[0] + "_" + n + "_" + d + "_" + count + ".out"));
        pw.println("n: " + n + " d: " + d + " count: " + count);

        double[][][] tests = new double[count][n][d];
        int[] ranks = new int[n];
        Random random = new Random(8234925);

        if (Integer.parseInt(args[4]) == 1) {
            pw.println("hypercube");
            fillUniformHypercube(random, tests, count, n, d);
        } else if (Integer.parseInt(args[4]) == 2) {
            fillUniformCorrelated(random, tests, count, n, d);
            pw.println("correlated");
        }

//        for (int i = 0; i < count; ++i) {
//            pw.println("Test "+ i);
//            for (int j = 0; j < n; ++j) {
//                for (int k = 0; k < d; ++k) {
//                    pw.print(tests[i][j][k] + " ");
//                }
//                pw.println();
//            }
//        }

        JFBBase sorting = factory.getInstance(n, d);
        run(sorting, tests, ranks, pw);
    }

    //args[4] = 1
    private static void fillUniformHypercube(Random random, double[][][] tests, int count, int n, int d) {
        for (int i = 0; i < count; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < d; ++k) {
                    tests[i][j][k] = random.nextDouble();
                }
            }
        }
    }

    //args[4] = 2
    private static void fillUniformCorrelated(Random random, double[][][] tests, int count, int n, int d) {
        for (int i = 0; i < count; ++i) {
            int x = i % 2 == 0 ? 1 : d - 2;
            for (int j = 0; j < n; ++j) {
                double first = random.nextDouble();
                for (int k = 0; k < d; ++k) {
                    tests[i][j][k] = k == x ? -first : first;
                }
            }
            Collections.shuffle(Arrays.asList(tests[i]), random);
        }
    }

}
