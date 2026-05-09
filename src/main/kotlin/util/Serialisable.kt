/*
 * Copyright © 2026 IxComputerTechnologies (a.k.a. IxCT) and Belov Ivan Alekseevich (a.k.a. MrWooly357). Licensed under the MIT licence.
 */

package net.ixct.ixct_commons.util

/**
 * A marker for objects that can (and potentially will) be serialised into bytes.
 * @author MrWooly357
 * @since 0.1.0
 * @see ByteBuf
 */
fun interface Serialisable {


    /**
     * Serialises this thing into a [ByteBuf].
     * @return the byte representation of this object as a [ByteBuf].
     */
    fun serialise(): ByteBuf
}
