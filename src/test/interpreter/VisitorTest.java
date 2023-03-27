package interpreter;

import interpreter.antlr.MineScriptLexer;
import interpreter.antlr.MineScriptParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

class VisitorTest {
    private final Visitor visitor = new Visitor();

    @Test
    void visitAssign() {
        visitor.visitAssign((MineScriptParser.AssignContext)getStmtTreeFromString("x = 5\n"));
        Assertions.assertEquals(5, visitor.visitId((MineScriptParser.IdContext)getExprTreeFromString("x")));
    }

    @Test
    void visitWhile() {
    }

    @Test
    void visitIf() {
    }

    @Test
    void visitRepeat() {
    }

    @Test
    void visitBoolPassTrueExpectedTrue() {
        Assertions.assertEquals(true, visitor.visitBool((MineScriptParser.BoolContext)getExprTreeFromString("true")));
    }
    @Test
    void visitBoolPassFalseExpectedFalse(){
        Assertions.assertEquals(false, visitor.visitBool((MineScriptParser.BoolContext)getExprTreeFromString("false")));
    }
    @Test
    void visitBoolPassExceptions(){
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitBool((MineScriptParser.BoolContext)getExprTreeFromString("abc")));
    }

    @Test
    void visitComp() {

    }

    @Test
    void visitId() {
    }

    @Test
    void visitAddSub() {
        Assertions.assertEquals(10, visitor.visitAddSub((MineScriptParser.AddSubContext)getExprTreeFromString("5 + 5")));
        Assertions.assertEquals(0, visitor.visitAddSub((MineScriptParser.AddSubContext)getExprTreeFromString("5 - 5")));
    }

    @Test
    void visitNumber() {
    }

    private MineScriptParser.ExpressionContext getExprTreeFromString(String input) {
        var lexer = new MineScriptLexer(CharStreams.fromString(input));
        var parser = new MineScriptParser(new CommonTokenStream(lexer));
        return parser.expression();
    }

    private MineScriptParser.StatementContext getStmtTreeFromString(String input) {
        var lexer = new MineScriptLexer(CharStreams.fromString(input));
        var parser = new MineScriptParser(new CommonTokenStream(lexer));
        return parser.statement();
    }
}