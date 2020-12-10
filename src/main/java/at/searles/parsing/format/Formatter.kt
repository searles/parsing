package at.searles.parsing.format

import at.searles.buf.Frame
import at.searles.parsing.CanRecognize
import at.searles.parsing.ParserStream

abstract class Formatter<A>: ParserStream.Listener {
    private val commandCountStash = ArrayList<Int>()

    val commandList = ArrayList<A>()

    var maxOffset: Long = 0
    var maxCommandList = emptyList<A>()
    var maxFailedParser: CanRecognize? = null

    abstract fun createMarkCommand(marker: Any, offset: Long): A
    abstract fun createTokenCommand(tokenId: Int, frame: Frame): A

    override fun onMark(marker: Any, stream: ParserStream) {
        commandList.add(createMarkCommand(marker, stream.offset))
    }

    override fun onToken(tokenId: Int, frame: Frame, stream: ParserStream) {
        commandList.add(createTokenCommand(tokenId, frame))
    }

    override fun onTry(parser: CanRecognize, stream: ParserStream) {
        commandCountStash.add(commandList.size)
    }

    override fun onSuccess(parser: CanRecognize, stream: ParserStream) {
        require(commandCountStash.isNotEmpty())
        commandCountStash.removeLast()

        if(stream.offset >= maxOffset) {
            maxOffset = stream.offset
        }
    }

    override fun onFail(parser: CanRecognize, stream: ParserStream) {
        require(commandCountStash.isNotEmpty())

        if(stream.offset >= maxOffset) {
            // save longest command list. This is useful for eg syntax highlighting or partial formatting.
            maxOffset = stream.offset
            maxCommandList = commandList.toList()
            maxFailedParser = parser
        }

        val restoredSize = commandCountStash.removeLast()

        while(commandList.size > restoredSize) {
            commandList.removeLast()
        }
    }

}