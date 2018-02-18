package ru.ifmo.nds.ens;

public class ENS_SS extends ENSBase {
    public ENS_SS(int maximumPoints, int maximumDimension) {
        super(maximumPoints, maximumDimension);
    }

    private int findRank(double[][] points, int index, int maxRank) {
        double[] point = points[index];
        int currRank = 0;
        while (currRank <= maxRank) {
            if (frontDominates(currRank, points, point)) {
                ++currRank;
            } else {
                break;
            }
        }
        return currRank;
    }

    @Override
    void sortCheckedImpl(double[][] points, int[] ranks, int maximalMeaningfulRank) {
        int n = ranks.length;
        int maxRank = -1;
        for (int i = 0; i < n; ++i) {
            int index = indices[i];
            int currRank = findRank(points, index, maxRank);
            maxRank = setRank(index, ranks, currRank, maxRank, maximalMeaningfulRank);
        }
    }

    @Override
    public String getName() {
        return "ENS-SS";
    }
}
