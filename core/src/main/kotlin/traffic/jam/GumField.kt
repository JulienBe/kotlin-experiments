package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray

class GumField(val gumPerW: Int, val gumPerH: Int) {

    private val fieldGums = Array(gumPerW * gumPerH) { Gum.none }
    private val mergeList = GdxArray<Gum>()
    private var selectedGum = Gum.none
    private var dirty = true
    private var nextMergeCheck = 0L

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
            val clicked = gum.clickDetect(xClick, yClick) {
                selectedGum = if (selectedGum == it) {
                    Gum.none
                } else if (selectedGum != Gum.none) {
                    swapGums(it)
                    Gum.none
                } else {
                    it
                }
            }
            if (clicked) break
        }
    }

    private fun swapGums(it: Gum) {
        fieldGums[it.i] = selectedGum
        fieldGums[selectedGum.i] = it
        it.swapWith(selectedGum)
        dirty = true
    }

    private fun shouldMerge(origin: Gum, x: Int, y: Int): Boolean =
        x in 0..Main.gumPerW &&
        y in 0..Main.gumPerH &&
        gum(x, y).sameTypeAs(origin) &&
        gum(x, y).mergeableState()

    private fun index(x: Int, y: Int) = x * gumPerH + y
    private fun gum(x: Int, y: Int) = fieldGums[index(x, y)]

    fun mergeCheck() {
        if (dirty && System.currentTimeMillis() > nextMergeCheck) {
            nextMergeCheck = System.currentTimeMillis() + mergeCheckDelay
            dirty = false
            // it will go through each gum, and resolve the first 'merge' that is found.
            // The iterative approach is there to make cascading effects more apparent.
            // As the array is iterated from the bottom to the top, lower merges are prioritized
            for (y in 0 until gumPerH) {
                for (x in 0 until gumPerW) {
                    val currentGum = gum(x, y)
                    if (!currentGum.mergeableState()) continue

                    mergeList.clear()
                    mergeList.add(currentGum)
                    // iterate until it doesn't match
                    var leftCheck = -1
                    while (shouldMerge(currentGum,x + leftCheck, y)) {
                        mergeList.add(gum(x + leftCheck, y))
                        leftCheck--
                    }
                    // found any match
                    if (mergeList.size > 1) {
                        merge(mergeList)
                        dirty = true
                        return
                    }
                }
            }
        }
    }

    private fun merge(mergeList: GdxArray<Gum>) {
        mergeList.forEach {
            fieldGums[it.i].beginMerge()
        }
    }

    companion object {
        const val mergeCheckDelay = 1000L
    }
}