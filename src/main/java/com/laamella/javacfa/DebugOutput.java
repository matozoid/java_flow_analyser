package com.laamella.javacfa;


import static java.util.Objects.requireNonNull;

/**
 * Shows the control flow information available in a node for inspection.
 */
public class DebugOutput {
    public String print(Flow flow) {
        requireNonNull(flow);
        StringBuilder output = new StringBuilder();
        new Visitor(flow).visit(f -> innerPrint(output, f));

        return output.toString();
    }

    private void innerPrint(StringBuilder output, Flow flow) {
        output.append(String.format("%-4.4s %-6.6s ", extractLineNumber(flow), flow.getType().name()));
        if (flow.getNext() != null) {
            output.append("-> " + extractLineNumber(flow.getNext()));
        } else {
            output.append("-> end");
        }
        if (flow.getMayBranchTo() != null) {
            output.append(" or " + extractLineNumber(flow.getMayBranchTo()));
        }

        output.append("\n");
    }

    private String extractLineNumber(Flow flow) {
        return flow.getNode().getRange().map(range -> "" + range.begin.line).orElse("end");
    }
}
