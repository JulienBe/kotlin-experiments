package traffic.jam

import java.util.*

class GumParticle(internal var anchorX: Float, internal var anchorY: Float) {
    var actualX = Historic<Float>(TRAIL_SIZE)
    var actualY = Historic<Float>(TRAIL_SIZE)
    var index = Historic<Int>(4)
    var verticalMove = rnd.nextBoolean()
    var ticks = rnd.nextInt(100)
    val i: Int
        get() = index.get()

    init {
        index.init(Shades.MAX_COLOR_INDEX)
        val tmpX: Float = (rnd.nextGaussian() * Main.dim.wf * 2f).toFloat()
        val tmpY: Float = (rnd.nextGaussian() * Main.dim.hf * 2f).toFloat()
        for (i in 0..TRAIL_SIZE) {
            actualX.addVal(tmpX)
            actualY.addVal(tmpY)
        }
    }


    companion object {
        const val INIT_OFFSET = 100.0f
        const val TRAIL_SIZE = Shades.EFFECTIVE_COLOR
        val downIter = (TRAIL_SIZE - 1) downTo 0
        val rnd = Random(0)
    }
}