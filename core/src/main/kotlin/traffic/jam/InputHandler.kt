package traffic.jam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import traffic.jam.State.*

class InputHandler(val main: Main) : InputAdapter() {

    private var nextAllowedClick = 0L

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.SPACE) {
            when (main.state) {
                START       -> main.newState(PLAY)
                PLAY        -> main.newState(PAUSE)
                PAUSE       -> main.newState(PLAY)
                GAME_OVER   -> main.newState(START)
            }
        }
        return super.keyDown(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (nextAllowedClick < System.currentTimeMillis()) {
            nextAllowedClick = System.currentTimeMillis() + CLICK_DELAY
            main.clicked(Gdx.input.xClick(), Gdx.input.yClick())
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    companion object {
        const val CLICK_DELAY = 10L
    }
}
private fun Input.xClick(): Float = x * Main.ratio.wf
private fun Input.yClick(): Float = (Gdx.graphics.height - y) * Main.ratio.hf

