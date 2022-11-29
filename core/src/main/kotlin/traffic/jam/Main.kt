package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*

class Main : ApplicationAdapter() {
    private val gums = mutableListOf<Gum>()
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var image: Texture
    private lateinit var graphics: Graphics

    var state = State.START

    override fun create() {
        image = Texture("square.png")
        Gdx.input.inputProcessor = InputHandler(this)
        graphics = Graphics()
        for (x in 0..dim.w)
            for (y in 0..dim.h)
                gums.add(Gum.obtain(x, y))
    }

    override fun render() {
        graphics.draw {
            when (state) {
                State.START -> {
//                    drawText("Press space to start", 0f, 0f)
                }
                State.PLAY -> {
                    for (gum in gums) {
                        gum.update()
                        gum.draw(graphics.batch, image)
                    }
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
    }
}