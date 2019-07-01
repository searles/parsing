package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.regex.Regex;

public class RegexNode extends ExprNode {
    public final Regex regex;

    public RegexNode(ParserStream stream, Regex regex) {
        super(stream);
        this.regex = regex;
    }
}
