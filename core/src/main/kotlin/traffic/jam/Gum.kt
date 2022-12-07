package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import traffic.jam.GumState.*

class Gum private constructor() {


    /**
     * Maybe the state function could actually be just a bunch of periodic that would be different based on the state
     */
    internal val outlinePos = List((outlineDim.w) * 4) {
        if (it < outlineDim.w)
            Pos(it.toFloat(), 0f)                    // bottom
        else if (it < outlineDim.w * 2)
            Pos(outlineDim.wf, (it - outlineDim.w).toFloat())         // right
        else if (it < outlineDim.w * 3)
            Pos(outlineDim.wf - (it - (outlineDim.w * 2)), outlineDim.wf)
        else
            Pos(0f, outlineDim.wf - (it - (outlineDim.w * 3)))
    }
    internal val innerParticles = List(innerDim.w * innerDim.h) {
        GumParticle(it % innerDim.wf + 1f, it / innerDim.hf + 1f)
    }
    private val particlePeriodic = PeriodicAction(PARTICLE_DELAY) { particleAct() }
    private val alignPosPeriodic = PeriodicAction(ALIGN_POS_DELAY) { alignPos() }
    private var currentParticleActIndex = 0
    private lateinit var gumField: GumField
    internal var shades = Shades.rand()
    internal var pos: Pos = Pos(0f, 0f)
    internal var state = MOVING
    private val centerX: Float
        get() = pos.xf + dim.hwF
    private val centerY: Float
        get() = pos.yf + dim.hhF

    fun inGame(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, pos.xf + it.anchorX, pos.yf + it.anchorY, 1f, 1f)
        }
        particlePeriodic.act()
    }

    fun moving(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, pos.xf + it.anchorX + it.offsetX, pos.yf + it.anchorY + it.offsetY, 1f, 1f)
        }
        particlePeriodic.act()
        alignPosPeriodic.act()
    }

    private fun particleAct() {
        for (i in 0..6) {
            val p = innerParticles[currentParticleActIndex++ % innerParticles.size]
            if (p.index > 1) p.index--
        }
    }
    private fun alignPos() {
        innerParticles.forEach {
//            if (it.verticalMove) {
                it.offsetY *= 0.97f
//            } else {
                it.offsetX *= 0.97f
//            }
//            if (it.ticks++ > 60) {
//                it.verticalMove = !it.verticalMove
//                it.ticks = 0
//            }
        }
        if (innerParticles.all { it.offsetX < 0.3f && it.offsetY < 0.3f }) {
            updateState(IN_GAME)
        }
    }

    fun updateState(newState: GumState) {
        state = newState
    }

    fun isWithinField(d: Dimension): Boolean = centerX > 0f && pos.xf <= d.wf && centerY > 0f && pos.yf <= d.hf

    companion object {
        const val PARTICLE_DELAY = 16L
        const val ALIGN_POS_DELAY = 16L
        val dim = Dimension(16)
        val outlineDim = Dimension(dim.w - 1)
        val innerDim = Dimension(outlineDim.w - 1)

        private val pool: Pool<Gum> = object : Pool<Gum>() {
            override fun newObject(): Gum {
                return Gum()
            }
        }
        fun obtain(x: Int, y: Int, gumField: GumField): Gum {
            val g =  pool.obtain()
            g.pos.update(x, y)
            g.updateState(MOVING)
            g.gumField = gumField
            return g
        }
    }
}