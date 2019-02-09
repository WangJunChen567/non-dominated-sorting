package ru.ifmo.nds.jfb.hybrid.ps;

public enum ConstParameterStrategy implements ParameterStrategy {
    INSTANCE;

    @Override
    public int next(int current) {
        return current;
    }
}
