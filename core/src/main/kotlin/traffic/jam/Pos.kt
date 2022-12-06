package traffic.jam

import kotlin.math.abs

class Pos(private var xF: Float, private var yF: Float) {

    private var x = xF.toInt()
    private var y = yF.toInt()

    val xf: Float
        get() = xF
    val yf: Float
        get() = yF

    fun update(x: Int, y: Int) {
        this.x = x
        this.y = y
        this.xF = x.toFloat()
        this.yF = y.toFloat()
    }
    fun update(xf: Float, yf: Float): Pos {
        this.xF = xf
        this.yF = yf
        this.x = xf.toInt()
        this.y = yf.toInt()
        return this
    }

    fun update(pos: Pos) {
        update(pos.x, pos.y)
    }

    fun copy(): Pos {
        return Pos(xF, yF)
    }

    fun roughDst(other: Pos): Float = abs(xF - other.xF) + abs(yF - other.yF)

    fun equalInt(pos: Pos): Boolean = x == pos.x && y == pos.y

    override fun toString(): String {
        return "Pos(xf=$xF, yf=$yF, x=$x, y=$y)"
    }

    fun isZero(): Boolean = x == 0 && y == 0
    fun contains(xf: Float, yf: Float, dim: Dimension): Boolean = xf > this.xF && xf <= this.xF + dim.wf && yf > this.yF && yf <= this.yF + dim.hf

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pos

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}