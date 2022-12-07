package traffic.jam

import kotlin.math.abs

class Pos(private var xF: Float, private var yF: Float) {

    private var X = xF.toInt()
    private var Y = yF.toInt()

    val xf: Float
        get() = xF
    val yf: Float
        get() = yF
    val x: Int
        get() = X
    val y: Int
        get() = Y

    fun update(x: Int, y: Int) {
        this.X = x
        this.Y = y
        this.xF = X.toFloat()
        this.yF = Y.toFloat()
    }
    fun update(xf: Float, yf: Float): Pos {
        this.xF = xf
        this.yF = yf
        this.X = xF.toInt()
        this.Y = yF.toInt()
        return this
    }

    fun update(pos: Pos) {
        update(pos.xf, pos.yf)
    }

    fun copy(): Pos {
        return Pos(xF, yF)
    }

    fun roughDst(other: Pos): Float = abs(xF - other.xF) + abs(yF - other.yF)

    fun equalInt(pos: Pos): Boolean = X == pos.X && Y == pos.Y

    override fun toString(): String {
        return "Pos(xf=$xF, yf=$yF, x=$X, y=$Y)"
    }

    fun isZero(): Boolean = X == 0 && Y == 0
    fun contains(xf: Float, yf: Float, dim: Dimension): Boolean = xf > this.xF && xf <= this.xF + dim.wf && yf > this.yF && yf <= this.yF + dim.hf

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pos

        if (X != other.X) return false
        if (Y != other.Y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = X
        result = 31 * result + Y
        return result
    }
}