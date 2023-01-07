package traffic.jam

import java.util.*

class PeriodicAction(private val delay: Long, rand: Boolean = false, private val action: () -> Unit) {
    private var next = if (rand) rnd.nextInt(delay.toInt()).toLong() else 0L
    fun act(): Boolean {
        return if (next < System.currentTimeMillis()) {
            next = System.currentTimeMillis() + delay
            action.invoke()
            true
        } else {
            false
        }
    }

    companion object {
        val rnd = Random(0)
    }
}