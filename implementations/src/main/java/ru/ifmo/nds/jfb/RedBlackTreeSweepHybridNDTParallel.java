package ru.ifmo.nds.jfb;

import ru.ifmo.nds.ndt.Split;
import ru.ifmo.nds.ndt.SplitBuilderShifted;
import ru.ifmo.nds.ndt.TreeRankNode;

public class RedBlackTreeSweepHybridNDTParallel extends RedBlackTreeSweep {
    private static final int THRESHOLD_3D = 100;
    private static final int THRESHOLD_ALL = 20000;

    private final int threshold;

    public RedBlackTreeSweepHybridNDTParallel(int maximumPoints, int maximumDimension, int allowedThreads, int threshold) {
        super(maximumPoints, maximumDimension, allowedThreads);

        this.threshold = threshold;
    }

    @Override
    protected void closeImpl() {
        super.closeImpl();
    }

    @Override
    public String getName() {
        return "Jensen-Fortin-Buzdalov parallel sorting, " + getThreadDescription() + " (tree sweep, hybrid with ENS-NDT, threshold " + threshold + ")";
    }

    @Override
    protected boolean helperAHookCondition(int size, int obj) {
        switch (obj) {
            case 1:
                return false;
            case 2:
                return size < THRESHOLD_3D;
            default:
                return size < THRESHOLD_ALL;
        }
    }

    @Override
    protected boolean helperBHookCondition(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int obj) {
        return helperAHookCondition(goodUntil - goodFrom + weakUntil - weakFrom, obj);
    }

    @Override
    protected int helperAHook(int from, int until, int obj) {
        int M = obj + 1;

        SplitBuilderShifted splitBuilder = new SplitBuilderShifted(until - from);
        TreeRankNode tree = TreeRankNode.EMPTY;
        double[][] localPoints = new double[until - from][M];
        Split split = splitBuilder.result(transposedPoints, from, until, indices, M, threshold, from);

        for (int i = from; i < until; ++i) {
            System.arraycopy(points[indices[i]], 0, localPoints[i - from], 0, M);
        }

        int minOverflow = until;
        for (int i = from; i < until; ++i) {
            int idx = indices[i];
            ranks[idx] = tree.evaluateRank(localPoints[i - from], ranks[idx], split, M);

            if (ranks[idx] <= maximalMeaningfulRank) {
                tree = tree.add(localPoints[i - from], ranks[idx], split, threshold);
            } else if (minOverflow > i) {
                minOverflow = i;
            }
        }
        return kickOutOverflowedRanks(minOverflow, until);
    }

    @Override
    protected int helperBHook(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int obj, int tempFrom) {
        int M = obj + 1;
        int sizeUnion = goodUntil - goodFrom + weakUntil - weakFrom;

        SplitBuilderShifted splitBuilder = new SplitBuilderShifted(goodUntil - goodFrom);
        TreeRankNode tree = TreeRankNode.EMPTY;
        double[][] localPoints = new double[sizeUnion][M];

        Split split = splitBuilder.result(transposedPoints, goodFrom, goodUntil, indices, M, threshold, goodFrom);

        for (int good = goodFrom; good < goodUntil; ++good) {
            System.arraycopy(points[indices[good]], 0, localPoints[good - goodFrom], 0, M);
        }
        for (int weak = weakFrom; weak < weakUntil; ++weak) {
            System.arraycopy(points[indices[weak]], 0, localPoints[weak - weakFrom + goodUntil - goodFrom], 0, M);
        }

        int minOverflow = weakUntil;
        for (int good = goodFrom, weak = weakFrom; weak < weakUntil; ++weak) {
            int wi = indices[weak];
            int gi;
            while (good < goodUntil && (gi = indices[good]) < wi) {
                tree = tree.add(localPoints[good - goodFrom], ranks[gi], split, threshold);
                ++good;
            }
            ranks[wi] = tree.evaluateRank(localPoints[weak - weakFrom + goodUntil - goodFrom], ranks[wi], split, M);
            if (minOverflow > weak && ranks[wi] > maximalMeaningfulRank) {
                minOverflow = weak;
            }
        }
        return kickOutOverflowedRanks(minOverflow, weakUntil);
    }
}
