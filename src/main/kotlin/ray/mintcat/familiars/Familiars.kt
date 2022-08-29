package ray.mintcat.familiars

import ray.mintcat.familiars.impl.data.FamiliarsData
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@RuntimeDependencies(
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    )
)
object Familiars : Plugin() {

    val data = ConcurrentHashMap<String, FamiliarsData>()

    fun getData(uuid: UUID, id: String): FamiliarsData? {
        return data["${uuid}::${id}"]
    }


}