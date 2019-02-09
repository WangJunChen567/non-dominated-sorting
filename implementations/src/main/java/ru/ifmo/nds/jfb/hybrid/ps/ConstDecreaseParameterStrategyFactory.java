package ru.ifmo.nds.jfb.hybrid.ps;

public class ConstDecreaseParameterStrategyFactory implements ParameterStrategyFactory {
    private final int decreaseCount;

    public ConstDecreaseParameterStrategyFactory(int decreaseCount) {
        this.decreaseCount = decreaseCount;
    }

    @Override
    public ParameterStrategy createStrategy() {
        return new ConstDecreaseParameterStrategy(decreaseCount);
    }
}
