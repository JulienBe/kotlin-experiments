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
    private val mergePeriodic = PeriodicAction(MERGE_CHECK_DELAY) { lookForMerges() }
    private val dropDownPeriodic = PeriodicAction(DROP_DOWN_DELAY) { lookForDropDown() }
    private val wavePeriodic = PeriodicAction(WAVE_DELAY) { waveEffect() }
    private val cells = Array(gumPerW * gumPerH) {
        val x = it % gumPerW
        val y = it / gumPerW
        Cell(x, y, it).setGum(Gum.obtain(x * Gum.dim.w, y * Gum.dim.h, this))
    }
    private val waves = GdxArray<WaveEffect>()

    fun draw(batch: SpriteBatch, image: Texture) {
        wavePeriodic.act()
        cells.forEach { it.draw(batch, image) }
        selectedCell?.drawOutline(batch, image)
    }

    fun clicked(xClick: Float, yClick: Float) {
        waves.add(WaveEffect().init(xClick, yClick, cells
            .mapNotNull { it.getGum }
            .map { it.innerParticles }
            .flatten()))
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
        moveGumTo(otherGum, it)
        moveGumTo(itGum, other)
    }

    fun mergeCheck() {
        mergePeriodic.act()
    }
    private fun waveEffect() {
        waves.forEach { it.act() }
        waves.removeAll { it.isDone }
    }

    private fun lookForDropDown() {
        for (i in gumPerW until cells.size) {
            val cell = cells[i]
            val gum = cell.getGum
            if (gum != null && gum.getState.downable) {
                val downCell = getCell(cell.x, cell.y - 1)
                if (downCell.getGum == null || downCell.getGum!!.getState == GumState.TO_COLLECT) {
                    cell.setGum(null)
                    moveGumTo(gum, downCell)
                    break
                }
            }
        }
    }

    private fun moveGumTo(gum: Gum, cell: Cell) {
        gum.updateState(GumState.MOVING)
        gum.alignParticlesTo(cell.x * Gum.dim.wf, cell.y * Gum.dim.hf)
        cell.setGum(gum)
    }

    private fun lookForMerges() {
//        if (cells.all { it.getGum == null || it.getGum!!.state.mergeable })
            cells.forEach { cell ->
                val gum = cell.getGum
                if (gum != null && gum.getState.mergeable) {
                    MatchPattern.values().forEach { pattern ->
                        if (checkPattern(pattern, cell))
                            return
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
        return other != null && other.shades == origin.shades && other.getState.mergeable
    }

    fun dropDownCheck() {
        dropDownPeriodic.act()
    }

    fun trimField() {

    }

    companion object {
        const val MERGE_CHECK_DELAY = 500L
        const val DROP_DOWN_DELAY = 50L
        const val WAVE_DELAY = 16L
    }
}