package traffic.jam

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import traffic.jam.State.*

class InputHandler(val main: Main) : InputAdapter() {
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
}