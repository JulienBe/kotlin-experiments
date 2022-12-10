package traffic.jam

import java.util.*

class GumParticle(internal var anchorX: Float, internal var anchorY: Float) {
    var actualX: Float = (rnd.nextGaussian() * Main.dim.wf * 2f).toFloat()
    var actualY: Float = (rnd.nextGaussian() * Main.dim.hf * 2f).toFloat()
    var verticalMove = rnd.nextBoolean()
    var ticks = rnd.nextInt(100)

    init {
        if (rnd.nextBoolean())
            actualX *= -1f
        else
            actualY *= -1f
    }

    var index = Shades.MAX_COLOR_INDEX

    companion object {
        const val INIT_OFFSET = 100.0f
        val rnd = Random(0)
    }
}