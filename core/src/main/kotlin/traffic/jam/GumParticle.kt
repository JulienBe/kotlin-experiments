package traffic.jam

import java.util.*

class GumParticle(internal var anchorX: Float, internal var anchorY: Float) {
    var offsetX = 0f
    var offsetY = 0f
    var verticalMove = rnd.nextBoolean()
    var ticks = rnd.nextInt(100)

    init {
        if (rnd.nextBoolean())
            offsetX = (rnd.nextGaussian() * INIT_OFFSET).toFloat()
        else
            offsetY = (rnd.nextGaussian() * INIT_OFFSET).toFloat()
    }
    var index = Shades.MAX_COLOR_INDEX

    companion object {
        const val INIT_OFFSET = 100.0f
        val rnd = Random(0)
    }
}