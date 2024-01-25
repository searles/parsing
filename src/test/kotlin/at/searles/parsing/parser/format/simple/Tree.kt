package at.searles.parsing.parser.format.simple

interface Tree {
    fun exec(variables: MutableMap<String, Any>): Any?

    data class IfStmt(val condition: Tree, val thenBranch: Tree, val elseBranch: Tree?): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any? {
            return if(condition.exec(variables) as Boolean) {
                thenBranch.exec(variables)
            } else {
                elseBranch?.exec(variables)
            }
        }
    }

    data class WhileStmt(val condition: Tree, val body: Tree): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any? {
            while(condition.exec(variables) as Boolean) {
                body.exec(variables)
            }

            return null
        }
    }

    data class Block(val stmts: List<Tree>): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any? {
            var returnValue: Any? = null

            for(stmt in stmts) {
                returnValue = stmt.exec(variables)
            }

            return returnValue
        }
    }

    data class Assignment(val lValue: Tree, val rValue: Tree): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any? {
            with(rValue.exec(variables)!!) {
                variables[(lValue as Id).id] = this
                return this
            }
        }
    }

    data class BiTree(val op: Op, val arg0: Tree, val arg1: Tree): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any {
            return op.exec(listOf(arg0.exec(variables)!!, arg1.exec(variables)!!))
        }
    }

    data class UnTree(val op: Op, val arg: Tree): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any {
            return op.exec(listOf(arg.exec(variables)!!))
        }
    }

    data class Application(val fn: Tree, val args: List<Tree>): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any? {
            when((fn as Id).id) {
                "print" -> args.forEach { println(it.exec(variables)) }
                else -> error("Unknown instruction: $fn")
            }

            return null
        }
    }

    data class Id(val id: String): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any {
            return variables[id] ?: error("No such value: $id")
        }
    }

    data class Num(val num: Int): Tree {
        override fun exec(variables: MutableMap<String, Any>): Any {
            return num
        }
    }
}
