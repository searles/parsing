package at.searles.parsing.parser.tools

import org.junit.Assert
import org.junit.Test

class BacktrackingListTest {
    @Test
    fun testPushBack() {
        val list = BacktrackingList<Int>()

        val list2 = list + 1 + 2
        val list3 = list2 + 3

        Assert.assertEquals(listOf(1, 2, 3), list3)

        val list4 = list2 + 4

        Assert.assertEquals(listOf(1, 2, 4), list4)
    }
}