package ray.mintcat.familiars.utils

import java.util.concurrent.ConcurrentHashMap

object Cooldown {

    private val map = ConcurrentHashMap<String, Long>()

    fun check(key: String, tick: Long): Boolean {
        val now = System.currentTimeMillis()
        if (map[key] == null) {
            map[key] = now + (tick * 1000 / 20)
            return true
        }
        if (map[key]!! <= now) {
            map.remove(key)
            return true
        }
        return false
    }

}