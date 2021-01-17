package at.searles.parsing.printer

interface Tree {
    class IntNode(val value: Int): Tree {}
    class AddNode(val left: Tree, val right: Tree): Tree {}
}