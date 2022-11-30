package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*

class Main : ApplicationAdapter() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var image: Texture
    private lateinit var graphics: Graphics

    private val gumField = GumField(gumPerW, gumPerH)
    var state = State.START

    override fun create() {
        image = Texture("square.png")
        Gdx.input.inputProcessor = InputHandler(this)
        graphics = Graphics()
        ratio = Dimension(dim.wf / Gdx.graphics.width, dim.hf / Gdx.graphics.height)
    }

    override fun render() {
        graphics.draw {
            when (state) {
                State.START -> {
//                    drawText("Press space to start", 0f, 0f)
                }
                State.PLAY -> {
                    gumField.mergeCheck()
                    gumField.draw(graphics.batch, image)
                }
                State.PAUSE -> {
//                    drawText("Press space to resume", 0f, 0f)
                    gumField.draw(graphics.batch, image)
                }
                State.GAME_OVER -> {
//                    drawText("Press space to restart", 0f, 0f)
                }
            }
        }
    }

    fun clicked(xClick: Float, yClick: Float) {
        gumField.clicked(xClick, yClick)
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
        val gumPerW = dim.w / Gum.dim.w
        val gumPerH = dim.h / Gum.dim.h
    }
}