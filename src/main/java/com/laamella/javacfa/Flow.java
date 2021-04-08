package com.laamella.javacfa;

import com.github.javaparser.ast.Node;

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

    void setNext(Flow next);

    void setMayBranchTo(Flow mayBranchTo);

    void setType(Type type);

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
         * Not a flow node. These only occur during analysis, the user won't see them.
         */
        EMPTY,
        /**
         * Like STEP, but it is cause by the return statement.
         */
        RETURN
    }

    class SimpleFlow implements Flow {
        private final Node node;
        private Type type;
        private Flow next;
        private Flow mayBranchTo = null;

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
        public void setNext(Flow next) {
            this.next = next;
        }

        @Override
        public void setMayBranchTo(Flow mayBranchTo) {
            this.mayBranchTo = mayBranchTo;
        }

        @Override
        public void setType(Type type) {
            this.type = type;
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
        public void setNext(Flow next) {
            indirection.setNext(next);
        }

        @Override
        public void setMayBranchTo(Flow mayBranchTo) {
            indirection.setMayBranchTo(mayBranchTo);
        }

        @Override
        public void setType(Type type) {
            indirection.setType(type);
        }

        public void directTo(Flow flow) {
            indirection = flow;
        }

        public Flow getIndirection() {
            return indirection;
        }

    }
}
