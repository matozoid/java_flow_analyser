package com.laamella.javacfa;

import io.vavr.collection.List;

public class CompilationUnitFlows {
    private final List<Flow> constructorFlows;
    private final List<Flow> methodFlows;

    public CompilationUnitFlows(List<Flow> constructorFlows, List<Flow> methodFlows) {
        this.constructorFlows = constructorFlows;
        this.methodFlows = methodFlows;
    }

    public List<Flow> getConstructorFlows() {
        return constructorFlows;
    }

    public List<Flow> getMethodFlows() {
        return methodFlows;
    }
}
