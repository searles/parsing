package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.SourceInfo;
import at.searles.regex.Regex;

public class RegexNode extends ExprNode {
    public final Regex regex;

    public RegexNode(SourceInfo info, Regex regex) {
        super(info);
        this.regex = regex;
    }
}
