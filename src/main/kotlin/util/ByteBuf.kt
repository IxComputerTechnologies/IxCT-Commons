/*
 * Copyright © 2026 IxComputerTechnologies (a.k.a. IxCT) and Belov Ivan Alekseevich (a.k.a. MrWooly357). Licensed under the MIT licence.
 */

package net.ixct.ixct_commons.util

class ByteBuf(
    private val buffer: ArrayList<Byte> = ArrayList()
) : AutoCloseable {

    private var pointer = 0

    constructor(size: Int) : this(ArrayList(size))

    constructor(bytes: ByteArray) : this(ArrayList(bytes.asList()))


    fun size(): Int = buffer.size

    fun allocate(bytes: Int): ByteBuf {
        buffer.ensureCapacity(bytes)

        return this
    }

    fun putByte(b: Byte): ByteBuf {
        buffer.add(b)

        return this
    }

    fun putUByte(ub: UByte): ByteBuf {
        putByte(ub.toByte())

        return this
    }

    fun putShort(s: Short): ByteBuf {
        putByte((s.toInt() shr 8).toByte())
        putByte(s.toByte())

        return this
    }

    fun putUShort(us: UShort): ByteBuf {
        putShort(us.toShort())

        return this
    }

    fun putInt(i: Int): ByteBuf {
        putByte((i shr 24).toByte())
        putByte((i shr 16).toByte())
        putByte((i shr 8).toByte())
        putByte(i.toByte())

        return this
    }

    fun putUInt(ui: UInt): ByteBuf {
        putInt(ui.toInt())

        return this
    }

    fun putVarInt(i: Int): ByteBuf {
        var v = i

        while (v and 0xFFFFFF80.toInt() != 0) {
            allocate(1)
            putByte(((v and 0x7F) or 0x80).toByte())
            v = v ushr 7
        }

        allocate(1)
        putByte((v and 0x7F).toByte())

        return this
    }

    fun putUVarInt(ui: UInt): ByteBuf {
        putVarInt(ui.toInt())

        return this
    }

    fun putLong(l: Long): ByteBuf {
        putByte((l shr 56).toByte())
        putByte((l shr 48).toByte())
        putByte((l shr 40).toByte())
        putByte((l shr 32).toByte())
        putByte((l shr 24).toByte())
        putByte((l shr 16).toByte())
        putByte((l shr 8).toByte())
        putByte(l.toByte())

        return this
    }

    fun putULong(ul: ULong): ByteBuf {
        putLong(ul.toLong())

        return this
    }

    fun putVarLong(l: Long): ByteBuf {
        var v = l

        while (v and 0xFFFFFF80 != 0L) {
            putByte(((v and 0x7F) or 0x80).toByte())
            v = v ushr 7
        }

        putByte((v and 0x7F).toByte())

        return this
    }

    fun putUVarLong(ul: ULong): ByteBuf {
        putVarLong(ul.toLong())

        return this
    }

    fun putFloat(f: Float): ByteBuf {
        putInt(f.toRawBits())

        return this
    }

    fun putDouble(d: Double): ByteBuf {
        putLong(d.toRawBits())

        return this
    }

    fun putBoolean(bl: Boolean): ByteBuf {
        putByte(if (bl) 1 else 0)

        return this
    }

    fun putChar(c: Char): ByteBuf = putShort(c.code.toShort())

    fun putString(s: String): ByteBuf {
        val bytes = s.toByteArray(Charsets.UTF_8)
        putVarInt(bytes.size)
        putBytes(bytes)

        return this
    }

    fun allocateAndPutString(s: String): ByteBuf {
        val bytes = s.toByteArray(Charsets.UTF_8)
        putVarInt(bytes.size)
        allocate(bytes.size)
        bytes.forEach(this::putByte)

        return this
    }

    fun putBytes(buf: ByteBuf): ByteBuf {
        putBytes(buf.buffer)

        return this
    }

    fun putBytes(bytes: ByteArray): ByteBuf {
        putBytes(bytes.asList())

        return this
    }

    fun putBytes(bytes: Collection<Byte>): ByteBuf {
        buffer.addAll(bytes)

        return this
    }

    fun getByte(): Byte = buffer[pointer++]

    fun getUByte(): UByte = getByte().toUByte()

    fun getShort(): Short = (
            ((getByte().toInt() and 0xFF) shl 8)
                    or (getByte().toInt() and 0xFF)
            ).toShort()

    fun getUShort(): UShort = getShort().toUShort()

    fun getInt(): Int = (
            ((getByte().toInt() and 0xFF) shl 24)
                    or ((getByte().toInt() and 0xFF) shl 16)
                    or ((getByte().toInt() and 0xFF) shl 8)
                    or (getByte().toInt() and 0xFF)
            )

    fun getUInt(): UInt = getInt().toUInt()

    fun getVarInt(): Int {
        var v = 0
        var pos = 0
        var curr: Byte

        while (true) {
            curr = getByte()
            v = v or ((curr.toInt() and 0x7F) shl pos)

            if (curr.toInt() and 0x80 == 0)
                break
            else
                pos += 7

            if (pos >= 35)
                throw IllegalStateException("VarInt is too big!")
        }

        return v
    }

    fun getUVarInt(): UInt = getVarInt().toUInt()

    fun getLong(): Long = (
            ((getByte().toLong() and 0xFF) shl 56)
                    or ((getByte().toLong() and 0xFF) shl 48)
                    or ((getByte().toLong() and 0xFF) shl 40)
                    or ((getByte().toLong() and 0xFF) shl 32)
                    or ((getByte().toLong() and 0xFF) shl 24)
                    or ((getByte().toLong() and 0xFF) shl 16)
                    or ((getByte().toLong() and 0xFF) shl 8)
                    or (getByte().toLong() and 0xFF)
            )

    fun getULong(): ULong = getLong().toULong()

    fun getVarLong(): Long {
        var v = 0L
        var pos = 0
        var curr: Byte

        while (true) {
            curr = getByte()
            v = v or ((curr.toLong() and 0x7F) shl pos)

            if (curr.toInt() and 0x80 == 0)
                break
            else
                pos += 7

            if (pos >= 70)
                throw IllegalStateException("VarLong is too big!")
        }

        return v
    }

    fun getUVarLong(): ULong = getVarLong().toULong()

    fun getFloat(): Float = Float.fromBits(getInt())

    fun getDouble(): Double = Double.fromBits(getLong())

    fun getBoolean(): Boolean = getByte() == 1.toByte()

    fun getChar(): Char = getShort().toUShort().toInt().toChar()

    fun getString(): String {
        val size = getVarInt()
        val bytes = ByteArray(size)

        for (i in 0 until size)
            bytes[i] = getByte()

        return String(bytes, Charsets.UTF_8)
    }

    fun toArray(): ByteArray = buffer.toByteArray()

    fun copyRemaining(): ByteBuf = ByteBuf(ArrayList(buffer.subList(pointer, size())))

    override fun close() {
        buffer.clear()
        pointer = 0
    }

    override fun toString(): String = "[$buffer, $pointer]"


    class Builder(
        private val buf: ByteBuf = ByteBuf()
    ) {

        constructor(size: Int) : this(ByteBuf(size))


        fun allocate(bytes: Int): Builder {
            buf.allocate(bytes)

            return this
        }

        fun putByte(b: Byte): Builder {
            buf.putByte(b)

            return this
        }

        fun putUByte(ub: UByte): Builder {
            buf.putUByte(ub)

            return this
        }

        fun putShort(s: Short): Builder {
            buf.putShort(s)

            return this
        }

        fun putUShort(us: UShort): Builder {
            buf.putUShort(us)

            return this
        }

        fun putInt(i: Int): Builder {
            buf.putInt(i)

            return this
        }

        fun putUInt(ui: UInt): Builder {
            buf.putUInt(ui)

            return this
        }

        fun putVarInt(i: Int): Builder {
            buf.putVarInt(i)

            return this
        }

        fun putUVarInt(ui: UInt): Builder {
            buf.putUVarInt(ui)

            return this
        }

        fun putLong(l: Long): Builder {
            buf.putLong(l)

            return this
        }

        fun putULong(ul: ULong): Builder {
            buf.putULong(ul)

            return this
        }

        fun putVarLong(l: Long): Builder {
            buf.putVarLong(l)

            return this
        }

        fun putUVarLong(ul: ULong): Builder {
            buf.putUVarLong(ul)

            return this
        }

        fun putFloat(f: Float): Builder {
            buf.putFloat(f)

            return this
        }

        fun putDouble(d: Double): Builder {
            buf.putDouble(d)

            return this
        }

        fun putBoolean(bl: Boolean): Builder {
            buf.putBoolean(bl)

            return this
        }

        fun putChar(c: Char): Builder {
            buf.putChar(c)

            return this
        }

        fun putString(s: String): Builder {
            buf.putString(s)

            return this
        }

        fun allocateAndPutString(s: String): Builder {
            buf.allocateAndPutString(s)

            return this
        }

        fun putBytes(bytes: ByteArray): Builder {
            buf.putBytes(bytes)

            return this
        }

        fun putBytes(buf: ByteBuf): Builder {
            buf.putBytes(buf)

            return this
        }

        fun putBytes(bytes: Collection<Byte>): Builder {
            buf.putBytes(bytes)

            return this
        }

        fun build(): ByteBuf = buf

        override fun toString(): String = buf.toString()
    }


    class Lookup(
        private val buf: ByteBuf
    ) {


        fun size(): Int = buf.size()

        fun getByte(): Byte = buf.getByte()

        fun getUByte(): UByte = buf.getUByte()

        fun getShort(): Short = buf.getShort()

        fun getUShort(): UShort = buf.getUShort()

        fun getInt(): Int = buf.getInt()

        fun getUInt(): UInt = buf.getUInt()

        fun getVarInt(): Int = buf.getVarInt()

        fun getUVarInt(): UInt = buf.getUVarInt()

        fun getLong(): Long = buf.getLong()

        fun getULong(): ULong = buf.getULong()

        fun getVarLong(): Long = buf.getVarLong()

        fun getUVarLong(): ULong = buf.getUVarLong()

        fun getFloat(): Float = buf.getFloat()

        fun getDouble(): Double = buf.getDouble()

        fun getBoolean(): Boolean = buf.getBoolean()

        fun getChar(): Char = buf.getChar()

        fun getString(): String = buf.getString()

        fun hasMore(): Boolean = buf.pointer < size()

        override fun toString(): String = buf.toString()
    }
}
