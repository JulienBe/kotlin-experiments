package traffic.jam

data class Dimension(val w: Int, val h: Int) {
    constructor(w: Int) : this(w, w)

    val wF: Float = w.toFloat()
    val hF: Float = h.toFloat()
    val hw: Int = w / 2
    val hh: Int = h / 2
    val hwF: Float = wF / 2f
    val hhF: Float = hF / 2f

    companion object {
        val ZERO = Dimension(0, 0)
    }
}