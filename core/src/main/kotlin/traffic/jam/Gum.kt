package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.collections.sortBy
import ktx.collections.toGdxArray
import traffic.jam.GumState.*
import kotlin.math.abs

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
    }.toGdxArray()
    private val baseColorPeriodic = PeriodicAction(BASE_COLOR_DELAY) { toBaseColor() }
    private val alignPosPeriodic = PeriodicAction(ALIGN_POS_DELAY) { alignPos() }
    private val toDarkPeriodic = PeriodicAction(TO_DARK_DELAY) { toDark() }
    private lateinit var gumField: GumField
    internal var shades = Shades.rand()
    private var state = MOVING
    val getState: GumState
        get() = state

    fun inGame(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, it.anchorX, it.anchorY, 1f, 1f)
        }
        baseColorPeriodic.act()
    }

    fun moving(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, it.actualX, it.actualY, 1f, 1f)
        }
        baseColorPeriodic.act()
        alignPosPeriodic.act()
        if (innerParticles.all { abs(it.actualX - it.anchorX) < 1f && abs(it.actualY - it.anchorY) < 1f }) {
            updateState(IN_GAME)
        }
    }

    fun merging(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, it.actualX, it.actualY, 1f, 1f)
        }
        toDarkPeriodic.act()
        if (innerParticles.all { it.index == Shades.MAX_COLOR_INDEX }) {
            updateState(TO_COLLECT)
        }
    }

    private fun changeByPatches(numberToActOn: Int, termination: (GumParticle) -> Boolean, action: (GumParticle) -> Unit) {
        var i = 0
        var p = innerParticles.random()
        while (termination.invoke(p) && i++ < 10) {
            p = innerParticles.random()
        }
        innerParticles.sortBy {
            Vector2.dst2(it.actualX, it.actualY, p.actualX, p.actualY)
        }
        for (j in 0..numberToActOn) {
            p = innerParticles[j]
            if (!termination.invoke(p))
                action.invoke(p)
        }
    }

    private fun toDark() {
        changeByPatches(10, { it.index == Shades.MAX_COLOR_INDEX }) {
            it.index++
        }
    }

    private fun toBaseColor() {
        for (i in 0..24) {
            val p = innerParticles.random()
            if (p.index > 1) p.index-- else p.index = 1
        }
    }
    private fun alignPos() {
        innerParticles.sortBy {
            -Vector2.dst2(it.actualX, it.actualY, it.anchorX, it.anchorY)
        }
        for (i in 0..23) {
            val p = innerParticles[i]
            if (p.actualX < p.anchorX)  p.actualX -= ((p.actualX - p.anchorX) * 0.12f) - 0.30f
            else                        p.actualX -= ((p.actualX - p.anchorX) * 0.12f) + 0.30f
            if (p.actualY < p.anchorY)  p.actualY -= ((p.actualY - p.anchorY) * 0.16f) - 0.28f
            else                        p.actualY -= ((p.actualY - p.anchorY) * 0.16f) + 0.28f
        }
    }

    fun alignParticlesTo(x: Float, y: Float) {
        innerParticles.forEachIndexed { index, gumParticle ->
            gumParticle.anchorX = x + (index % innerDim.wf + 1f)
            gumParticle.anchorY = y + (index / innerDim.hf + 1f)
        }
    }

    fun updateState(newState: GumState) {
        state = newState
    }

    companion object {
        const val BASE_COLOR_DELAY = 16L
        const val ALIGN_POS_DELAY = 16L
        const val TO_DARK_DELAY = 24L
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
            g.alignParticlesTo(x.toFloat(), y.toFloat())
            g.updateState(MOVING)
            g.gumField = gumField
            return g
        }
    }
}