package com.laamella.javacfa;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.laamella.snippets_test_junit5.BasePath;
import com.laamella.snippets_test_junit5.SnippetFileFormat;
import com.laamella.snippets_test_junit5.SnippetTestFactory;
import io.vavr.control.Option;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.laamella.snippets_test_junit5.TestCaseFilenameFilter.allFiles;

class ControlFlowAnalyserTest {
    private final BasePath basePath = BasePath.fromMavenModuleRoot(ControlFlowAnalyserTest.class).inSrcTestResources();

    @TestFactory
    Stream<DynamicTest> singleResult() throws IOException {
        return new SnippetTestFactory<>(
                new SnippetFileFormat("/*", "*/\n", "\n/* expected:\n", "\n---\n", "*/"),
                basePath.inSubDirectory("single_result"),
                allFiles(),
                this::parse,
                (testCaseText, testCase) -> dumpDebugFlow(testCase)
        ).stream();
    }

    @TestFactory
    Stream<DynamicTest> compilationUnitTests() throws IOException {
        JavaParser jp = new JavaParser(
                new ParserConfiguration()
                        .setSymbolResolver(new JavaSymbolSolver(new CombinedTypeSolver(new ReflectionTypeSolver()))));
        return new SnippetTestFactory<>(
                new SnippetFileFormat("/*", "*/\n", "\n/* expected:\n", "\n---\n", "*/"),
                basePath.inSubDirectory("compilation_unit"),
                allFiles(),
                jp::parse,
                (testCaseText, testCase) -> dumpMultipleDebugFlow(testCase)
        ).stream();
    }

    private String dumpMultipleDebugFlow(ParseResult<CompilationUnit> result) {
        if (!result.isSuccessful()) {
            return result.toString();
        }
        CompilationUnitFlows flows = new ControlFlowAnalyser().analyse(result.getResult().get());
        DebugOutput debugOutput = new DebugOutput();
        return
                flows.getConstructorFlows().filter(Objects::nonNull).map(debugOutput::print).mkString("======\n") +
                        flows.getMethodFlows().filter(Objects::nonNull).map(debugOutput::print).mkString("======\n");
    }

    private Node parse(String testCase) {
        JavaParser jp = new JavaParser();
        return parseAs(testCase, jp::parseBlock)
                .getOrElse(parseAs(testCase, jp::parseBodyDeclaration)
                        .getOrElseThrow(AssertionFailedError::new));
    }

    private Option<Node> parseAs(String testCase, Function<String, ParseResult<? extends Node>> parse) {
        ParseResult<? extends Node> result = parse.apply(testCase);
        if (result.isSuccessful()) {
            return Option.of(result.getResult().get());
        }
        return Option.none();
    }

    private String dumpDebugFlow(Node input) {
        Flow flow = new ControlFlowAnalyser().analyse(input);
        if (flow == null) {
            return "Not a flow.";
        }
        return new DebugOutput().print(flow);
    }
}