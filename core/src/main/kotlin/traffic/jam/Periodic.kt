package traffic.jam

class PeriodicAction(private val delay: Long, private val action: () -> Unit) {
    private var next = 0L
    fun act(): Boolean {
        return if (next < System.currentTimeMillis()) {
            next = System.currentTimeMillis() + delay
            action.invoke()
            true
        } else {
            false
        }
    }
}