package interpreter;

import interpreter.antlr.MineScriptParser;
import interpreter.types.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static interpreter.utils.TreeFromString.*;

class VisitorIntegrationTest {
    private final SymbolTable symbolTable = new SymbolTable();
    private final Visitor visitor = new Visitor(symbolTable);

    @ParameterizedTest
    @ValueSource(ints = {-1000, -10, 0, 10, 1000})
    void visitAssignReturnsCorrectNumbers(int value) {

        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = " + value + "\n"));
        Assertions.assertEquals(value, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void visitAssignReturnsCorrectBools(boolean value) {
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = " + value + "\n"));
        Assertions.assertEquals(value, ((MSBool) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitAssignInvalidAssignThrowsException() {
        System.setErr(null);
        Assertions.assertThrows(NullPointerException.class, () ->
                visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = \"hej\"\n"))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 10, 15})
    void visitWhileTrueExprCorrectNumIterations(int value) {
        String input = """
                while (x < %d) do
                    x = x + 1
                endwhile
                """.formatted(value);
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitWhile((MineScriptParser.WhileContext) getStmtTreeFromString(input));
        Assertions.assertEquals(value, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitWhileFalseExprSkipDo() {
        String input = """
                while (false) do
                    x = x + 1
                endwhile
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitWhile((MineScriptParser.WhileContext) getStmtTreeFromString(input));
        Assertions.assertEquals(0, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitWhileInvalidExprThrowsException() {
        String input = """
                while (abcd) do
                    x = x + 1
                endwhile
                """;
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitWhile((MineScriptParser.WhileContext) getStmtTreeFromString(input)));
    }

    @Test
    void visitIfIfEvaluatesTrue() {
        String input = """
                if (x is 0) do
                    x = 1
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(1, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfIfEvaluatesFalse() {
        String input = """
                if (x is 0) do
                    x = 1
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 1\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(1, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfElseIfEvaluatesTrue() {
        String input = """
                if (x is 0) do
                    x = 1
                else do
                    x = 2
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(1, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfElseIfEvaluatesFalse() {
        String input = """
                if (x is 0) do
                    x = 1
                else do
                    x = 2
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 1\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(2, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfElseIfElseIfEvaluatesTrue() {
        String input = """
                if (x is 0) do
                    x = 1
                else if (x is 1) do
                    x = 2
                else do
                    x = 3
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(1, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfElseIfElseElseIfEvaluatesTrue() {
        String input = """
                if (x is 0) do
                    x = 1
                else if (x is 1) do
                    x = 2
                else if (x is 2) do
                    x = 3
                else do
                    x = 4
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 2\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(3, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @Test
    void visitIfElseIfElseElseIfEvaluatesFalse() {
        String input = """
                if (x is 0) do
                    x = 1
                else if (x is 1) do
                    x = 2
                else if (x is 2) do
                    x = 3
                else do
                    x = 4
                endif
                """;
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 3\n"));
        visitor.visitIf((MineScriptParser.IfContext) getStmtTreeFromString(input));
        Assertions.assertEquals(4, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100})
    void visitRepeatValidTimesEqualsNumIterations(int value) {
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 0\n"));
        visitor.visitRepeat((MineScriptParser.RepeatContext) getStmtTreeFromString(
                """
                        repeat (%d) do
                            x = x + 1
                        endrepeat
                        """.formatted(value)
        ));
        Assertions.assertEquals(value, ((MSNumber) visitor.visitId((MineScriptParser.IdContext) getExprTreeFromString("x"))).getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void visitRepeatNegTimesThrowsException(int value) {
        Assertions.assertThrows(RuntimeException.class, () ->
                visitor.visitRepeat((MineScriptParser.RepeatContext) getStmtTreeFromString(
                        """
                                repeat (%d) do
                                    x = x + 1
                                endrepeat
                                """.formatted(value)
                ))
        );
    }

    @Test
    void visitRepeatNonIntTimesThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () ->
                visitor.visitRepeat((MineScriptParser.RepeatContext) getStmtTreeFromString(
                        """
                                repeat ("hej") do
                                    x = x + 1
                                endrepeat
                                """
                ))
        );
    }

    @Test
    void visitCompComparesIntValuesAndReturnsBooleanExpectsTrue() {
        Assertions.assertTrue(((MSBool) visitor.visitComp((MineScriptParser.CompContext) getExprTreeFromString("5 > 4"))).getValue());
        Assertions.assertTrue(((MSBool) visitor.visitComp((MineScriptParser.CompContext) getExprTreeFromString("5 >= 4"))).getValue());
    }

    @Test
    void visitCompComparesIntValuesAndReturnsBooleanExpectsFalse() {
        Assertions.assertFalse(((MSBool) visitor.visitComp((MineScriptParser.CompContext) getExprTreeFromString("5 < 4"))).getValue());
        Assertions.assertFalse(((MSBool) visitor.visitComp((MineScriptParser.CompContext) getExprTreeFromString("5 <= 4"))).getValue());
    }

    @Test
    void visitIsIsNotTestsIf5is5ExpectsTrue() {
        Assertions.assertTrue(((MSBool) visitor.visitIsIsNot((MineScriptParser.IsIsNotContext) getExprTreeFromString("5 is 5"))).getValue());
    }

    @Test
    void visitIsIsNotTestsIf5isNot5ExpectsFalse() {
        Assertions.assertFalse(((MSBool) visitor.visitIsIsNot((MineScriptParser.IsIsNotContext) getExprTreeFromString("5 is not 5"))).getValue());
    }

    @Test
    void visitAddSub() {
        Assertions.assertEquals(10, ((MSNumber) visitor.visitAddSub((MineScriptParser.AddSubContext) getExprTreeFromString("5 + 5"))).getValue());
        Assertions.assertEquals(0, ((MSNumber) visitor.visitAddSub((MineScriptParser.AddSubContext) getExprTreeFromString("5 - 5"))).getValue());
    }

    @Test
    void visitNumberReadsNumFromInputExpectsTrue() {
        Assertions.assertEquals(5, ((MSNumber) visitor.visitNumber((MineScriptParser.NumberContext) getExprTreeFromString("5"))).getValue());
    }

    @Test
    void visitNumberWithCorrectInputExpectedTrue(){
        Assertions.assertEquals(5, ((MSNumber) visitor.visitNumber((MineScriptParser.NumberContext) getExprTreeFromString("5"))).getValue());
    }

    @Test
    void visitNumberWithIncorrectInputExpectedTrue(){
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitNumber((MineScriptParser.NumberContext) getExprTreeFromString("abc")));
    }

    @Test
    void visitRelDirWithCorrectInputsExpectsTrue() {
        MSRelDir.Direction up_direction = ((MSRelDir) visitor.visitRelDir((MineScriptParser.RelDirContext) getExprTreeFromString("up"))).getValue();
        MSRelDir.Direction down_direction = ((MSRelDir) visitor.visitRelDir((MineScriptParser.RelDirContext) getExprTreeFromString("down"))).getValue();
        MSRelDir.Direction left_direction = ((MSRelDir) visitor.visitRelDir((MineScriptParser.RelDirContext) getExprTreeFromString("left"))).getValue();
        MSRelDir.Direction right_direction = ((MSRelDir) visitor.visitRelDir((MineScriptParser.RelDirContext) getExprTreeFromString("right"))).getValue();

        Assertions.assertEquals(MSRelDir.Direction.UP, up_direction);
        Assertions.assertEquals(MSRelDir.Direction.DOWN, down_direction);
        Assertions.assertEquals(MSRelDir.Direction.LEFT, left_direction);
        Assertions.assertEquals(MSRelDir.Direction.RIGHT, right_direction);
    }

    @Test
    void visitAbsDirWithCorrectInputsExpectedTrue(){
        MSAbsDir.Direction north_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("north"))).getValue();
        MSAbsDir.Direction south_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("south"))).getValue();
        MSAbsDir.Direction east_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("east"))).getValue();
        MSAbsDir.Direction west_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("west"))).getValue();
        MSAbsDir.Direction top_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("top"))).getValue();
        MSAbsDir.Direction bottom_direction = ((MSAbsDir) visitor.visitAbsDir((MineScriptParser.AbsDirContext) getExprTreeFromString("bottom"))).getValue();

        Assertions.assertEquals(MSAbsDir.Direction.NORTH, north_direction);
        Assertions.assertEquals(MSAbsDir.Direction.SOUTH, south_direction);
        Assertions.assertEquals(MSAbsDir.Direction.EAST, east_direction);
        Assertions.assertEquals(MSAbsDir.Direction.WEST, west_direction);
        Assertions.assertEquals(MSAbsDir.Direction.TOP, top_direction);
        Assertions.assertEquals(MSAbsDir.Direction.BOTTOM, bottom_direction);
    }

    @Test
    void visitParenExpr() {
        String input = "(10-3)\n";
        Assertions.assertEquals(7, ((MSNumber) visitor.visitParenExpr((MineScriptParser.ParenExprContext) getExprTreeFromString(input))).getValue());
    }

    @Test
    void visitMultDivMod() {
        Assertions.assertEquals(25, ((MSNumber) visitor.visitMultDivMod((MineScriptParser.MultDivModContext) getExprTreeFromString("5 * 5"))).getValue());
        Assertions.assertEquals(1, ((MSNumber) visitor.visitMultDivMod((MineScriptParser.MultDivModContext) getExprTreeFromString("5 / 5"))).getValue());
        Assertions.assertEquals(0, ((MSNumber) visitor.visitMultDivMod((MineScriptParser.MultDivModContext) getExprTreeFromString("5 % 5"))).getValue());
    }

    @Test
    void visitPow() {
        Assertions.assertEquals(25, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ 2"))).getValue());
        Assertions.assertEquals(1, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ 0"))).getValue());
        Assertions.assertEquals(0, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("0 ^ 5"))).getValue());
        Assertions.assertEquals(1, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("1 ^ 5"))).getValue());
    }

    @Test
    void visitPowNegativeNumberThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ -1")));
    }

    @Test
    void visitPowExpr() {
        Assertions.assertEquals(25, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ (2 + 0 * 1)"))).getValue());
    }

    @Test
    void visitPowId() {
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 2\n"));
        Assertions.assertEquals(25, ((MSNumber) visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ x"))).getValue());
    }

    @Test
    void visitPowStringThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitPow((MineScriptParser.PowContext) getExprTreeFromString("5 ^ unknown")));
    }

    @Test
    void visitNegNegativeNumber() {
        Assertions.assertEquals(-5, ((MSNumber) visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-5"))).getValue());
        Assertions.assertEquals(-3335, ((MSNumber) visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-3335"))).getValue());
    }

    @Test
    void visitNegExpr() {
        Assertions.assertEquals(-10, ((MSNumber) visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-(5 + 5)"))).getValue());
        Assertions.assertEquals(-1, ((MSNumber) visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-(5 / 5)"))).getValue());
    }

    @Test
    void visitNegId() {
        visitor.visitAssign((MineScriptParser.AssignContext) getStmtTreeFromString("x = 5\n"));
        Assertions.assertEquals(-5, ((MSNumber) visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-x"))).getValue());
    }

    @Test
    void visitNegStringThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitNeg((MineScriptParser.NegContext) getExprTreeFromString("-unknown")));
    }

    @Test
    void visitOrTrueOrFalseExpectsTrue() {
        Assertions.assertTrue(((MSBool) visitor.visitOr((MineScriptParser.OrContext) getExprTreeFromString("true or false"))).getValue());
    }

    @Test
    void visitOrTrueOrTrueExpectsTrue() {
        Assertions.assertTrue(((MSBool) visitor.visitOr((MineScriptParser.OrContext) getExprTreeFromString("true or true"))).getValue());
    }

    @Test
    void visitOrFalseOrFalseExpectsFalse() {
        Assertions.assertFalse(((MSBool) visitor.visitOr((MineScriptParser.OrContext) getExprTreeFromString("false or false"))).getValue());
    }


    @Test
    void visitAndComparesTwoTruesExpectsTrue() {
        Assertions.assertTrue(((MSBool) visitor.visitAnd((MineScriptParser.AndContext) getExprTreeFromString("true and true"))).getValue());
    }

    @Test
    void visitAndComparesFalseOutcomesExpectsFalse() {
        Assertions.assertFalse(((MSBool) visitor.visitAnd((MineScriptParser.AndContext) getExprTreeFromString("true and false"))).getValue());
        Assertions.assertFalse(((MSBool) visitor.visitAnd((MineScriptParser.AndContext) getExprTreeFromString("false and true"))).getValue());
        Assertions.assertFalse(((MSBool) visitor.visitAnd((MineScriptParser.AndContext) getExprTreeFromString("false and false"))).getValue());
    }

    @Test
    void visitFuncCallNoFormalParamsNoActualParamReturnsValue() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test() do 
                            return 5
                        enddefine
                        """
        ));
        Assertions.assertEquals(5, ((MSNumber) visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test()"))).getValue());
    }

    @Test
    void visitFuncCallNoFormalParamsOneActualParamThrowsException() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test() do 
                            return 5
                        enddefine
                        """
        ));
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test(5)")));
    }

    @Test
    void visitFuncCallOneFormalParamsNoActualParamsThrowsException() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test(x) do 
                            return 5
                        enddefine
                        """
        ));
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test()")));
    }

    @Test
    void visitFuncCallOneFormalParamOneActualParamReturnsVariable() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test(x) do 
                            return x
                        enddefine
                        """
        ));
        Assertions.assertEquals(5, ((MSNumber) visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test(5)"))).getValue());
    }

    @Test
    void visitFuncCallOneFormalParamMultipleActualParamsThrowsException() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test(x) do 
                            return x
                        enddefine
                        """
        ));
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test(5, 6)")));
    }

    @Test
    void visitFuncCallMultipleFormalParamsOneActualParamThrowsException() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test(x, y) do 
                            return x
                        enddefine
                        """
        ));
        Assertions.assertThrows(RuntimeException.class, () -> visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test(5)")));
    }

    @Test
    void visitFuncCallMultipleFormalParamsMultipleActualParamsReturnsValue() {
        visitor.visitFuncDecl((MineScriptParser.FuncDeclContext) getStmtTreeFromString(
                """
                        define test(x, y) do 
                            z = x + y
                            return z
                        enddefine
                        """
        ));
        Assertions.assertEquals(11, ((MSNumber) visitor.visitFuncCall((MineScriptParser.FuncCallContext) getExprTreeFromString("test(5, 6)"))).getValue());
    }
}