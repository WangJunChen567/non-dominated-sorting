package ru.ifmo.nds.tests;

import ru.ifmo.nds.JensenFortinBuzdalov;
import ru.ifmo.nds.NonDominatedSortingFactory;

public class JensenFortinBuzdalovRedBlackHybridNDTParallel1Test extends CorrectnessTestsBase {
    @Override
    protected NonDominatedSortingFactory getFactory() {
        return JensenFortinBuzdalov.getRedBlackTreeSweepHybridNDTParallelImplementation(-1, 1);
    }
}
