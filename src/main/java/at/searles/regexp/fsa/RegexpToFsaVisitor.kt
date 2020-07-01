package at.searles.regexp.fsa

import at.searles.lexer.utils.IntervalSet
import at.searles.regexp.Regexp
import at.searles.regexp.Visitor

object RegexpToFsaVisitor: Visitor<Automaton> {
    override fun visitOr(l: Regexp, r: Regexp): Automaton {
        return l.accept(this).union(r.accept(this))
    }

    override fun visitThen(l: Regexp, r: Regexp): Automaton {
        return l.accept(this).concat(r.accept(this))
    }

    override fun visitFirstMatch(regexp: Regexp): Automaton {
        return regexp.accept(this).firstMatch()
    }

    override fun visitKleeneStar(regexp: Regexp): Automaton {
        return regexp.accept(this).kleeneStar()
    }

    override fun visitOpt(regexp: Regexp): Automaton {
        return regexp.accept(this).optional()
    }

    override fun visitText(string: String): Automaton {
        return Automaton.create(string)
    }

    override fun visitEmpty(): Automaton {
        return Automaton.empty()
    }

    override fun visitSet(set: IntervalSet): Automaton {
        return Automaton.create(set)
    }

    override fun visitKleenePlus(regexp: Regexp): Automaton {
        return regexp.accept(this).kleenePlus()
    }

    override fun visitMinus(l: Regexp, r: Regexp): Automaton {
        return l.accept(this).minus(r.accept(this))
    }

    override fun visitAnd(l: Regexp, r: Regexp): Automaton {
        return l.accept(this).intersect(r.accept(this))
    }

    override fun visitAtLeast(regexp: Regexp, count: Int): Automaton {
        val automaton = regexp.accept(this)
        return automaton.kleeneStar().concat(repeat(count, automaton))
    }

    private fun repeat(count: Int, automaton: Automaton): Automaton {
        return when (count) {
            0 -> Automaton.empty()
            1 -> automaton
            else -> repeat(count - 1, automaton.createCopy()).concat(automaton)
        }
    }

    override fun visitCount(regexp: Regexp, count: Int): Automaton {
        return repeat(count, regexp.accept(this))
    }

    override fun visitRange(regexp: Regexp, min: Int, max: Int): Automaton {
        val automaton = regexp.accept(this)
        val optAutomaton = automaton.optional()
        return repeat(min, automaton).concat(repeat(max - min, optAutomaton))
    }
}