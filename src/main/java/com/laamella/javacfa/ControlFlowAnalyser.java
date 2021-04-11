package com.laamella.javacfa;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedType;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

import static com.laamella.javacfa.Flow.ForwardDeclaredFlow;
import static com.laamella.javacfa.Flow.SimpleFlow;
import static com.laamella.javacfa.Flow.Type.*;

public class ControlFlowAnalyser {
    /**
     * @return a list of all possible flows in this compilation unit.
     */
    public CompilationUnitFlows analyse(CompilationUnit compilationUnit) {
        return new CompilationUnitFlows(
                compilationUnit.findAll(ConstructorDeclaration.class).stream()
                        .map(this::analyse)
                        .collect(List.collector()),
                compilationUnit.findAll(MethodDeclaration.class).stream()
                        .map(this::analyse)
                        .collect(List.collector()));
    }

    /**
     * Analyse the control flow in a piece of code.
     *
     * @param node the node to analyse, probably a {@link MethodDeclaration}.
     * @return the control flow graph start node.
     */
    public Flow analyse(Node node) {
        SimpleFlow startNode = new SimpleFlow(node, START, null);
        Flow flow = analyse(node, null, HashMap.empty(), null, null, HashMap.empty(), null, List.empty());
        if (flow == null) {
            return null;
        }
        startNode.setNext(flow);
        removeIndirections(startNode);
        return startNode;
    }

    /**
     * The analyser uses forward declared flow nodes so that a flow can
     * refer to following flows without analysing them already.
     * <p>
     * The forward declared flows are set to an actual flow with an indirection
     * trick. The problem is that the graph now has the same flow as two or
     * more object instances in the tree, which will give trouble when comparing
     * flows.
     * <p>
     * Here we replace all indirections before returning the flow graph to the user.
     */
    private void removeIndirections(Flow startNode) {
        new Visitor(startNode).visit(flow -> {
            while (flow.getNext() instanceof ForwardDeclaredFlow) {
                flow.setNext(((ForwardDeclaredFlow) flow.getNext()).getIndirection());
            }
            while (flow.getMayBranchTo() instanceof ForwardDeclaredFlow) {
                flow.setMayBranchTo(((ForwardDeclaredFlow) flow.getMayBranchTo()).getIndirection());
            }
        });
    }

    private Flow analyse(
            Node node,
            Flow back,
            Map<String, Flow> continueLabels,
            Flow next,
            Flow breakTo,
            Map<String, Flow> breakLabels,
            Flow returnFlow,
            List<Tuple2<Type, Flow>> catchClausesByCatchType) {
        if (node instanceof MethodDeclaration) {
            return ((MethodDeclaration) node).getBody()
                    .map(body -> analyse(body, back, continueLabels, next, next, breakLabels, returnFlow, catchClausesByCatchType))
                    .orElse(null);
        } else if (node instanceof ConstructorDeclaration) {
            return analyse(((ConstructorDeclaration) node).getBody(), back, continueLabels, next, next, breakLabels, returnFlow, catchClausesByCatchType);
        } else if (node instanceof NodeWithStatements) {
            return List.ofAll(((NodeWithStatements<?>) node).getStatements())
                    .reverse()
                    .foldLeft(next, (nextFlow, currentStmt) -> analyse(currentStmt, back, continueLabels, nextFlow, breakTo, breakLabels, returnFlow, catchClausesByCatchType));
        } else if (node instanceof SwitchStmt) {
            return analyseSwitchStmt((SwitchStmt) node, back, continueLabels, next, breakTo, breakLabels, returnFlow, catchClausesByCatchType);
        } else if (node instanceof ContinueStmt) {
            return ((ContinueStmt) node).getLabel()
                    .map(SimpleName::asString)
                    .map(label -> new SimpleFlow(node, CONTINUE, continueLabels.getOrElse(label, null)))
                    .orElseGet(() -> new SimpleFlow(node, CONTINUE, back));
        } else if (node instanceof BreakStmt) {
            return ((BreakStmt) node).getLabel()
                    .map(SimpleName::asString)
                    .map(label -> new SimpleFlow(node, BREAK, breakLabels.getOrElse(label, null)))
                    .orElseGet(() -> new SimpleFlow(node, BREAK, breakTo));
        } else if (node instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) node;
            Flow ifFlow = new SimpleFlow(node, CHOICE, null)
                    .setMayBranchTo(analyse(ifStmt.getThenStmt(), back, continueLabels, next, breakTo, breakLabels, returnFlow, catchClausesByCatchType));

            return Option.ofOptional(ifStmt.getElseStmt())
                    .map(elseStmt -> ifFlow.setNext(analyse(elseStmt, back, continueLabels, next, breakTo, breakLabels, returnFlow, catchClausesByCatchType)))
                    .getOrElse(() -> ifFlow.setNext(next));
        } else if (node instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) node;
            Flow forConditionFlow = new SimpleFlow(forStmt, CHOICE, next);
            Flow updateFlow = new SimpleFlow(forStmt, FOR_UPDATE, forConditionFlow);
            forConditionFlow
                    .setMayBranchTo(analyse(forStmt.getBody(), updateFlow, continueLabels, updateFlow, next, breakLabels, returnFlow, catchClausesByCatchType));
            return new SimpleFlow(forStmt, FOR_INITIALIZATION, forConditionFlow);
        } else if (node instanceof ForEachStmt) {
            ForEachStmt forEachStmt = (ForEachStmt) node;
            SimpleFlow forEachFlow = new SimpleFlow(node, CHOICE, next);
            return forEachFlow
                    .setMayBranchTo(analyse(forEachStmt.getBody(), forEachFlow, continueLabels, forEachFlow, next, breakLabels, returnFlow, catchClausesByCatchType));
        } else if (node instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) node;
            SimpleFlow whileFlow = new SimpleFlow(node, CHOICE, next);
            return whileFlow
                    .setMayBranchTo(analyse(whileStmt.getBody(), whileFlow, continueLabels, whileFlow, next, breakLabels, returnFlow, catchClausesByCatchType));
        } else if (node instanceof DoStmt) {
            DoStmt doStmt = (DoStmt) node;
            Flow conditionFlow = new SimpleFlow(node, CHOICE, next);
            Flow bodyFlow = analyse(doStmt.getBody(), back, continueLabels, conditionFlow, next, breakLabels, returnFlow, catchClausesByCatchType);
            conditionFlow.setMayBranchTo(bodyFlow);
            return bodyFlow;
        } else if (node instanceof LabeledStmt) {
            LabeledStmt labeledStmt = (LabeledStmt) node;
            String label = labeledStmt.getLabel().asString();
            ForwardDeclaredFlow labeledFlow = new ForwardDeclaredFlow();
            Flow directFlow = analyse(labeledStmt.getStatement(), back, continueLabels.put(label, labeledFlow), next, breakTo, breakLabels.put(label, next), returnFlow, catchClausesByCatchType);
            return labeledFlow.directTo(directFlow);
        } else if (node instanceof TryStmt) {
            return analyseTryStmt((TryStmt) node, back, continueLabels, next, breakTo, breakLabels, returnFlow, catchClausesByCatchType);
        } else if (node instanceof ThrowStmt) {
            try {
                ResolvedType thrownType = ((ThrowStmt) node).getExpression().calculateResolvedType();
                Flow correspondingCatch = catchClausesByCatchType
                        .find(tuple -> thrownType.isAssignableBy(tuple._1.resolve())).map(tuple -> tuple._2)
                        .getOrElse((Flow) null);
                return new SimpleFlow(node, THROW, correspondingCatch);
            } catch (IllegalStateException e) {
                return new SimpleFlow(node, THROW, null)
                        .addError("Cannot define a throws-flow without the symbol solver.");
            }
        } else if (node instanceof ReturnStmt) {
            return new SimpleFlow(node, RETURN, returnFlow);
        } else if (node instanceof Statement) {
            return new SimpleFlow(node, STEP, next);
        }
        throw new IllegalArgumentException(String.format("%s doesn't have a flow.", node.getClass().getSimpleName()));
    }

    private Flow analyseTryStmt(TryStmt tryStmt, Flow back, Map<String, Flow> continueLabels, Flow next, Flow breakTo, Map<String, Flow> breakLabels, Flow returnFlow, List<Tuple2<Type, Flow>> catchClausesByCatchType) {
        // We have to redirect all the flows escaping this block through the finally block.
        Flow finallyFlow = tryStmt.getFinallyBlock()
                .map(fb -> analyse(fb, back, continueLabels, next, breakTo, breakLabels, returnFlow, catchClausesByCatchType))
                .orElse(next);
        Flow finallyFlowForContinue = tryStmt.getFinallyBlock()
                .map(fb -> analyse(fb, back, continueLabels, back, breakTo, breakLabels, returnFlow, catchClausesByCatchType))
                .orElse(next);
        Flow finallyFlowForBreakTo = tryStmt.getFinallyBlock()
                .map(fb -> analyse(fb, back, continueLabels, breakTo, breakTo, breakLabels, returnFlow, catchClausesByCatchType))
                .orElse(next);
        // TODO redirect labeled breaks through the finally block
        // TODO redirect labeled continues through the finally block
        List<Tuple2<Type, Flow>> newCatchClausesByCatchType = tryStmt.getCatchClauses().stream()
                .map(cc -> Tuple.of(cc.getParameter().getType(),
                        analyse(cc.getBody(), back, continueLabels, finallyFlowForContinue, finallyFlowForBreakTo, breakLabels, returnFlow, catchClausesByCatchType)))
                .collect(List.collector())
                .appendAll(catchClausesByCatchType);
        return analyse(tryStmt.getTryBlock(), finallyFlowForContinue, continueLabels, finallyFlow, finallyFlowForBreakTo, breakLabels, returnFlow, newCatchClausesByCatchType);
    }

    private Flow analyseSwitchStmt(SwitchStmt switchStmt, Flow back, Map<String, Flow> continueLabels, Flow next, Flow breakTo, Map<String, Flow> breakLabels, Flow returnFlow, List<Tuple2<Type, Flow>> catchClausesByCatchType) {
        List<SwitchEntry> entriesInReverse = List.ofAll(switchStmt.getEntries()).reverse();
        // Figure out the mapping of entries to statement flows:
        Map<SwitchEntry, Flow> entryToFirstStatementFlow = entriesInReverse
                .foldLeft(Tuple.of(next, HashMap.empty()), (Tuple2<Flow, Map<SwitchEntry, Flow>> nextEntry, SwitchEntry currentEntry) -> {
                    Flow bodyFlow = analyse(currentEntry, back, continueLabels, nextEntry._1, next, breakLabels, returnFlow, catchClausesByCatchType);
                    if (bodyFlow == null) {
                        // Empty entry directly falls through to the next:
                        bodyFlow = nextEntry._1;
                    }
                    return Tuple.of(bodyFlow, nextEntry._2.put(currentEntry, bodyFlow));
                })._2;

        // Create CHOICE nodes pointing to the statement flows and tie them together:
        return entriesInReverse
                .foldLeft(next, (nextEntry, currentEntry) -> {
                    Flow bodyFlow = entryToFirstStatementFlow.get(currentEntry).getOrElseThrow(RuntimeException::new);
                    if (currentEntry.getLabels().isEmpty()) {
                        // The default case is not a choice. When all choices have been evaluated, default is mandatory.
                        return bodyFlow;
                    }

                    return new SimpleFlow(currentEntry, CHOICE, nextEntry)
                            .setMayBranchTo(bodyFlow);
                });
    }
}
