package ru.ifmo.nds;

import ru.ifmo.nds.jfb.*;

public class JensenFortinBuzdalov {
    private JensenFortinBuzdalov() {}

    public static NonDominatedSortingFactory getRedBlackTreeSweepImplementation(int allowedThreads) {
        return (p, d) -> new RedBlackTreeSweep(p, d, allowedThreads);
    }

    public static NonDominatedSortingFactory getFenwickSweepImplementation(int allowedThreads) {
        return (p, d) -> new FenwickSweep(p, d, allowedThreads);
    }

    public static NonDominatedSortingFactory getRedBlackTreeSweepHybridFNDSImplementation(int allowedThreads) {
        return (p, d) -> new RedBlackTreeSweepHybridLinearNDS(p, d, allowedThreads);
    }

    public static NonDominatedSortingFactory getRedBlackTreeSweepHybridENSImplementation(int allowedThreads) {
        return (p, d) -> new RedBlackTreeSweepHybridENS(p, d, allowedThreads);
    }

    public static NonDominatedSortingFactory getRedBlackTreeSweepHybridNDTImplementation(int threshold) {
        return (p, d) -> new RedBlackTreeSweepHybridNDT(p, d, threshold);
    }

    public static NonDominatedSortingFactory getRedBlackTreeSweepHybridNDTParallelImplementation(int allowedThreads, int threshold) {
        return (p, d) -> new RedBlackTreeSweepHybridNDTParallel(p, d, allowedThreads, threshold);
    }
}
