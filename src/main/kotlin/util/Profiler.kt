package net.ixct.ixct_commons.util

class Profiler : AutoCloseable {

    private val start: Long = System.nanoTime()
    private val stack: ArrayDeque<Long> = ArrayDeque()


    fun startedAt(): Long = start

    fun activeFor(): Long = System.nanoTime() - start

    fun push(): Profiler {
        stack.addLast(System.nanoTime())

        return this
    }

    fun pop(): Long = System.nanoTime() - stack.removeLast()

    fun measure(block: () -> Unit): Long {
        push()
        block()

        return pop()
    }

    override fun close() {
        stack.clear()
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + stack.hashCode()

        return result
    }

    override fun equals(other: Any?): Boolean = (this === other) || (other is Profiler
            && start == other.start
            && stack == other.stack)

    override fun toString(): String = "[$start, $stack]"
}
