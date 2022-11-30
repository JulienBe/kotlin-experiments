package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*

class Main : ApplicationAdapter() {
    private val gums = mutableListOf<Gum>()
    private var selectedGum = Gum.none
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var image: Texture
    private lateinit var graphics: Graphics

    var state = State.START

    override fun create() {
        image = Texture("square.png")
        Gdx.input.inputProcessor = InputHandler(this)
        graphics = Graphics()
        ratio = Dimension(dim.wf / Gdx.graphics.width, dim.hf / Gdx.graphics.height)
        for (x in 0..dim.w step Gum.dim.w)
            for (y in 0..dim.h step Gum.dim.h)
                gums.add(Gum.obtain(x, y))
    }

    override fun render() {
        graphics.draw {
            when (state) {
                State.START -> {
//                    drawText("Press space to start", 0f, 0f)
                }
                State.PLAY -> {
                    gums.forEach { gum ->
                        gum.draw(graphics.batch, image)
                    }
                    if (selectedGum != Gum.none)
                        selectedGum.drawSelected(graphics.batch, image)
                }
                State.PAUSE -> {
//                    drawText("Press space to resume", 0f, 0f)
                    for (gum in gums) {
                        gum.draw(graphics.batch, image)
                    }
                }
                State.GAME_OVER -> {
//                    drawText("Press space to restart", 0f, 0f)
                }
            }
        }
    }

    fun clicked(xClick: Float, yClick: Float) {
        for (gum in gums) {
            val clicked = gum.clickDetect(xClick, yClick) {
                selectedGum = if (selectedGum == it) {
                    Gum.none
                } else if (selectedGum != Gum.none) {
                    val itPos = it.posCopy()
                    it.swapTo(selectedGum.posCopy())
                    selectedGum.swapTo(itPos)
                    Gum.none
                } else {
                    it
                }
            }
            if (clicked) break
        }
    }

    override fun dispose() {
        scope.cancel()
        graphics.dispose()
        image.dispose()
    }

    fun newState(state: State) {
        this.state = state
    }

    companion object {
        const val SCALE = 5
        val dim: Dimension = Dimension(160, 144)
        var ratio: Dimension = Dimension(160, 144)
    }
}