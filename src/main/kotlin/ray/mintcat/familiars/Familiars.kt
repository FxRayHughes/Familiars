package ray.mintcat.familiars

import ray.mintcat.familiars.impl.data.FamiliarsData
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Familiars : Plugin() {

    val data = ConcurrentHashMap<String, FamiliarsData>()

    fun getData(uuid: UUID, id: String): FamiliarsData? {
        return data["${uuid}::${id}"]
    }

    override fun onDisable() {
        data.values.forEach {
            it.delete()
        }
    }

}