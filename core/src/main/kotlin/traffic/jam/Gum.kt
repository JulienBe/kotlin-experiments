package traffic.jam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.collections.sortBy
import ktx.collections.toGdxArray
import traffic.jam.GumParticle.Companion.downIter
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
    private val scramblePeriodic = PeriodicAction(SCRAMBLE_DELAY, true) { innerParticles.random() }
    private lateinit var gumField: GumField
    internal var shades = Shades.rand()
    private var state = MOVING
    val getState: GumState
        get() = state

    fun inGame(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.i].f
            batch.draw(image, it.anchorX, it.anchorY, 1f, 1f)
        }
        baseColorPeriodic.act()
    }

    fun moving(batch: SpriteBatch, image: Texture) {
        downIter.forEach { i ->
            innerParticles.forEach { gum ->
                batch.packedColor = shades.colors[Shades.TARGET_COLOR + i].f
                batch.draw(image, gum.actualX.get(-i), gum.actualY.get(-i), 1f, 1f)
            }
        }
        alignPosPeriodic.act()
        scramblePeriodic.act()
    }

    fun merging(batch: SpriteBatch, image: Texture) {
        toDarkPeriodic.act()
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.i].f
            batch.draw(image, it.actualX.get(), it.actualY.get(), 1f, 1f)
        }
        if (innerParticles.all { it.i == Shades.MAX_COLOR_INDEX }) {
            updateState(TO_COLLECT)
        }
    }

    private fun changeByPatches(numberToActOn: Int, termination: (GumParticle) -> Boolean, action: (GumParticle) -> Unit) {
        var i = 0
        var p = innerParticles.random()
        while (termination.invoke(p) && i++ < 20) {
            p = innerParticles.random()
        }
        innerParticles.sortBy {
            Vector2.dst2(it.actualX.get(), it.actualY.get(), p.actualX.get(), p.actualY.get())
        }
        for (j in 0..numberToActOn) {
            p = innerParticles[j]
            if (!termination.invoke(p))
                action.invoke(p)
        }
    }

    private fun toDark() {
        changeByPatches(40, { it.i == Shades.MAX_COLOR_INDEX }) {
            it.index.addVal(it.i + 1)
        }
    }
    private fun toBaseColor() {
        changeByPatches(24, { it.i == 1 } ) {
            if (it.i > 1) it.index.addVal(it.i - 1) else it.index.addVal(1)
        }
    }
    
    private fun alignPos() {
//        val first = innerParticles
//        innerParticles.forEach { p ->
        var allSet = true
        for (i in 0..innerParticles.size / 2) {
//            val p = innerParticles.random()
            val p = innerParticles[i]
            var newX = p.actualX.get() + (p.anchorX - p.actualX.get()) / 40f
            var newY = p.actualY.get() + (p.anchorY - p.actualY.get()) / 40f
            if (p.actualX.get() < p.anchorX)
                newX += 1f
            else
                newX -= 1f
            if (p.actualY.get() < p.anchorY)
                newY += 1f
            else
                newY -= 1f
            if (abs(p.actualX.get() - p.anchorX) < 1f)
                newX = p.anchorX
            else
                allSet = false
            if (abs(p.actualY.get() - p.anchorY) < 1f)
                newY = p.anchorY
            else
                allSet = false
            p.actualX.addVal(newX)
            p.actualY.addVal(newY)
        }
        if (allSet)
            innerParticles.reverse()
//        innerParticles.first().actualX.add(innerParticles.first().anchorX)
//        innerParticles.first().actualY.add(innerParticles.first().anchorY)
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
        const val SCRAMBLE_DELAY = 500L
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
            g.innerParticles.sortBy {
                Vector2.dst2(it.actualX.get(), it.actualY.get(), it.anchorX, it.anchorY)
            }
            g.gumField = gumField
            return g
        }
    }
}