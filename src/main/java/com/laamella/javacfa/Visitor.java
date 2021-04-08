package com.laamella.javacfa;

import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.Set;

import java.util.function.Consumer;

public class Visitor {
    private final Flow flow;

    public Visitor(Flow flow) {
        this.flow = flow;
    }

    public void visit(Consumer<Flow> consumer) {
        Set<Flow> seen = LinkedHashSet.empty();
        Set<Flow> todo = LinkedHashSet.of(flow);
        while (!todo.isEmpty()) {
            Flow flow = todo.get();
            todo = todo.remove(flow);
            if (seen.contains(flow)) {
                continue;
            }
            consumer.accept(flow);
            if (flow instanceof Flow.ForwardDeclaredFlow) {
                seen = seen.add(((Flow.ForwardDeclaredFlow) flow).getIndirection());
            }
            seen = seen.add(flow);

            if (flow.getNext() != null) {
                todo = todo.add(flow.getNext());
            }
            if (flow.getMayBranchTo() != null) {
                todo = todo.add(flow.getMayBranchTo());
            }
        }

    }
}
