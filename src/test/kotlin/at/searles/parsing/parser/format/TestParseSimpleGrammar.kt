package at.searles.parsing.parser.format

import at.searles.parsing.parser.format.simple.SimpleGrammar
import at.searles.parsing.parser.format.simple.Tree
import org.junit.Test

class TestParseSimpleGrammar {
    @Test
    fun testParseExpr() {
        val result = SimpleGrammar().stmt.parse("a=fn(b,c)")
        assert(result.isSuccess)
        assert(result.value is Tree.Assignment)
    }

    @Test
    fun testParseComplexExpr() {
        val result = SimpleGrammar().stmt.parse("if(a==1)then{i=(0-1)^(2+3)}")
        assert(result.isSuccess)
        assert(result.value is Tree.IfStmt)
    }

    @Test
    fun testParseComplexExprWithSpaces() {
        val result = SimpleGrammar().stmt.parse("if a==1 then {\n    i = (0 - 1) ^ (2 + 3)\n}\n")
        assert(result.isSuccess)
        assert(result.value is Tree.IfStmt)
    }

    @Test
    fun testWhileStmt() {
        val result = SimpleGrammar().stmt.parse("while a==1 do {\n    i = (0 - 1) ^ (2 + 3)\n}\n")
        assert(result.isSuccess)
        assert(result.value is Tree.WhileStmt)
    }

    @Test
    fun testWhileStmtAfterAssignment() {
        val result = SimpleGrammar().program.parse("a = 1\nwhile a == 1 & b == 1 do {\n    i = (0 - 1) ^ (2 + 3)\n}\n")
        assert(result.isSuccess)
        assert(result.value is Tree.Block)
        assert((result.value as Tree.Block).stmts[1] is Tree.WhileStmt)
    }


}