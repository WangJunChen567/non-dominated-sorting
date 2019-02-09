package ru.ifmo.nds.jfb.hybrid.ps;

public interface ParameterStrategyFactory {
    ParameterStrategy createStrategy();

    ParameterStrategyFactory CONST = () -> ConstParameterStrategy.INSTANCE;
}
