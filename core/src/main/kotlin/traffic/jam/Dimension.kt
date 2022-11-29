package traffic.jam

import kotlin.math.roundToInt

data class Dimension(val wf: Float, val hf: Float) {
    constructor(w: Int) : this(w.toFloat(), w.toFloat())
    constructor(w: Int, h: Int) : this(w.toFloat(), h.toFloat())

    val w: Int = wf.roundToInt()
    val h: Int = hf.roundToInt()
    val hw: Int = w / 2
    val hh: Int = h / 2
    val hwF: Float = wf / 2f
    val hhF: Float = hf / 2f

    companion object {
        val ZERO = Dimension(0, 0)
    }
}