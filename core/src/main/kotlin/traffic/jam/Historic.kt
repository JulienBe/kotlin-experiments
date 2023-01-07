package traffic.jam

@Suppress("UNCHECKED_CAST")
class Historic<T>(private val size: Int) {
    private val array: Array<T?> = arrayOfNulls<Any?>(size) as Array<T?>
    private var index = size

    fun addVal(element: T) {
        array[++index % size] = element
    }

    fun get(index: Int): T {
        return array[(this.index + index) % size]!!
    }
    fun get(): T {
        return array[this.index % size]!!
    }

    fun init(t: T) {
        for (i in 0 until size) {
            addVal(t)
        }
    }
}