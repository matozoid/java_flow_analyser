package com.laamella.javacfa;

import io.vavr.collection.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class Visitor {
    private final Flow flow;

    public Visitor(Flow flow) {
        this.flow = flow;
    }

    public <R> List<R> map(Function<Flow, R> mapper) {
        // This can probably be rewritten as a tidy little recursive function.
        Set<Flow> seen = LinkedHashSet.empty();
        Set<Flow> todo = LinkedHashSet.of(flow);
        List<R> result = List.empty();
        while (!todo.isEmpty()) {
            Flow flow = todo.get();
            todo = todo.remove(flow);
            if (seen.contains(flow)) {
                continue;
            }
            result = result.append(mapper.apply(flow));
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
        return result;
    }

    public void visit(Consumer<Flow> consumer) {
        map(flow -> {
            consumer.accept(flow);
            return null;
        });
    }
}
