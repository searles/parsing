package at.searles.lexer.test

import at.searles.lexer.Lexer
import at.searles.lexer.ShadowedTokenizer
import at.searles.lexer.TokenStream
import at.searles.regexparser.StringToRegex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SoftKeywordTest {
    private lateinit var shadowedTokenizer: ShadowedTokenizer
    private lateinit var lexer: Lexer

    @Before
    fun setUp() {
        this.lexer = Lexer()
        this.shadowedTokenizer = ShadowedTokenizer(lexer)
    }

    @Test
    fun testPublic() {
        val publicId = lexer.add("public")
        val ifId = lexer.add("if")
        val nameId = lexer.add(StringToRegex.parse("[a-z]+"))
        val comma = lexer.add(",")

        shadowedTokenizer.addShadowed(publicId)

        val stream = TokenStream.fromString("if,public,publicc")

        Assert.assertNotNull(lexer.matchToken(stream, ifId, false))
        Assert.assertNotNull(lexer.matchToken(stream, comma, false))
        Assert.assertNull(lexer.matchToken(stream, nameId, true))
        Assert.assertNotNull(lexer.matchToken(stream, publicId, false))
        Assert.assertNotNull(lexer.matchToken(stream, comma, false))
        Assert.assertNull(lexer.matchToken(stream, publicId, false))
        Assert.assertNotNull(lexer.matchToken(stream, nameId, true))
    }

    @Test
    fun testPublicShadowed() {
        val publicId = lexer.add("public")
        val ifId = lexer.add("if")
        val nameId = lexer.add(StringToRegex.parse("[a-z]+"))
        val comma = lexer.add(",")

        shadowedTokenizer.addShadowed(publicId)

        val stream = TokenStream.fromString("if,public,public")

        Assert.assertNotNull(shadowedTokenizer.matchToken(stream, ifId, false))
        Assert.assertNotNull(shadowedTokenizer.matchToken(stream, comma, false))
        Assert.assertNotNull(shadowedTokenizer.matchToken(stream, nameId, true))
        Assert.assertNotNull(shadowedTokenizer.matchToken(stream, comma, false))
        Assert.assertNotNull(shadowedTokenizer.matchToken(stream, publicId, false))
    }
}
