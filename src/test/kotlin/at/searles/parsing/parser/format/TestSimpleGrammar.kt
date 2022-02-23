package at.searles.parsing.parser.format

import org.junit.Test

class TestSimpleGrammar {
    @Test
    fun testParseExpr() {
        val result = SimpleGrammar().stmt.parse("a=fn(b,c)")
        assert(result.isSuccess)
        assert(result.value is SimpleGrammar.Assignment)
    }

    @Test
    fun testParseComplexExpr() {
        val result = SimpleGrammar().stmt.parse("if(a==1)then{i=(0-1)^(2+3)}")
        assert(result.isSuccess)
        assert(result.value is SimpleGrammar.IfStmt)
    }
}