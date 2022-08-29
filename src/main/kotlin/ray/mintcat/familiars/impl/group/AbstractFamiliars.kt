package ray.mintcat.familiars.impl.group

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import ray.mintcat.familiars.Familiars
import ray.mintcat.familiars.impl.data.FamiliarsData

interface AbstractFamiliars {

    val id: String

    fun create(livingEntity: LivingEntity): FamiliarsData?

    fun run(livingEntity: LivingEntity) {
        if (get(livingEntity) == null) {
            register(livingEntity)
        }
        val getter = get(livingEntity) ?: return
        if (livingEntity is Player && !livingEntity.isOnline) {
            getter.delete()
        }
        if (getter.delete) {
            Familiars.data.remove("${livingEntity.uniqueId}::${id}")
        } else {
            getter.update()
        }
    }

    fun get(livingEntity: LivingEntity): FamiliarsData? {
        return Familiars.getData(livingEntity.uniqueId, id)
    }

    fun build(livingEntity: LivingEntity): EntityInstance

    fun register(livingEntity: LivingEntity) {
        val creates = create(livingEntity)
        if (creates != null) {
            Familiars.data["${livingEntity.uniqueId}::${id}"] = creates
        }
    }
}