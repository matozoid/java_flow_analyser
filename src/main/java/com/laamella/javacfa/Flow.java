package com.laamella.javacfa;

import com.github.javaparser.ast.Node;
import io.vavr.collection.List;

import static java.util.Objects.requireNonNull;

public interface Flow {
    /**
     * @return the JavaParser AST node.
     */
    Node getNode();

    /**
     * @return the flow that may be branched to, or null if there is no branch.
     */
    Flow getMayBranchTo();

    /**
     * @return the normally taken flow, or null when the flow ends after this step.
     */
    Flow getNext();

    /**
     * @return an indication of the kind of flow.
     */
    Type getType();

    Flow setNext(Flow next);

    Flow setMayBranchTo(Flow mayBranchTo);

    Flow setType(Type type);

    List<String> getErrors();

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
        THROW
    }

    class SimpleFlow implements Flow {
        private final Node node;
        private Type type;
        private Flow next;
        private Flow mayBranchTo = null;
        private List<String> errors = List.empty();

        public SimpleFlow(Node node, Type type, Flow next) {
            this.node = node;
            this.type = requireNonNull(type);
            this.next = next;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public Flow getMayBranchTo() {
            return mayBranchTo;
        }

        @Override
        public Flow getNext() {
            return next;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Flow setNext(Flow next) {
            this.next = next;
            return this;

        }

        @Override
        public Flow setMayBranchTo(Flow mayBranchTo) {
            this.mayBranchTo = mayBranchTo;
            return this;

        }

        @Override
        public Flow setType(Type type) {
            this.type = type;
            return this;

        }

        @Override
        public List<String> getErrors() {
            return errors;
        }

        public Flow addError(String message) {
            this.errors = errors.append(message);
            return this;
        }
    }

    class ForwardDeclaredFlow implements Flow {
        private Flow indirection;

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

        public void directTo(Flow flow) {
            indirection = flow;
        }

        public Flow getIndirection() {
            return indirection;
        }

    }
}
