package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray
import traffic.jam.Main.Companion.gumPerH
import traffic.jam.Main.Companion.gumPerW

/**
 * Due to the necessity of coherent effects, it will be easier to handle drawing and triggering effects from this class
 * Gum's particles will still handle the basic behavior of coming back to the base state
 */
class GumField(gumPerW: Int, gumPerH: Int) {

    private val mergeList = GdxArray<Gum>()
    private var selectedCell: Cell? = null
    private var dirty = true
    private val mergePeriodic = PeriodicAction(MERGE_CHECK_DELAY) { lookForMerges() }
    private val dropDownPeriodic = PeriodicAction(DROP_DOWN_DELAY) { lookForDropDown() }
    private val cells = Array(gumPerW * gumPerH) {
        val x = it % gumPerW
        val y = it / gumPerW
        Cell(x, y, it).setGum(Gum.obtain(x * Gum.dim.w, y * Gum.dim.h, this))
    }

    init {
        MatchPattern.values().forEach {
            println("Pattern: ${it.name}")
            it.offsets.forEach { println(it) }
        }
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        cells.forEach { it.draw(batch, image) }
        selectedCell?.drawOutline(batch, image)
    }

    fun clicked(xClick: Float, yClick: Float) {
        val clickedCell = getCell((xClick / Gum.dim.wf).toInt(), (yClick / Gum.dim.hf).toInt())
        val clickedGum = clickedCell.getGum
        if (clickedGum != null) {
            selectedCell = if (selectedCell == clickedCell) {     // Clicked on the same gum, unselect
                null
            } else if (selectedCell != null) {           // Clicked on a different gum, try to merge
                swapGums(selectedCell!!, clickedCell)
                null
            } else {                                    // Clicked on a gum, select
                clickedCell
            }
        }
    }

    private fun isValidCell(x: Int, y: Int): Boolean = x in 0 until gumPerW && y in 0 until gumPerH
    private fun getCell(x: Int, y: Int): Cell = cells[x + y * gumPerW]

    private fun swapGums(it: Cell, other: Cell) {
        val itGum = it.getGum!!
        val otherGum = other.getGum!!
        val oldPos = itGum.pos.copy()
        itGum.pos.update(otherGum.pos)
        otherGum.pos.update(oldPos)
        it.setGum(otherGum)
        other.setGum(itGum)
        dirty = true
    }

    fun mergeCheck() {
        if (dirty)
            mergePeriodic.act()
    }

    private fun lookForDropDown() {
        for (i in gumPerW until cells.size) {
            val cell = cells[i]
            val gum = cell.getGum
            if (gum != null && gum.state.mergeable) {
                val downCell = getCell(cell.x, cell.y - 1)
                if (downCell.getGum == null || downCell.getGum!!.state == GumState.MERGING) {
                    gum.updateState(GumState.MOVING)
                    gum.pos.update(gum.pos.x, gum.pos.y - Gum.dim.h)
                    cell.setGum(null)
                    downCell.setGum(gum)
                    dirty = true
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
        cells.forEach { cell ->
            val gum = cell.getGum
            if (gum != null && gum.state.mergeable && gum.isWithinField(Main.screenDim)) {
                MatchPattern.values().forEach { pattern ->
                    if (checkPattern(pattern, cell)) {
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
    private fun checkPattern(pattern: MatchPattern, currentCell: Cell): Boolean {
        val gum = currentCell.getGum!!
        pattern.offsets.forEach { offset ->
            mergeList.clear()
            val allMatches = offset.pairs.all { p ->
                val otherX = currentCell.x + p.first
                val otherY = currentCell.y + p.second
                if (!isValidCell(otherX, otherY))
                    return false
                val otherGum = getCell(otherX, otherY).getGum
                if (shouldMerge(gum, otherGum)) {
                    mergeList.add(otherGum!!)
                    true
                } else {
                    false
                }
            }
            if (allMatches) {
                mergeList.add(gum)
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
        dropDownPeriodic.act()
    }

    companion object {
        const val MERGE_CHECK_DELAY = 1000L
        const val DROP_DOWN_DELAY = 100L
    }
}