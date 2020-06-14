package at.searles.parsingtools

import at.searles.parsing.Parser
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsingtools.list.EmptyListCreator
import at.searles.parsingtools.list.ListAppender
import at.searles.parsingtools.list.ListCreator
import at.searles.parsingtools.opt.NoneCreator
import at.searles.parsingtools.opt.SomeCreator
import java.util.*

fun <T> Parser<T>.list1(separator: Recognizer): Parser<List<T>> {
    return (this + ListCreator()) + (separator + this.fold(ListAppender(1))).rep()
}

fun <T> Parser<T>.list1(): Parser<List<T>> {
    return (this + ListCreator()) + this.fold(ListAppender(1)).rep()
}

fun <T> Parser<T>.list(separator: Recognizer): Parser<List<T>> {
    return EmptyListCreator<T>() + separator.join(this.fold(ListAppender(0)))
}

fun <T> Parser<T>.list(): Parser<List<T>> {
    return EmptyListCreator<T>() + this.fold(ListAppender(0)).rep()
}

fun <T> Parser<T>.optional(): Parser<Optional<T>> {
    return this + SomeCreator() or NoneCreator()
}
