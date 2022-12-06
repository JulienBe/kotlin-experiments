package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray

/**
 * Due to the necessity of coherent effects, it will be easier to handle drawing and triggering effects from this class
 * Gum's particles will still handle the basic behavior of coming back to the base state
 */
class GumField(gumPerW: Int, gumPerH: Int) {

    private val mergeList = GdxArray<Gum>()
    private var selectedGum: Gum? = null
    private var dirty = true
    private val mergePeriodic = PeriodicAction(MERGE_CHECK_DELAY) { lookForMerges() }
    private val dropDownPeriodic = PeriodicAction(DROP_DOWN_DELAY) { lookForDropDown() }
    private val gumArray = GdxArray<Gum>(gumPerW * gumPerH)

    init {
        for (x in 0 until gumPerW) {
            for (y in 0 until gumPerH) {
                gumArray.add(Gum.obtain(x * Gum.dim.w, y * Gum.dim.h, this))
            }
        }
        MatchPattern.values().forEach {
            println("Pattern: ${it.name}")
            it.offsets.forEach { println(it) }
        }
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        gumArray.forEach { it.state.act(it, batch, image) }
        selectedGum?.outlinePos?.forEach { batch.draw(image, selectedGum!!.pos, it) }
    }

    private fun getGum(x: Float, y: Float): Gum? = gumArray.firstOrNull { it.pos.contains(x, y, Gum.dim) }

    fun clicked(xClick: Float, yClick: Float) {
        val gum = getGum(xClick, yClick)
        if (gum != null) {
            selectedGum = if (selectedGum == gum) {     // Clicked on the same gum, unselect
                null
            } else if (selectedGum != null) {           // Clicked on a different gum, try to merge
                swapGums(selectedGum!!, gum)
                null
            } else {                                    // Clicked on a gum, select
                gum
            }
        }
    }

    private fun swapGums(it: Gum, other: Gum) {
        val oldPos = it.pos.copy()
        it.pos.update(other.pos)
        other.pos.update(oldPos)
        dirty = true
    }

    fun mergeCheck() {
        if (dirty)
            mergePeriodic.act()
    }

    private fun lookForDropDown() {
        for (i in 0 until gumArray.size) {
            val gum = gumArray[i]
            if (gum.state.mergeable && gum.isWithinField(Main.screenDim)) {
                val downGum = getGum(gum.centerX, gum.centerY - Gum.dim.hf)
                if (gum != null && downGum != null && downGum.state == GumState.MERGED) {
                    gum.updateState(GumState.MOVING)
                    gum.pos.update(downGum.pos)
                    gumArray.removeValue(downGum, true)
                    break
                }
            }
        }
    }

    private fun lookForMerges() {
        dirty = false
        // maybe sort it to do the break in a coherent way. Would also help with proximity checks
        // no stream because of the break
        // could always do a dirty based on the gum if that becomes a problem
        for (i in 0 until gumArray.size) {
            val gum = gumArray[i]
            if (gum.state.mergeable && gum.isWithinField(Main.screenDim)) {
                MatchPattern.values().forEach { pattern ->
                    if (checkPattern(pattern, gum)) {
                        dirty = true
                        return
                    }
                }
            }
        }
    }

    /**
     * Current gum is expected to be mergeable and within the field
     * Checks if the pattern matches the current gum
     * @return true if the pattern matches, false otherwise
     */
    private fun checkPattern(pattern: MatchPattern, currentGum: Gum): Boolean {
        pattern.offsets.forEach { offset ->
            mergeList.clear()
            val allMatches = offset.posses.all { pos ->
                val otherGum = getGum(currentGum.centerX + pos.xf, currentGum.centerY + pos.yf)
                if (shouldMerge(currentGum, otherGum)) {
                    mergeList.add(otherGum!!)
                    true
                } else {
                    false
                }
            }
            if (allMatches) {
                mergeList.add(currentGum)
                mergeList.forEach { it.updateState(GumState.MERGING) }
                return true
            }
        }
        return false
    }

    /**
     * Origin is expected to be mergeable and within the field
     */
    private fun shouldMerge(origin: Gum, other: Gum?): Boolean {
        return other != null && other.isWithinField(Main.screenDim) && other.shades == origin.shades && other.state.mergeable
    }

    fun dropDownCheck() {
//        dropDownPeriodic.act()
    }

    companion object {
        const val MERGE_CHECK_DELAY = 1000L
        const val DROP_DOWN_DELAY = 100L
    }
}

private fun SpriteBatch.draw(texture: Texture, pos: Pos, offset: Pos) = draw(texture, pos.xf + offset.xf, pos.yf + offset.yf, 1f, 1f)