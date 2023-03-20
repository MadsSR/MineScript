package src.main.interpreter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import src.main.interpreter.lexer.*;
import src.main.interpreter.parser.MineScriptParser;
import src.main.interpreter.visitor.Visitor;

public class Interpreter {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromFileName("src/main/interpreter/input.minescript");
        // create a lexer that feeds off of input CharStream
        MineScriptLexer lexer = new MineScriptLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        MineScriptParser parser = new MineScriptParser(tokens);
        ParseTree tree = parser.program(); // begin parsing at init rule
        Visitor visitor = new Visitor();
        visitor.visit(tree);
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}