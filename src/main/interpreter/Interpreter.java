package interpreter;

import minescript.block.entity.TurtleBlockEntity;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import interpreter.antlr.*;

public class Interpreter {

    private static TurtleBlockEntity entity;
    private static String program;

    public Interpreter(String program, TurtleBlockEntity entity) {
        Interpreter.program = program;
        Interpreter.entity = entity;
    }

    public void run() {
        // create a CharStream that reads from standard input
//        CharStream input = CharStreams.fromString(CharStreams.fromFileName("src/main/interpreter/input.minescript") + System.lineSeparator());
        CharStream input = CharStreams.fromString(program + System.lineSeparator());
        // create a lexer that feeds off of input CharStream
        MineScriptLexer lexer = new MineScriptLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        MineScriptParser parser = new MineScriptParser(tokens);
        ParseTree tree = parser.program(); // begin parsing at init rule
        Visitor visitor = new Visitor(entity);
        visitor.visit(tree);
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}