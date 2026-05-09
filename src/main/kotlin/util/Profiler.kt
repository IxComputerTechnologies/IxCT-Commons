/*
 * Copyright © 2026 IxComputerTechnologies (a.k.a. IxCT) and Belov Ivan Alekseevich (a.k.a. MrWooly357). Licensed under the MIT licence.
 */

package net.ixct.ixct_commons.util

class Profiler : AutoCloseable {

    private val start: ULong = System.nanoTime().toULong()
    private val stack: ArrayDeque<Long> = ArrayDeque()


    fun startedAt(): ULong = start

    fun activeFor(): ULong = System.nanoTime().toULong() - start

    fun push(): Profiler {
        stack.addLast(System.nanoTime())

        return this
    }

    fun pop(): ULong = System.nanoTime().toULong() - stack.removeLast().toULong()

    fun measure(block: () -> Unit): ULong {
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
