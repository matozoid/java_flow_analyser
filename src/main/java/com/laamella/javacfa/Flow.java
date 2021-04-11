package com.laamella.javacfa;

import com.github.javaparser.ast.Node;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import static java.util.Objects.requireNonNull;

public class Flow {
    private final Node node;
    private Type type;
    private Flow next;
    private Flow mayBranchTo = null;
    private List<String> errors = List.empty();

    public Flow(Node node, Type type, Flow next) {
        this.node = node;
        this.type = requireNonNull(type);
        this.next = next;
    }

    /**
     * @return the JavaParser AST node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return the flow that may be branched to, or null if there is no branch.
     */
    public Flow getMayBranchTo() {
        return mayBranchTo;
    }

    /**
     * @return the normally taken flow, or null when the flow ends after this step.
     */
    public Flow getNext() {
        return next;
    }

    /**
     * @return an indication of the kind of flow.
     */
    public Type getType() {
        return type;
    }

    public Flow setNext(Flow next) {
        this.next = next;
        return this;
    }

    public Flow setMayBranchTo(Flow mayBranchTo) {
        this.mayBranchTo = mayBranchTo;
        return this;
    }

    public Flow setType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * @return the errors for this flow node.
     */
    public List<String> getErrors() {
        return errors;
    }

    public Flow addError(String message) {
        this.errors = errors.append(message);
        return this;
    }

    /**
     * @return the errors for all flow nodes.
     */
    public Map<Flow, List<String>> getAllErrors() {
        return new Visitor(this)
                .map(flow -> Tuple.of(flow, flow.getErrors()))
                .foldLeft(HashMap.empty(), HashMap::put);
    }

    enum Type {
        /**
         * A simple step: this flow always goes to the next.
         */
        STEP,
        /**
         * A step that may go to the next, or may branch to another flow.
         */
        CHOICE,
        /**
         * Like STEP, but it is cause by the break statement.
         */
        BREAK,
        /**
         * Like STEP, but it is cause by the continue statement.
         */
        CONTINUE,
        /**
         * The start node, which is the node that the user sent to the analyser.
         */
        START,
        /**
         * Like STEP, but it is cause by the return statement.
         */
        RETURN,
        /**
         * At this point the code throws an exception, which is caught at next.
         */
        THROW,
        /**
         * This flow runs the initialization part of the classic for statement.
         */
        FOR_INITIALIZATION,
        /**
         * This flow runs the update part of the classic for statement.
         */
        FOR_UPDATE
    }

    /**
     * A placeholder for when we don't know exactly where something is
     * going to flow yet, but we need a pointer to it anyway.
     * <p>
     * (Terrible use of OOP here to avoid ugly code elsewhere)
     */
    static class ForwardDeclaredFlow extends Flow {
        private Flow indirection;

        public ForwardDeclaredFlow() {
            super(null, Type.STEP, null);
        }

        @Override
        public Node getNode() {
            return indirection.getNode();
        }

        @Override
        public Flow getMayBranchTo() {
            return indirection.getMayBranchTo();
        }

        @Override
        public Flow getNext() {
            return indirection.getNext();
        }

        @Override
        public Type getType() {
            return indirection.getType();
        }

        @Override
        public Flow setNext(Flow next) {
            indirection.setNext(next);
            return indirection;
        }

        @Override
        public Flow setMayBranchTo(Flow mayBranchTo) {
            indirection.setMayBranchTo(mayBranchTo);
            return indirection;
        }

        @Override
        public Flow setType(Type type) {
            indirection.setType(type);
            return indirection;
        }

        @Override
        public List<String> getErrors() {
            return indirection.getErrors();
        }

        public Flow directTo(Flow flow) {
            indirection = flow;
            return flow;
        }

        public Flow getIndirection() {
            return indirection;
        }

    }
}
