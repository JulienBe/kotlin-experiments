package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

enum class GumState(val mergeable: Boolean, val downable: Boolean, val act: (Gum, SpriteBatch, Texture) -> Unit) {
    APPEARING   (false, false, { _, _, _ -> }),
    DISAPPEARING(false, false, { _, _, _ -> }),
    MOVING      (false, true,   Gum::moving),
    IN_GAME     (true,  true,   Gum::inGame),
    MERGING     (false, false,  Gum::merging),
    TO_COLLECT      (false, false, { _, _, _ -> });
}