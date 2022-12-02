package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray

/**
 * Due to the necessity of coherents effects, it will be easier to handle drawing and triggering effects from this class
 * Gum's particles will still handle the basic behavior of coming back to the base state
 */
class GumField(private val gumPerW: Int, private val gumPerH: Int) {

    private val fieldGums = Array(gumPerW * gumPerH) { Gum.none }
    private val mergeList = GdxArray<Gum>()
    private var selectedGum = Gum.none
    private var dirty = true
    private val mergePeriodic = PeriodicAction(MERGE_CHECK_DELAY) { lookForMerges() }
    private val dropDownPeriodic = PeriodicAction(DROP_DOWN_DELAY) { dropDown() }

    init {
        for (y in 0 until gumPerH)
            for (x in 0 until gumPerW)
                fieldGums[index(x, y)] = Gum.obtain(x * Gum.dim.w, y * Gum.dim.h, index(x, y))
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        fieldGums.forEach {
            it.draw(batch, image)
        }
        if (selectedGum != Gum.none)
            selectedGum.drawSelected(batch, image)
    }

    fun clicked(xClick: Float, yClick: Float) {
        for (gum in fieldGums) {
            if (gum.clickDetect(xClick, yClick)) {
                if (selectedGum == gum) {
                    selectedGum = Gum.none
                } else if (selectedGum != Gum.none) {
                    swapGums(gum)
                    selectedGum = Gum.none
                } else {
                    selectedGum = gum
                    gum.innerParticles.forEach {
                        it.index = Shades.MAX_COLOR_INDEX
                    }
                    gum.currentParticleActIndex = 0
                }
                break
            }
        }
    }

    private fun swapGums(it: Gum) {
        fieldGums[it.i] = selectedGum
        fieldGums[selectedGum.i] = it
        it.swapWith(selectedGum)
        dirty = true
    }

    private fun shouldMerge(origin: Gum, x: Int, y: Int): Boolean =
        x in 0 until Main.gumPerW &&
        y in 0 until Main.gumPerH &&
        gum(x, y).sameTypeAs(origin) &&
        gum(x, y).mergeableState()

    private fun index(x: Int, y: Int) = x * gumPerH + y
    private fun gum(x: Int, y: Int) = fieldGums[index(x, y)]

    fun mergeCheck() {
        if (dirty)
            mergePeriodic.act()
    }

    private fun lookForMerges() {
        dirty = false
        // it will go through each gum, and resolve the first 'merge' that is found.
        // The iterative approach is there to make cascading effects more apparent.
        // As the array is iterated from the bottom to the top, lower merges are prioritized
        for (y in 0 until gumPerH) {
            for (x in 0 until gumPerW) {
                MatchPattern.values().forEach { pattern ->
                    if (checkPattern(pattern, gum(x, y), x, y)) {
                        dirty = true
                        return
                    }
                }
            }
        }
    }

    private fun dropDown() {
    }

    /**
     * Checks if the pattern matches the current gum
     * @return true if the pattern matches, false otherwise
     */
    private fun checkPattern(pattern: MatchPattern, currentGum: Gum, x: Int, y: Int): Boolean {
        if (!currentGum.mergeableState())
            return false
        pattern.offsets.forEach { offsetsSetHaHa ->
            val noMatch = offsetsSetHaHa.any {
                !shouldMerge(currentGum, x + it.x, y + it.y)
            }
            if (!noMatch) {
                mergeList.clear()
                mergeList.add(currentGum)
                offsetsSetHaHa.forEach {
                    mergeList.add(gum(x + it.x, y + it.y))
                }
                merge(mergeList)
                return true
            }
        }
        return false
    }

    private fun merge(mergeList: GdxArray<Gum>) {
        mergeList.forEach {
            fieldGums[it.i].beginMerge()
        }
    }

    companion object {
        const val MERGE_CHECK_DELAY = 1000L
        const val DROP_DOWN_DELAY = 1000L
    }
}