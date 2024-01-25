package at.searles.parsing.parser.format

import at.searles.parsing.parser.format.simple.SimpleGrammar
import org.junit.Assert
import org.junit.Test

class TestInterpretSimpleGrammar {
    @Test
    fun testEuclid() {
        val program = """
            a = 108
            b = 90
            
            while a > 0 & b > 0 do {
                if b > a then {
                    c = a
                    a = b
                    b = c
                }

                a = a - b                
            }
        """.trimIndent()

        val syntaxTree = SimpleGrammar().program.parse(program).value

        val variables = mutableMapOf<String, Any>()

        syntaxTree.exec(variables)

        Assert.assertEquals(0, variables["a"])
        Assert.assertEquals(18, variables["b"])
    }
}