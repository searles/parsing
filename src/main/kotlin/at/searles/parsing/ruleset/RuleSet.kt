package at.searles.parsing.ruleset

import at.searles.parsing.lexer.FrameStream

interface RuleSet {
    fun parse(stream: FrameStream)
}